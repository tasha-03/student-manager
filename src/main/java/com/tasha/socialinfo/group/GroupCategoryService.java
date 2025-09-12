package com.tasha.socialinfo.group;

import com.tasha.socialinfo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupCategoryService {
    private final GroupCategoryRepository categoryRepository;

    public GroupCategoryService(GroupCategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<GroupCategory> getAllCategories() {
        return categoryRepository.findAll();
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
