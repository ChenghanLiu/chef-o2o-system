package com.labsafety.system.order.dto;

import jakarta.validation.constraints.NotBlank;

public class OrderActionRequest {

    // USER: CANCEL
    // CHEF: ACCEPT / REJECT / START / COMPLETE
    @NotBlank
    private String action;

    public OrderActionRequest() {}

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}