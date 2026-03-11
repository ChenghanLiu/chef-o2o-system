package com.labsafety.system.news.repo;

import java.time.Instant;

public interface NewsView {
    Long getId();
    String getTitle();
    String getContent();
    String getStatus();
    Instant getCreatedAt();
    Instant getUpdatedAt();
}