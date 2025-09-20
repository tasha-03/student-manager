package com.tasha.socialinfo.student;

import com.tasha.socialinfo.field.StudentFieldValueRequest;

import java.time.LocalDate;
import java.util.List;

public record StudentInfoRequest(
        String name,
        LocalDate birthdate,
        Long groupId,
        List<StudentFieldValueRequest> fields
) {}
