package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/subject")
public class SubjectController {
    @Autowired
    SubjectRepository subjectRepository;

    //CREATE
    @RequestMapping(method = RequestMethod.POST)
    public String addSubject(@RequestBody Subject subject) {
        boolean existsByName = subjectRepository.existsByName(subject.getName());
        if (existsByName)
            return "This subject already exist";
        subjectRepository.save(subject);
        return "Subject added";
    }

    //READ
//    @RequestMapping(method = RequestMethod.GET)
    @GetMapping
    public List<Subject> getSubjects() {
        List<Subject> subjectList = subjectRepository.findAll();
        return subjectList;
    }

    @GetMapping("/{id}")
    public Subject getSubjectById(@PathVariable Integer id){
        Optional<Subject> optionalSubject = subjectRepository.findById(id);
        if (optionalSubject.isPresent()){
            Subject subject = optionalSubject.get();
            return subject;
        }
        return new Subject();
    }

    @PutMapping("/{id}")
    public String editSubject(@PathVariable Integer id, @RequestBody Subject subject){
        if (!subject.getName().isEmpty()){
            Optional<Subject> optionalSubject = subjectRepository.findById(id);
            if (optionalSubject.isPresent()) {
                Subject subject1 = optionalSubject.get();
                boolean exists = subjectRepository.existsByName(subject.getName());
                if (exists) {
                    return "Kiritilgan nomli subject mavjud, boshqa nom kiriting!";
                } else {
                    subject1.setName(subject.getName());
                    subjectRepository.save(subject1);
                    return "Subject taxrirlandi!";
                }
            }else {
                return "Kiritilgan id li subject topilmadi!";
            }
        }
        return "Subject nomini kiriting!";
    }

    @DeleteMapping("/{id}")
    public String deleteSubject(@PathVariable Integer id){
        Optional<Subject> optionalSubject = subjectRepository.findById(id);
        if (optionalSubject.isPresent()){
            Subject subject = optionalSubject.get();
            subjectRepository.delete(subject);
            return "Subject o'chirildi!";
        }
        return "Kiritilgan id li subject topilmadi!";
    }

}
