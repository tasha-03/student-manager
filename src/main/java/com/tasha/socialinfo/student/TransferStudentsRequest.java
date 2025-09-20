package com.tasha.socialinfo.student;

import java.util.List;

public record TransferStudentsRequest(
        Long groupId,
        List<Long> studentIds
) {
}
