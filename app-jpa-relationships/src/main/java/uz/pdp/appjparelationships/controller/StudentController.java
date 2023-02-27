package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.*;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    AddressRepository addressRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
            //1-1=0     2-1=1    3-1=2    4-1=3
            //select * from student limit 10 offset (0*10)
            //select * from student limit 10 offset (1*10)
            //select * from student limit 10 offset (2*10)
            //select * from student limit 10 offset (3*10)
            Pageable pageable = PageRequest.of(page, 10);
            Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
            return studentPage;

    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId, @RequestParam int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        return studentPage;
    }

    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentListForGroup(@PathVariable Integer groupId, @RequestParam int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> allByGroupId = studentRepository.findAllByGroupId(groupId, pageable);
        return allByGroupId;
    }

    @PostMapping
    public String addStudent(@RequestBody StudentDto studentDto){
        if (!studentDto.getFirstName().isEmpty() && !studentDto.getLastName().isEmpty() && !studentDto.getCity().isEmpty()
        && studentDto.getGroupId()!=null && !studentDto.getStreet().isEmpty() && !studentDto.getDistrict().isEmpty()){
            Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
            if (optionalGroup.isPresent()){
                Group group = optionalGroup.get();
                List<Integer> subjectsId = studentDto.getSubjectsId();
                List<Subject> subjects = new ArrayList<>();
                for (Integer subjectId : subjectsId) {
                    Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
                    if (optionalSubject.isPresent()){
                        Subject subject = optionalSubject.get();
                        subjects.add(subject);
                    }else {
                        return "Kiritilgan id li subject topilmadi!";
                    }
                }
                Address address = new Address();
                address.setStreet(studentDto.getStreet());
                address.setDistrict(studentDto.getDistrict());
                address.setCity(studentDto.getCity());
                Address savedAddress = addressRepository.save(address);

                Student student = new Student();
                student.setAddress(savedAddress);
                student.setGroup(group);
                student.setSubjects(subjects);
                student.setFirstName(studentDto.getFirstName());
                student.setLastName(studentDto.getLastName());
                studentRepository.save(student);
                return "Student qo'shildi!";

            }else {
                return "Kiritilgan id li group topilmadi!";
            }
        }
        return "Qatorlarni to'ldiring!";
    }

    @PutMapping("/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()){
            Student student = optionalStudent.get();
            if (!studentDto.getFirstName().isEmpty() && !studentDto.getLastName().isEmpty() && !studentDto.getCity().isEmpty()
                    && studentDto.getGroupId()!=null && !studentDto.getStreet().isEmpty() && !studentDto.getDistrict().isEmpty()){
                Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
                if (optionalGroup.isPresent()){
                    Group group = optionalGroup.get();
                    List<Integer> subjectsId = studentDto.getSubjectsId();
                    List<Subject> subjects = new ArrayList<>();
                    for (Integer subjectId : subjectsId) {
                        Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
                        if (optionalSubject.isPresent()){
                            Subject subject = optionalSubject.get();
                            subjects.add(subject);
                        }else {
                            return "Kiritilgan id li subject topilmadi!";
                        }
                    }

                    student.getAddress().setStreet(studentDto.getStreet());
                    student.getAddress().setDistrict(studentDto.getDistrict());
                    student.getAddress().setCity(studentDto.getCity());
                    addressRepository.save(student.getAddress());

                    student.setAddress(student.getAddress());
                    student.setGroup(group);
                    student.setSubjects(subjects);
                    student.setFirstName(studentDto.getFirstName());
                    student.setLastName(studentDto.getLastName());
                    studentRepository.save(student);
                    return "Student qo'shildi!";

                }else {
                    return "Kiritilgan id li group topilmadi!";
                }
            }
            return "Qatorlarni to'ldiring!";
        }
        return "Kiritilgan id li student topilmadi!";
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()){
            Student student = optionalStudent.get();
            addressRepository.delete(student.getAddress());
            studentRepository.delete(student);
            return "Student o'chirildi!";
        }
        return "Kiritilgan id li student topilmadi!";
    }

}
