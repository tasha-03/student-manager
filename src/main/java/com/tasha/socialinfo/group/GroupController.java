package com.tasha.socialinfo.group;

import com.tasha.socialinfo.spreadsheet.SpreadsheetMediaType;
import com.tasha.socialinfo.spreadsheet.SpreadsheetReader;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        List<GroupDto> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable Long id) {
        GroupDto group = groupService.getGroupById(id);
        return ResponseEntity.ok(group);
    }

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@RequestBody GroupRequest group) {
        GroupDto createdGroup = groupService.createGroup(group);
        return ResponseEntity.ok(createdGroup);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importGroups(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "hasHeaderRow", defaultValue = "true") boolean hasHeaderRow,
            Authentication authentication) {
        SpreadsheetMediaType type = SpreadsheetMediaType.validateSpreadsheetFile(file);

        groupService.importGroupsFromExcel(file, authentication.getName(), hasHeaderRow);
        return ResponseEntity.ok("Groups imported successfully!");
    }

    @PostMapping("/import/preview")
    public ResponseEntity<List<List<String>>> importPreviewGroups(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "hasHeaderRow", defaultValue = "true") boolean hasHeaderRow,
            Authentication authentication) {
        SpreadsheetMediaType type = SpreadsheetMediaType.validateSpreadsheetFile(file);
        List<List<String>> data = SpreadsheetReader.readRows(file, hasHeaderRow);

        return ResponseEntity.ok(data);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDto> updateGroup(@PathVariable Long id, @RequestBody GroupRequest group) {
        GroupDto updatedGroup = groupService.updateGroup(id, group);
        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}
