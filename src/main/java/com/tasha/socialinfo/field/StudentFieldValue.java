package com.tasha.socialinfo.field;

import com.tasha.socialinfo.student.Student;
import jakarta.persistence.*;

@Entity
@Table(name = "student_field_values")
public class StudentFieldValue {
    @EmbeddedId
    private StudentFieldId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fieldId")
    @JoinColumn(name = "field_id")
    private Field field;

    private String value;

    public StudentFieldValue() {}

    public StudentFieldValue(Student student, Field field, String value) {
        this.student = student;
        this.field = field;
        this.value = value;
        this.id = new StudentFieldId(student.getId(), field.getId());
    }

    public StudentFieldId getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Student getStudent() {
        return student;
    }

    public Field getField() {
        return field;
    }
}
