package com.tasha.socialinfo.user;

import com.tasha.socialinfo.security.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserInitializer {
    @Value("${admin.login}") String adminLogin;
    @Value("${admin.password}") String adminPassword;

    @Bean
    CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByLogin("admin").isEmpty()) {
                User admin = new User();
                admin.setLogin(adminLogin);
                admin.setName("Администратор");
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ROLE_ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
