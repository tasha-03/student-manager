package com.tasha.socialinfo.student;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class StudentSpecifications {
    public static Specification<Student> hasFieldValue(Long fieldId, String value) {
        return (root, query, cb) -> {
            Join<Object, Object> join = root.join("fieldValues");
            return cb.and(
                    cb.equal(join.get("id").get("fieldId"), fieldId),
                    cb.equal(join.get("value"), value)
            );
        };
    }

    public static Specification<Student> hasFieldValues(List<Long> fieldIds, List<String> values) {
        if (fieldIds == null || values == null || fieldIds.size() != values.size()) {
            return null;
        }

        Specification<Student> spec = null;
        for (int i = 0; i < fieldIds.size(); i++) {
            Specification<Student> next = hasFieldValue(fieldIds.get(i), values.get(i));
            spec = (spec == null) ? next : spec.and(next);
        }

        return spec;
    }
}
