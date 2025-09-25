package com.tasha.socialinfo.group;

import java.util.List;

public record GroupCategoryDto(
        Long categoryId,
        String categoryName,
        List<Group> groups
) {
}
