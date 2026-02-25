package com.labsafety.system.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotBlank
    private String status; // e.g. CANCELLED / COMPLETED / ACCEPTED ...
}