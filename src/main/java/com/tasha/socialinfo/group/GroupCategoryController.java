package com.tasha.socialinfo.group;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group_categories")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class GroupCategoryController {
    private final GroupCategoryService categoryService;

    public GroupCategoryController(GroupCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<GroupCategory>> getAllCategories() {
        List<GroupCategory> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupCategory> getCategoryById(@PathVariable Long id) {
        GroupCategory category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<GroupCategory> createCategory(@RequestBody GroupCategory category) {
        GroupCategory createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupCategory> updateCategory(@PathVariable Long id, @RequestBody GroupCategory category) {
        GroupCategory updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
