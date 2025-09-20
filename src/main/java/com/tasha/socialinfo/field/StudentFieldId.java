package com.tasha.socialinfo.field;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class StudentFieldId {
    private Long studentId;
    private Long fieldId;

    public StudentFieldId() {}

    public StudentFieldId(Long studentId, Long fieldId) {
        this.studentId = studentId;
        this.fieldId = fieldId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentFieldId that)) return false;
        return Objects.equals(studentId, that.studentId) &&
                Objects.equals(fieldId, that.fieldId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, fieldId);
    }
}
