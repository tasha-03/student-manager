package com.tasha.socialinfo.student;

import java.time.Instant;
import java.time.LocalDate;

public record StudentDto (
    Long id,
    String name,
    LocalDate birthdate,
    Long groupId,
    String groupCode,
    String createdAt,
    String lastModified,
    String lastModifiedBy,
    String lastModifiedByFullName
) {}
