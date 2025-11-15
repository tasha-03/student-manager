package com.tasha.socialinfo.student;

import com.tasha.socialinfo.field.*;
import com.tasha.socialinfo.group.Group;
import com.tasha.socialinfo.group.GroupRepository;
import com.tasha.socialinfo.security.Role;
import com.tasha.socialinfo.spreadsheet.SpreadsheetWriter;
import com.tasha.socialinfo.user.User;
import com.tasha.socialinfo.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
public class StudentService {
    private static final ZoneId MOSCOW = ZoneOffset.ofHours(3);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final StudentFieldValueRepository studentFieldValueRepository;
    private final FieldRepository fieldRepository;

    public StudentService(
            StudentRepository studentRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            StudentFieldValueRepository studentFieldValueRepository,
            FieldRepository fieldRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.studentFieldValueRepository = studentFieldValueRepository;
        this.fieldRepository = fieldRepository;
    }

    private StudentDto toDto(Student student) {
        return new StudentDto(
                student.getId(),
                student.getName(),
                student.getBirthdate(),
                student.getGroup().getId(),
                student.getGroup().getCode(),
                FORMATTER.format(student.getCreatedAt().atZone(MOSCOW)),
                FORMATTER.format(student.getLastModified().atZone(MOSCOW)),
                student.getLastModifiedBy().getLogin(),
                student.getLastModifiedBy().getName()
        );
    }

    private StudentFieldValueDto toDto(StudentFieldValue studentFieldValue) {
        return new StudentFieldValueDto(
                studentFieldValue.getField().getId(),
                studentFieldValue.getField().getName(),
                studentFieldValue.getField().getType(),
                studentFieldValue.getField().getValidValues(),
                studentFieldValue.getValue()
        );
    }

    private StudentInfoDto toInfoDto(Student student) {
        List<StudentFieldValue> fields = studentFieldValueRepository.findByStudent_Id(student.getId());
        return new StudentInfoDto(
                student.getId(),
                student.getName(),
                student.getBirthdate(),
                student.getGroup().getId(),
                student.getGroup().getCode(),
                FORMATTER.format(student.getCreatedAt().atZone(MOSCOW)),
                FORMATTER.format(student.getLastModified().atZone(MOSCOW)),
                student.getLastModifiedBy().getLogin(),
                student.getLastModifiedBy().getName(),
                fields.stream().map(this::toDto).toList()
        );
    }

    public Page<StudentDto> getAllStudents(Pageable pageable, List<Long> fieldIds, List<String> values, Long groupId) {
        Specification<Student> spec = StudentSpecifications.hasFieldValues(fieldIds, values);
        Specification<Student> groupSpec = groupId != null
                ? StudentSpecifications.belongsToGroup(groupRepository.findById(groupId)
                        .orElseThrow(() -> new RuntimeException("Group not found")))
                : null;

        Specification<Student> finalSpec = (spec != null)
                ? spec.and(groupSpec)
                : groupSpec;

        Page<Student> studentPage = (finalSpec != null)
                ? studentRepository.findAll(finalSpec, pageable)
                : studentRepository.findAll(pageable);

        return studentPage.map(this::toDto);
    }

    public Page<StudentDto> getMyStudents(Pageable pageable, List<Long> fieldIds, List<String> values, Long groupId) {
        Specification<Student> spec = StudentSpecifications.hasFieldValues(fieldIds, values);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User curator = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specification<Student> curatorSpec = StudentSpecifications.belongsToCurator(curator);

        Specification<Student> groupSpec = groupId != null
                ? StudentSpecifications.belongsToGroup(groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found")))
                : null;

        Specification<Student> finalSpec = (spec != null)
                ? spec.and(curatorSpec)
                : curatorSpec;

        finalSpec = (groupSpec != null)
                ? groupSpec.and(finalSpec)
                : finalSpec;

        Page<Student> studentPage = studentRepository.findAll(finalSpec, pageable);

        return studentPage.map(this::toDto);
    }

    @Transactional
    public StudentInfoDto getStudentById(Long studentId, String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (user.getRole() != Role.ROLE_ADMIN && user.getRole() != Role.ROLE_SOCIAL) {
            if (student.getGroup().getCurator() != user) {
                throw new RuntimeException("Student not found");
            }
        }

        List<Field> fields = fieldRepository.findAll();


        for (Field field : fields) {
            StudentFieldId id = new StudentFieldId(student.getId(), field.getId());
            studentFieldValueRepository.findById(id)
                    .orElseGet(() -> {
                        StudentFieldValue newValue = new StudentFieldValue(student, field, null);
                        return studentFieldValueRepository.save(newValue);
                    });
        }
        return toInfoDto(student);
    }

    @Transactional
    public StudentDto createStudent(StudentRequest student, String username) {
        Group group = groupRepository.findById(student.groupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User modifiedBy = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student createdStudent = studentRepository.save(new Student(student.name(), student.birthdate(), group, modifiedBy));
        return toDto(createdStudent);
    }

    @Transactional
    public StudentInfoDto updateStudent(Long id, StudentInfoRequest updatedStudent, String username) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Group group = groupRepository.findById(updatedStudent.groupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User modifiedBy = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingStudent.setName(updatedStudent.name());
        existingStudent.setBirthdate(updatedStudent.birthdate());
        existingStudent.setGroup(group);
        existingStudent.setLastModifiedBy(modifiedBy);

        List<StudentFieldValue> existingValues = studentFieldValueRepository.findByStudent_Id(existingStudent.getId());

        for (StudentFieldValue value : existingValues) {
            Field field = fieldRepository.findById(value.getId().getFieldId())
                    .orElseThrow(() -> new RuntimeException("Field id not found: " + value.getId().getFieldId()));
            if (field.getType() == FieldType.ENUM &&
                    !(updatedStudent.fields().get(field.getId()) == null) &&
                    !updatedStudent.fields().get(field.getId()).isBlank() &&
                    !field.getValidValues().contains(updatedStudent.fields().get(field.getId()))
            ) {
                throw new RuntimeException("Value not allowed: " +
                        updatedStudent.fields().get(field.getId()) + " not in " +
                        field.getValidValues().toString());
            }
            value.setValue(updatedStudent.fields().getOrDefault(value.getId().getFieldId(), "false"));
        }

        existingStudent.setLastModified(Instant.now());
        Student savedStudent = studentRepository.save(existingStudent);
        return toInfoDto(savedStudent);
    }

    @Transactional
    public void transferStudents(Long groupId, List<Long> studentIds) {
        Group newGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        List<Student> students = new ArrayList<>();
        for (Long studentId : studentIds) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student id not found: " + studentId));
            student.setGroup(newGroup);
            students.add(student);
        }
        studentRepository.saveAll(students);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found");
        }
        studentFieldValueRepository.deleteAllByStudentIds(List.of(id));
        studentRepository.deleteById(id);
    }

    @Transactional
    public void deleteStudentBatch(List<Long> studentIds) {
        studentFieldValueRepository.deleteAllByStudentIds(studentIds);
        studentRepository.deleteAllByIdInBatch(studentIds);
    }

    public byte[] getExcel() {
        List<StudentInfoDto> students = studentRepository.findAll().stream().map(this::toInfoDto).toList();
        List<String> header = StudentExporter.buildHeader(students);
        List<List<String>> rows = StudentExporter.buildRows(students);

        return SpreadsheetWriter.writeExcel(rows, header);
    }
}
