package com.tasha.socialinfo.user;

import com.tasha.socialinfo.group.Group;
import com.tasha.socialinfo.group.GroupRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private @Value("${admin.login}") String adminLogin;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getLogin(),
                user.getName()
        );
    }

    public UserService(UserRepository userRepository, GroupRepository groupRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserDto> getCurators() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            throw new RuntimeException("Login already in use: " + user.getLogin());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        if (!existingUser.getLogin().equals(updatedUser.getLogin()) &&
                userRepository.findByLogin(updatedUser.getLogin()).isPresent()) {
            throw new RuntimeException("Login already in use: " + updatedUser.getLogin());
        }
        existingUser.setLogin(updatedUser.getLogin());
        existingUser.setName(updatedUser.getName());
        existingUser.setRole(updatedUser.getRole());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getLogin().equals(adminLogin)) {
            throw new RuntimeException("Cannot delete admin user");
        }

        User admin = userRepository.findByLogin(adminLogin)
                .orElseThrow(() -> new RuntimeException("Admin user has not been initialized"));

        List<Group> groups = groupRepository.findByCuratorId(user.getId());

        for (Group g : groups) {
            g.setCurator(admin);
        }

        userRepository.deleteById(id);
    }
}
