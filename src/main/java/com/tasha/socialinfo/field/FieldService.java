package com.tasha.socialinfo.field;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FieldService {
    private final FieldRepository fieldRepository;

    public FieldService(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    public List<Field> getAllFields() {
        return fieldRepository.findAll();
    }

    public Field getFieldById(Long id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));
    }

    @Transactional
    public Field createField(Field field) {
        validateField(field);
        return fieldRepository.save(field);
    }

    @Transactional
    public Field updateField(Long id, Field updatedField) {
        Field existingField = fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        existingField.setName(updatedField.getName());
        existingField.setType(updatedField.getType());

        if (updatedField.getType() == FieldType.ENUM) {
            existingField.setValidValues(updatedField.getValidValues());
        } else {
            existingField.setValidValues(null);
        }

        validateField(existingField);
        return fieldRepository.save(existingField);
    }

    @Transactional
    public void deleteField(Long id) {
        if (!fieldRepository.existsById(id)) {
            throw new RuntimeException("Field not found");
        }
        fieldRepository.deleteById(id);
    }

    private void validateField(Field field) {
        if (field.getType() != FieldType.ENUM && field.getValidValues() != null && !field.getValidValues().isEmpty()) {
            throw new IllegalArgumentException("Only ENUM fields can have validValues");
        }

        if (field.getType() == FieldType.ENUM && (field.getValidValues() == null || field.getValidValues().isEmpty())) {
            throw new IllegalArgumentException("ENUM fields must have at least one valid value");
        }
    }
}
