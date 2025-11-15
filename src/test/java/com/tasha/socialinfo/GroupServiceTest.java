package com.tasha.socialinfo;

import com.tasha.socialinfo.group.*;
import com.tasha.socialinfo.security.Role;
import com.tasha.socialinfo.student.StudentRepository;
import com.tasha.socialinfo.user.User;
import com.tasha.socialinfo.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GroupServiceTest {
    @Autowired
    private GroupService groupService;

    @MockitoBean
    private GroupRepository groupRepository;

    @MockitoBean
    private GroupCategoryRepository categoryRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    private static final User user = new User(1L, "admin", "Admin", "123", Role.ROLE_USER);
    private static final GroupCategory category = new GroupCategory(1L, "Category");

    private static final Group group1 = new Group(1L, "C1", category, user);
    private static final GroupDto group1Dto = new GroupDto(
            1L, "C1", category.getId(), category.getName(), user.getId(), user.getName());
    private static final GroupRequest group1Request = new GroupRequest("C1", category.getId(), user.getId());

    private static final Group group1Edited = new Group(1L, "C1 Edited", category, user);
    private static final GroupDto group1EditedDto = new GroupDto(
            1L, "C1 Edited", category.getId(), category.getName(), user.getId(), user.getName());
    private static final GroupRequest group1EditedRequest = new GroupRequest(
            "C1 Edited", category.getId(), user.getId());

    private static final Group group2 = new Group(2L, "C2", category, user);
    private static final GroupDto group2Dto = new GroupDto(
            2L, "C2", category.getId(), category.getName(), user.getId(), user.getName());
    private static final GroupRequest group2Request = new GroupRequest("C2", category.getId(), user.getId());


    @BeforeEach
    void setup() {
        groupRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(user);
        categoryRepository.save(category);
        groupRepository.save(group1);
    }

    @Test
    void createGroupTest() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(groupRepository.save(any(Group.class))).thenReturn(group2);

        GroupDto newGroupDto = groupService.createGroup(group2Request);

        assertEquals(group2Dto, newGroupDto);
    }

    @Test
    void updateGroupTest() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupRepository.save(any(Group.class))).thenReturn(group1Edited);

        GroupDto newGroupDto = groupService.updateGroup(1L, group1EditedRequest);

        assertEquals(group1EditedDto, newGroupDto);
    }

    @Test
    void deleteGroupTest() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(studentRepository.findByGroupId(1L)).thenReturn(List.of());

        groupService.deleteGroup(1L);

        verify(groupRepository).deleteById(1L);
    }
}
