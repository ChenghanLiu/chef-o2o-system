package com.labsafety.system.circle.repo;

import java.time.Instant;

public interface CommentView {
    Long getId();
    Long getPostId();
    Long getAuthorAccountId();
    String getAuthorUsername();
    String getContent();
    String getStatus();
    Instant getCreatedAt();
}