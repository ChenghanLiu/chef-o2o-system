package com.labsafety.system.circle.dto;

import java.time.Instant;

public class CommentResponse {
    private Long id;
    private Long postId;
    private Long authorAccountId;
    private String authorUsername;
    private String content;
    private String status;
    private Instant createdAt;

    public CommentResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getAuthorAccountId() { return authorAccountId; }
    public void setAuthorAccountId(Long authorAccountId) { this.authorAccountId = authorAccountId; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}