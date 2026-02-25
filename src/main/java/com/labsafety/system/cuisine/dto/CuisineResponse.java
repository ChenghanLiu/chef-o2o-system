package com.labsafety.system.cuisine.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CuisineResponse {
    private Long id;
    private String name;
    private Integer sortOrder;
}