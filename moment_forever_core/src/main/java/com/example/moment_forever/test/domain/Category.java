package com.example.moment_forever.test.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category extends NamedEntity {

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean active = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    // Getters and setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}