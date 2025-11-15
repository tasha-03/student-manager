package com.tasha.socialinfo.student;

import com.tasha.socialinfo.field.Field;
import com.tasha.socialinfo.field.FieldService;
import com.tasha.socialinfo.group.GroupCategoryDto;
import com.tasha.socialinfo.group.GroupCategoryService;
import com.tasha.socialinfo.spreadsheet.SpreadsheetMediaType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentWebController {
    private final StudentService studentService;
    private final GroupCategoryService groupCategoryService;
    private final FieldService fieldService;

    public StudentWebController(StudentService studentService,
                                GroupCategoryService groupCategoryService,
                                FieldService fieldService) {
        this.studentService = studentService;
        this.groupCategoryService = groupCategoryService;
        this.fieldService = fieldService;
    }

    @GetMapping
    public String listStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) Long searchGroupId,
            @RequestParam(required = false) List<Long> fieldIds,
            @RequestParam(required = false) List<String> values,
            Authentication authentication,
            Model model) {
        Boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Boolean isSocial = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SOCIAL"));

        Pageable pageable = PageRequest.of(page, limit);

        Page<StudentDto> students = (isAdmin || isSocial)
                ? studentService.getAllStudents(pageable, fieldIds, values, searchGroupId)
                : studentService.getMyStudents(pageable, fieldIds, values, searchGroupId);

        List<GroupCategoryDto> categories = groupCategoryService.getAllCategoriesWithGroups();
        categories.removeIf(cat -> cat.categoryName().equals(groupCategoryService.getDefaultCategoryName()));
        List<Field> fields = fieldService.getAllFields();

        model.addAttribute("allFields", fields);
        model.addAttribute("students", students.getContent());
        model.addAttribute("recordCount", students.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", students.getTotalPages());
        model.addAttribute("limit", limit);
        model.addAttribute("searchGroupId", searchGroupId);
        model.addAttribute("categories", categories);
        model.addAttribute("fieldIds", fieldIds);
        model.addAttribute("values", values);

        return "students/list";
    }

    @GetMapping("/{id}")
    public String viewStudent(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        StudentInfoDto student = studentService.getStudentById(id, authentication.getName());

        List<GroupCategoryDto> categories = groupCategoryService.getAllCategoriesWithGroups();
        categories.removeIf(cat -> cat.categoryName().equals(groupCategoryService.getDefaultCategoryName()));
        model.addAttribute("student", student);
        model.addAttribute("categories", categories);
        return "students/view";
    }

    @PostMapping("/transfer")
    public String transferStudents(
            @ModelAttribute TransferStudentsRequest transferRequest
    ) {
        studentService.transferStudents(transferRequest.groupId(), transferRequest.studentIds());
        return "redirect:/students";
    }

    @PostMapping("/{id}")
    public String saveStudent(
            @PathVariable Long id,
            @ModelAttribute StudentInfoRequest student,
            Authentication authentication,
            Model model) {
        System.out.println(student);
        studentService.updateStudent(id, student, authentication.getName());
        return "redirect:/students";
    }

    @GetMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }

    @PostMapping("/new")
    public String newStudent(
            @ModelAttribute StudentRequest student,
            Authentication authentication
    ) {
        StudentDto createdStudent = studentService.createStudent(student, authentication.getName());
        return "redirect:/students/" + createdStudent.id();
    }

    @GetMapping("/export")
    public void exportData(HttpServletResponse response) throws IOException {
        byte[] file = studentService.getExcel();

        response.setContentType(SpreadsheetMediaType.XLSX.getMimeType());
        response.setHeader("Content-Disposition", "attachment; filename=\"students.xlsx\"");
        response.getOutputStream().write(file);
    }
}
