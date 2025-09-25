package com.tasha.socialinfo.student;

import java.time.LocalDate;
import java.util.Map;

public record StudentInfoRequest(
        String name,
        LocalDate birthdate,
        Long groupId,
        Map<Long, String> fields
) {}
