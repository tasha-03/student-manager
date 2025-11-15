package com.tasha.socialinfo;

import com.tasha.socialinfo.security.Role;
import com.tasha.socialinfo.user.User;
import com.tasha.socialinfo.user.UserRepository;
import com.tasha.socialinfo.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private static final User adminUser = new User(1L, "admin", "Admin", "123", Role.ROLE_ADMIN);
    private static final User commonUser = new User(2L, "user", "User", "123", Role.ROLE_USER);
    private static final User socialUser = new User(3L, "social", "Social", "123", Role.ROLE_SOCIAL);
    private static final User socialUserEdited = new User(3L, "social", "Social Stuff", "123", Role.ROLE_SOCIAL);

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userRepository.save(adminUser);
        userRepository.save(socialUser);
    }

    @Test
    void createUserTest() {
        when(userRepository.save(commonUser))
                .thenReturn(commonUser);

        User newUser = userService.createUser(commonUser);

        assertEquals(commonUser, newUser);
    }

    @Test
    void updateUserTest() {
        when(userRepository.save(socialUserEdited))
                .thenReturn(socialUserEdited);
        when(userRepository.save(socialUser))
                .thenReturn(socialUser);
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(socialUser));

        User updatedUser = userService.updateUser(3L, socialUserEdited);

        assertEquals(socialUserEdited.getId(), updatedUser.getId());
        assertEquals(socialUserEdited.getLogin(), updatedUser.getLogin());
        assertEquals(socialUserEdited.getName(), updatedUser.getName());
    }

    @Test
    void deleteUserTest() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(socialUser));
        when(userRepository.findByLogin("admin")).thenReturn(Optional.of(adminUser));

        userService.deleteUser(3L);

        verify(userRepository).deleteById(3L);
    }

    @Test
    void deleteUserTest_throwsWhenNotFound() {
        when(userRepository.findById(3L)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> userService.deleteUser(3L));

        verify(userRepository, never()).deleteById(anyLong());
    }
}
