package com.labsafety.system.cuisine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCuisineRequest {
    @NotBlank
    private String name;

    @NotNull
    private Integer sortOrder;
}