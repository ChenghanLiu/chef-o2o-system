package com.labsafety.system.circle.dto;

import java.time.Instant;

public class PostResponse {
    private Long id;
    private Long authorAccountId;
    private String authorUsername;
    private String title;
    private String content;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private Long commentCount;

    public PostResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAuthorAccountId() { return authorAccountId; }
    public void setAuthorAccountId(Long authorAccountId) { this.authorAccountId = authorAccountId; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Long getCommentCount() { return commentCount; }
    public void setCommentCount(Long commentCount) { this.commentCount = commentCount; }
}