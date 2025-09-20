package com.tasha.socialinfo.student;

import com.tasha.socialinfo.field.StudentFieldValueDto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record StudentInfoDto(
    Long id,
    String name,
    LocalDate birthdate,
    Long groupId,
    String groupCode,
    String createdAt,
    String lastModified,
    String lastModifiedBy,
    List<StudentFieldValueDto> fields
) {
}
