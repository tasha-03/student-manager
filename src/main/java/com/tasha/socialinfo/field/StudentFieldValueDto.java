package com.tasha.socialinfo.field;

import java.util.List;

public record StudentFieldValueDto(
        Long fieldId,
        String fieldName,
        FieldType fieldType,
        List<String> validValues,
        String value
) {}
