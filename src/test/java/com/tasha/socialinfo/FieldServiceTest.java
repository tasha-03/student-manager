package com.tasha.socialinfo;

import com.tasha.socialinfo.field.Field;
import com.tasha.socialinfo.field.FieldRepository;
import com.tasha.socialinfo.field.FieldService;
import com.tasha.socialinfo.field.FieldType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FieldServiceTest {
    @Autowired
    private FieldService fieldService;

    @MockitoBean
    private FieldRepository fieldRepository;

    private static final Field textField = new Field(
            1L, "Text Field", FieldType.TEXT, null);
    private static final Field invalidTextField = new Field(
            1L, "Invalid Text Field", FieldType.TEXT, new ArrayList<>(List.of("Option 1", "Option 2", "Option 3")));
    private static final Field editedTextField = new Field(
            1L, "New Text Field", FieldType.TEXT, null);
    private static final Field fromTextToEnumField = new Field(
            1L, "Text To Enum Field", FieldType.ENUM, new ArrayList<>(List.of("Option 1", "Option 2", "Option 3")));
    private static final Field numberField = new Field(
            2L, "Number Field", FieldType.NUMBER, null);
    private static final Field enumField = new Field(
            5L, "Enum Field", FieldType.ENUM, new ArrayList<>(List.of("Option 1", "Option 2", "Option 3")));
    private static final Field invalidEnumField = new Field(
            6L, "Invalid Enum Field", FieldType.ENUM, null);

    @BeforeEach
    void setup() {
        fieldRepository.deleteAll();
        fieldRepository.save(textField);
    }

    @Test
    void createPlainFieldTest() {
        when(fieldRepository.save(numberField)).thenReturn(numberField);

        Field newField = fieldService.createField(numberField);

        assertEquals(numberField, newField);

        assertThrows(IllegalArgumentException.class, () -> fieldService.createField(invalidTextField));
    }

    @Test
    void createEnumFieldTest() {
        when(fieldRepository.save(enumField)).thenReturn(enumField);

        Field newField = fieldService.createField(enumField);

        assertEquals(enumField, newField);

        assertThrows(IllegalArgumentException.class, () -> fieldService.createField(invalidEnumField));
    }

    @Test
    void updateFieldTest_TEXT_to_TEXT() {
        when(fieldRepository.save(textField)).thenReturn(textField);
        when(fieldRepository.save(editedTextField)).thenReturn(editedTextField);

        when(fieldRepository.findById(1L)).thenReturn(Optional.of(textField));

        Field updateField1 = fieldService.updateField(1L, editedTextField);


        assertEquals(editedTextField.getName(), updateField1.getName());
        assertEquals(editedTextField.getType(), updateField1.getType());
        assertEquals(editedTextField.getValidValues(), updateField1.getValidValues());
    }

    @Test
    void updateFieldTest_TEXT_to_ENUM() {
        when(fieldRepository.save(textField)).thenReturn(textField);
        when(fieldRepository.save(fromTextToEnumField)).thenReturn(fromTextToEnumField);

        when(fieldRepository.findById(1L)).thenReturn(Optional.of(textField));

        Field updateField2 = fieldService.updateField(1L, fromTextToEnumField);

        assertEquals(fromTextToEnumField.getName(), updateField2.getName());
        assertEquals(fromTextToEnumField.getType(), updateField2.getType());
        assertEquals(fromTextToEnumField.getValidValues(), updateField2.getValidValues());
    }

    @Test
    void deleteFieldTest() {
        when(fieldRepository.existsById(1L)).thenReturn(true);

        fieldService.deleteField(1L);

        verify(fieldRepository).deleteById(1L);
    }
}
