package com.tasha.socialinfo.student;

import java.time.LocalDate;

public record StudentRequest(
        String name,
        LocalDate birthdate,
        Long groupId
        ) {}
