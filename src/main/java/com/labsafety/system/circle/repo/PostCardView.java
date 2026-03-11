package com.labsafety.system.circle.repo;

import java.time.Instant;

public interface PostCardView {
    Long getId();
    Long getAuthorAccountId();
    String getAuthorUsername();
    String getTitle();
    String getContent();
    String getStatus();
    Instant getCreatedAt();
    Instant getUpdatedAt();
    Long getCommentCount();
}