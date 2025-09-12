package com.tasha.socialinfo.group;

public class GroupDto {
    private Long id;
    private String code;

    private Long categoryId;
    private String categoryName;

    private Long curatorId;
    private String curatorName;

    public GroupDto(Long id, String code, Long categoryId, String categoryName, Long curatorId, String curatorName) {
        this.id = id;
        this.code = code;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.curatorId = curatorId;
        this.curatorName = curatorName;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Long getCuratorId() {
        return curatorId;
    }

    public String getCuratorName() {
        return curatorName;
    }
}
