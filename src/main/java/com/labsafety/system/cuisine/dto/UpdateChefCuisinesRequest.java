package com.labsafety.system.cuisine.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateChefCuisinesRequest {
    @NotNull
    private List<Long> cuisineIds; // replace set
}