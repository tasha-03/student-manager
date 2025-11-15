package com.tasha.socialinfo.field;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/fields")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class FieldWebController {
    private final FieldService fieldService;

    public FieldWebController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @GetMapping
    public String listFields(Model model) {
        List<Field> fields = fieldService.getAllFields();
        model.addAttribute("fields", fields);
        return "fields/list";
    }

    @PostMapping
    public String saveFields(
            @RequestParam(required = false) List<Long> fieldIds,
            @RequestParam(required = false) List<String> names,
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<List<String>> validValues,
            RedirectAttributes redirectAttributes
    ) {
        for (int i = 0; i < fieldIds.size(); i++) {
            List<String> currentValues = validValues.get(i);
            for (int j = 0; j < currentValues.size(); j++) {
                if (currentValues.get(j).isBlank() && j == currentValues.size() - 1) {
                    currentValues.remove(j);
                    break;
                }
                if (currentValues.get(j).isBlank()) {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            "В поле \"" + names.get(i) + "\" не может быть пустого допустимого значения");
                    return "redirect:/fields";
                }
            }
            Field field = new Field(
                    fieldIds.get(i),
                    names.get(i),
                    FieldType.valueOf(types.get(i)),
                    validValues.get(i)
            );
            fieldService.updateField(fieldIds.get(i), field);
        }
        return "redirect:/fields";
    }

    @PostMapping("/new")
    public String newField(RedirectAttributes redirectAttributes, @ModelAttribute Field field) {
        List<String> validValues = field.getValidValues();
        if (validValues != null) {
            for (int i = 0; i < field.getValidValues().size(); i++) {
                if (validValues.get(i).isBlank() && i == validValues.size() - 1) {
                    field.setValidValues(field.getValidValues().subList(0, validValues.size() - 1));
                    break;
                }
                if (field.getValidValues().get(i).isBlank()) {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage", "В поле не может быть пустого допустимого значения");
                    return "redirect:/fields";
                }
            }
        }
        fieldService.createField(field);
        return "redirect:/fields";
    }

    @GetMapping("/{id}/delete")
    public String deleteField(@PathVariable Long id) {
        fieldService.deleteField(id);
        return "redirect:/fields";
    }
}
