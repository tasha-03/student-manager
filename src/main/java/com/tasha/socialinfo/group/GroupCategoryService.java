package com.tasha.socialinfo.group;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class GroupCategoryService {
    private final GroupCategoryRepository categoryRepository;
    private final GroupRepository groupRepository;

    private GroupCategoryDto toDto(GroupCategory category) {
        return new GroupCategoryDto(
                category.getId(),
                category.getName(),
                groupRepository.findByCategoryId(category.getId())
        );
    }

    public GroupCategoryService(
            GroupCategoryRepository categoryRepository,
            GroupRepository groupRepository) {
        this.categoryRepository = categoryRepository;
        this.groupRepository = groupRepository;
    }

    public List<GroupCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<GroupCategoryDto> getAllCategoriesWithGroups() {
        List<GroupCategoryDto> categories = new java.util.ArrayList<>(
                categoryRepository.findAll().stream().map(this::toDto).toList());
        categories.sort(Comparator.comparing(GroupCategoryDto::categoryName));
        for (GroupCategoryDto category : categories) {
            category.groups().sort(Comparator.comparing(Group::getCode));
        }
        return categories;
    }

    public GroupCategory getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group category not found"));
    }

    @Transactional
    public GroupCategory createCategory(GroupCategory category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public GroupCategory updateCategory(Long id, GroupCategory updatedCategory) {
        GroupCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group category not found"));

        existingCategory.setName(updatedCategory.getName());

        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Group category not found");
        }
        categoryRepository.deleteById(id);
    }
}
