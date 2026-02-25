package com.labsafety.system.order;

public enum OrderStatus {
    PENDING,        // created by user, waiting chef decision
    ACCEPTED,       // chef accepted
    REJECTED,       // chef rejected
    IN_PROGRESS,    // chef started service
    COMPLETED,      // chef completed service
    CANCELLED       // user cancelled (before IN_PROGRESS)
}