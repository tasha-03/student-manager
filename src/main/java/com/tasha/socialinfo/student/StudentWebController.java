package com.tasha.socialinfo.student;

import com.tasha.socialinfo.field.Field;
import com.tasha.socialinfo.field.FieldRepository;
import com.tasha.socialinfo.field.FieldService;
import com.tasha.socialinfo.group.GroupCategoryDto;
import com.tasha.socialinfo.group.GroupCategoryService;
import com.tasha.socialinfo.group.GroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentWebController {
    private final StudentService studentService;
    private final GroupCategoryService groupCategoryService;
    private final FieldService fieldService;

    public StudentWebController(StudentService studentService, GroupService groupService, GroupCategoryService groupCategoryService, FieldRepository fieldRepository, FieldService fieldService) {
        this.studentService = studentService;
        this.groupCategoryService = groupCategoryService;
        this.fieldService = fieldService;
    }

    @GetMapping
    public String listStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) List<Long> fieldIds,
            @RequestParam(required = false) List<String> values,
            Model model) {

        System.out.println(fieldIds);
        System.out.println(values);

        Pageable pageable = PageRequest.of(page, limit);
        Page<StudentDto> students = studentService.getAllStudents(pageable, fieldIds, values);
        List<GroupCategoryDto> categories = groupCategoryService.getAllCategoriesWithGroups();
        List<Field> fields = fieldService.getAllFields();

        model.addAttribute("allFields", fields);
        model.addAttribute("students", students.getContent());
        model.addAttribute("recordCount", students.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", students.getTotalPages());
        model.addAttribute("limit", limit);
        model.addAttribute("categories", categories);
        model.addAttribute("fieldIds", fieldIds);
        model.addAttribute("values", values);

        return "students/list";
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        StudentInfoDto student = studentService.getStudentById(id);
        List<GroupCategoryDto> categories = groupCategoryService.getAllCategoriesWithGroups();
        model.addAttribute("student", student);
        model.addAttribute("categories", categories);
        return "students/view";
    }

    @PostMapping("/{id}")
    public String saveStudent(
            @PathVariable Long id,
            @ModelAttribute StudentInfoRequest student,
            Authentication authentication,
            Model model) {

        studentService.updateStudent(id, student, authentication.getName());
        return "redirect:/students";
    }

    @PostMapping("/new")
    public String newStudent(
            @ModelAttribute StudentRequest student,
            Authentication authentication,
            Model model
    ) {
        StudentDto createdStudent = studentService.createStudent(student, authentication.getName());
        return "redirect:/students/" + createdStudent.id();
    }
}
