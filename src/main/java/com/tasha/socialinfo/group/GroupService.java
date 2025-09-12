package com.tasha.socialinfo.group;

import com.tasha.socialinfo.user.User;
import com.tasha.socialinfo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupCategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, GroupCategoryRepository categoryRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    private GroupDto toDto(Group group) {
        return new GroupDto(
                group.getId(),
                group.getCode(),
                group.getCategory().getId(),
                group.getCategory().getName(),
                group.getCurator().getId(),
                group.getCurator().getName()
        );
    }

    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public GroupDto getGroupById(Long id) {
        return groupRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    @Transactional
    public GroupDto createGroup(Group group) {
        GroupCategory category = categoryRepository.findById(group.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Group category not found"));
        User curator = userRepository.findById(group.getCurator().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Group savedGroup = groupRepository.save(group);
        return toDto(savedGroup);
    }

    @Transactional
    public GroupDto updateGroup(Long id, Group updatedGroup) {
        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        existingGroup.setCode(updatedGroup.getCode());
        existingGroup.setCategory(updatedGroup.getCategory());
        existingGroup.setCurator(updatedGroup.getCurator());

        Group savedGroup = groupRepository.save(existingGroup);
        return toDto(savedGroup);
    }

    @Transactional
    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new RuntimeException("Group not found");
        }
        groupRepository.deleteById(id);
    }
}
