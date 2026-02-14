package com.forvmom.common.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReorderRequestDto {

    private Long id;
    private Long newPosition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Long newPosition) {
        this.newPosition = newPosition;
    }
}
