package com.tasha.socialinfo.group;

import com.tasha.socialinfo.spreadsheet.SpreadsheetReader;
import com.tasha.socialinfo.user.User;
import com.tasha.socialinfo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    public List<Group> getGroupsByCategoryId(Long categoryId) {
        return groupRepository.findByCategoryId(categoryId);
    }

    @Transactional
    public GroupDto createGroup(GroupRequest group) {
        GroupCategory category = categoryRepository.findById(group.categoryId())
                .orElseThrow(() -> new RuntimeException("Group category not found"));
        User curator = userRepository.findById(group.curatorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group savedGroup = groupRepository.save(new Group(group.code(), category, curator));
        return toDto(savedGroup);
    }

    @Transactional
    public GroupDto updateGroup(Long id, GroupRequest updatedGroup) {
        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        GroupCategory category = categoryRepository.findById(updatedGroup.categoryId())
                .orElseThrow(() -> new RuntimeException("Group category not found"));
        User curator = userRepository.findById(updatedGroup.curatorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingGroup.setCode(updatedGroup.code());
        existingGroup.setCategory(category);
        existingGroup.setCurator(curator);

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

    @Transactional
    public void importGroupsFromExcel(MultipartFile file, String username, boolean hasHeaderRow) {
        User curator = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<List<String>> rows = SpreadsheetReader.readRows(file, hasHeaderRow);

        for (List<String> r : rows) {
            String code = r.get(0).trim().toLowerCase();
            String categoryName = r.get(1).trim();

            if (groupRepository.existsByCode(code)) continue;

            GroupCategory category = categoryRepository.findByName(categoryName)
                    .orElseGet(() -> {
                        GroupCategory newCategory = new GroupCategory(categoryName);
                        return categoryRepository.save(newCategory);
                    });

            Group group = new Group(code, category, curator);
            groupRepository.save(group);
        }
    }
}
