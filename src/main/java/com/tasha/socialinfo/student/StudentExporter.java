package com.tasha.socialinfo.student;

import com.tasha.socialinfo.field.StudentFieldValueDto;

import java.util.*;

public class StudentExporter {
    public static List<String> buildHeader(List<StudentInfoDto> students) {
        Set<String> fieldNames = new LinkedHashSet<>();
        for (StudentInfoDto s : students) {
            if (s.fields() != null) {
                for (StudentFieldValueDto f : s.fields()) {
                    fieldNames.add(f.fieldName());
                }
            }
        }

        List<String> header = new ArrayList<>(List.of(
                "ID",
                "Name",
                "Birthdate",
                "GroupId",
                "GroupCode",
                "CreatedAt",
                "LastModified",
                "LastModifiedBy",
                "LastModifiedByFullName"
        ));

        header.addAll(fieldNames);

        return header;
    }

    public static List<List<String>> buildRows(List<StudentInfoDto> students) {
        List<String> dynamicFieldNames = buildHeader(students)
                .subList(9, buildHeader(students).size());

        List<List<String>> rows = new ArrayList<>();

        for (StudentInfoDto s : students) {
            List<String> row = new ArrayList<>();

            row.add(String.valueOf(s.id()));
            row.add(s.name());
            row.add(s.birthdate() != null ? s.birthdate().toString() : "");
            row.add(String.valueOf(s.groupId()));
            row.add(s.groupCode());
            row.add(s.createdAt());
            row.add(s.lastModified());
            row.add(s.lastModifiedBy());
            row.add(s.lastModifiedByFullName());

            Map<String, String> fieldMap = new HashMap<>();
            if (s.fields() != null) {
                for (StudentFieldValueDto f : s.fields()) {
                    fieldMap.put(f.fieldName(), f.value());
                }
            }

            for (String fname : dynamicFieldNames) {
                row.add(fieldMap.getOrDefault(fname, ""));
            }

            rows.add(row);
        }

        return rows;
    }
}
