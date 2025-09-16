package com.tasha.socialinfo.group;

public record GroupDto (
        Long id,
        String code,
        Long categoryId,
        String categoryName,
        Long curatorId,
        String curatorName
) {}
