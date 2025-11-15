package com.tasha.socialinfo.group;

import com.tasha.socialinfo.user.UserDto;
import com.tasha.socialinfo.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/groups")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class GroupWebController {
    private final GroupService groupService;
    private final GroupCategoryService categoryService;
    private final UserService userService;

    public GroupWebController(GroupService groupService, GroupCategoryService categoryService, UserService userService) {
        this.groupService = groupService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public String listGroups(Model model) {
        List<GroupDto> groups = groupService.getAllGroups();
        groups.removeIf(g -> g.code().equals(groupService.getDefaultGroupCode()));
        List<GroupCategory> categories = categoryService.getAllCategories();
        categories.removeIf(cat -> cat.getName().equals(categoryService.getDefaultCategoryName()));
        List<UserDto> curators = userService.getCurators();

        model.addAttribute("groups", groups);
        model.addAttribute("categories", categories);
        model.addAttribute("curators", curators);

        return "groups/list";
    }

    @GetMapping("/{id}")
    public String viewGroup(
            @PathVariable Long id,
            Model model
    ) {
        GroupDto group = groupService.getGroupById(id);
        List<GroupCategory> categories = categoryService.getAllCategories();
        categories.removeIf(cat -> cat.getName().equals(categoryService.getDefaultCategoryName()));
        List<UserDto> curators = userService.getCurators();

        model.addAttribute("group", group);
        model.addAttribute("categories", categories);
        model.addAttribute("curators", curators);

        return "groups/view";
    }

    @PostMapping("/{id}")
    public String saveGroup(
            @PathVariable Long id,
            @ModelAttribute GroupRequest group
    ) {

        groupService.updateGroup(id, group);
        return "redirect:/groups";
    }

    @GetMapping("/{id}/delete")
    public String deleteGroup(
            @PathVariable Long id
    ) {
        groupService.deleteGroup(id);
        return "redirect:/groups";
    }

    @PostMapping("/categories/new")
    public String newCategory(@ModelAttribute GroupCategory category) {
        categoryService.createCategory(category);
        return "redirect:/groups";
    }

    @PostMapping("/categories/{id}")
    public String saveCategory(
            @PathVariable Long id,
            @ModelAttribute GroupCategory category
    ) {
        categoryService.updateCategory(id, category);
        return "redirect:/groups";
    }

    @GetMapping("/categories/{id}/delete")
    public String deleteCategory(
            @PathVariable Long id
    ) {
        categoryService.deleteCategory(id);
        return "redirect:/groups";
    }

    @PostMapping("/new")
    public String newGroup(@ModelAttribute GroupRequest group) {
        GroupDto createdGroup = groupService.createGroup(group);
        return "redirect:/groups/" + createdGroup.id();
    }
}
