package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Faculty;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.payload.GroupDto;
import uz.pdp.appjparelationships.repository.FacultyRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    FacultyRepository facultyRepository;

    //VAZIRLIK UCHUN
    //READ
    @GetMapping
    public List<Group> getGroups() {
        List<Group> groups = groupRepository.findAll();
        return groups;
    }


    //UNIVERSITET MAS'UL XODIMI UCHUN
    @GetMapping("/byUniversityId/{universityId}")
    public List<Group> getGroupsByUniversityId(@PathVariable Integer universityId) {
        List<Group> allByFaculty_universityId = groupRepository.findAllByFaculty_UniversityId(universityId);
        List<Group> groupsByUniversityId = groupRepository.getGroupsByUniversityId(universityId);
        List<Group> groupsByUniversityIdNative = groupRepository.getGroupsByUniversityIdNative(universityId);
        return allByFaculty_universityId;
    }

    @PostMapping
    public String addGroup(@RequestBody GroupDto groupDto) {

        Group group = new Group();
        group.setName(groupDto.getName());

        Optional<Faculty> optionalFaculty = facultyRepository.findById(groupDto.getFacultyId());
        if (!optionalFaculty.isPresent()) {
            return "Such faculty not found";
        }

        group.setFaculty(optionalFaculty.get());

        groupRepository.save(group);
        return "Group added";
    }

    @PutMapping("/{id}")
    public String editGroup(@PathVariable Integer id, @RequestBody GroupDto groupDto){
        if (!groupDto.getName().isEmpty() && groupDto.getFacultyId()!=null){
            Optional<Group> optionalGroup = groupRepository.findById(id);
            if (optionalGroup.isPresent()){
                Group group = optionalGroup.get();
                Optional<Faculty> optionalFaculty = facultyRepository.findById(groupDto.getFacultyId());
                if (optionalFaculty.isPresent()){
                    Faculty faculty = optionalFaculty.get();
                    group.setName(groupDto.getName());
                    group.setFaculty(faculty);
                    groupRepository.save(group);
                    return "Group taxrirlandi!";
                }else {
                    return "Kiritilgan id li faculty topilmadi!";
                }
            }else {
                return "Kiritilgan id li group topilmadi!";
            }
        }
        return "Qatorlarni to'ldiring!";
    }

    @DeleteMapping("/{id}")
    public String deleteGroup(@PathVariable Integer id){
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isPresent()){
            Group group = optionalGroup.get();
            groupRepository.delete(group);
            return "Group o'chirildi!";
        }
        return "Kiritilgan id li group topilmadi!";
    }
}
