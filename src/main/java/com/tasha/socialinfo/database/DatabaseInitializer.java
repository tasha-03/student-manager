package com.tasha.socialinfo.database;

import com.tasha.socialinfo.group.Group;
import com.tasha.socialinfo.group.GroupCategory;
import com.tasha.socialinfo.group.GroupCategoryRepository;
import com.tasha.socialinfo.group.GroupRepository;
import com.tasha.socialinfo.security.Role;
import com.tasha.socialinfo.user.User;
import com.tasha.socialinfo.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseInitializer {
    @Value("${admin.login}") String adminLogin;
    @Value("${admin.password}") String adminPassword;
    private String defaultCategoryName = "Без категории";
    private String defaultGroupCode = "Группа без категории";

    @Bean
    CommandLineRunner initAdminUser(
            UserRepository userRepository,
            GroupCategoryRepository categoryRepository,
            GroupRepository groupRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            User admin = userRepository.findByLogin(adminLogin)
                    .orElseGet(() -> {
                        User newAdmin = new User();
                        newAdmin.setLogin(adminLogin);
                        newAdmin.setName("Администратор");
                        newAdmin.setPassword(passwordEncoder.encode(adminPassword));
                        newAdmin.setRole(Role.ROLE_ADMIN);
                        return userRepository.save(newAdmin);
                    });
            GroupCategory category = categoryRepository.findByName(defaultCategoryName)
                    .orElseGet(() -> {
                        GroupCategory newCategory = new GroupCategory(defaultCategoryName);
                        return categoryRepository.save(newCategory);
                    });
            if (!groupRepository.existsByCode(defaultGroupCode)) {
                Group group = new Group(defaultGroupCode, category, admin);
                groupRepository.save(group);
            }
        };
    }
}
