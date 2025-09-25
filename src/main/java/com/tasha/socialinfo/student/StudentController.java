package com.tasha.socialinfo.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<Page<StudentDto>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) List<Long> fieldIds,
            @RequestParam(required = false) List<String> values
    ) {

        Pageable pageable = PageRequest.of(page, limit);
        Page<StudentDto> students = studentService.getAllStudents(pageable, fieldIds, values);

        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentInfoDto> getStudentById(@PathVariable Long id) {
        StudentInfoDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferStudents(
            @RequestBody TransferStudentsRequest transferRequest
    ) {
        studentService.transferStudents(transferRequest.groupId(), transferRequest.studentIds());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(
            @RequestBody StudentRequest student,
            Authentication authentication
    ) {
        StudentDto createdStudent = studentService.createStudent(student, authentication.getName());
        return ResponseEntity.ok(createdStudent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentInfoDto> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentInfoRequest student,
            Authentication authentication
    ) {
        StudentInfoDto updatedStudent = studentService.updateStudent(id, student, authentication.getName());
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteStudentBatch(@RequestBody List<Long> studentIds) {
        studentService.deleteStudentBatch(studentIds);
        return ResponseEntity.noContent().build();
    }
}
