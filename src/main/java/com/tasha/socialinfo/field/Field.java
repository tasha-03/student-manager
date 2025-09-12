package com.tasha.socialinfo.field;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "fields")
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private FieldType type;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "FieldValidValues", joinColumns = @JoinColumn(name = "fieldId"))
    @Column(name = "value")
    private List<String> validValues;

    public Field() {
    }

    public Field(Long id, String name, FieldType type, List<String> validValues) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.validValues = validValues;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public List<String> getValidValues() {
        return validValues;
    }

    public void setValidValues(List<String> validValues) {
        this.validValues = validValues;
    }
}
