package com.tasha.socialinfo.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UserWebController {
    private final UserService userService;

    public UserWebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/list";
    }

    @PostMapping("/new")
    public String newUser(
            @ModelAttribute UserCreationDto userDto,
            RedirectAttributes redirectAttributes
    ) {
        if (!userDto.password().equals(userDto.confirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пароли не совпадают");
            return "redirect:/users";
        }

        User user = new User();
        user.setLogin(userDto.login());
        user.setName(userDto.name());
        user.setPassword(userDto.password());
        user.setRole(userDto.role());
        User savedUser = userService.createUser(user);
        return "redirect:/users/" + savedUser.getId();
    }

    @GetMapping("/{id}")
    public String viewUser(
            @PathVariable Long id,
            Model model
    ) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "users/view";
    }

    @PostMapping("/{id}")
    public String saveUser(
            @PathVariable Long id,
            @ModelAttribute UserCreationDto userDto,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (!userDto.password().equals(userDto.confirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пароли не совпадают");
            return "redirect:/users";
        }

        User user = new User();
        user.setLogin(userDto.login());
        user.setName(userDto.name());
        user.setPassword(userDto.password());
        user.setRole(userDto.role());

        User updatedUser = userService.updateUser(id, user);
        model.addAttribute("user", updatedUser);
        return "users/view";
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}
