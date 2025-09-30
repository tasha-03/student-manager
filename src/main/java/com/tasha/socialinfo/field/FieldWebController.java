package com.tasha.socialinfo.field;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/fields")
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
            @RequestParam(required = false) List<List<String>> validValues
    ) {
        for (int i = 0; i < fieldIds.size(); i++) {
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
    public String newField(@ModelAttribute Field field) {
        fieldService.createField(field);
        return "redirect:/fields";
    }

    @PostMapping("/{id}/delete")
    public String deleteField(@PathVariable Long id) {
        fieldService.deleteField(id);
        return "redirect:/fields";
    }
}
