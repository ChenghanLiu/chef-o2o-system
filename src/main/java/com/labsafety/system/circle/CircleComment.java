package com.labsafety.system.circle;

import com.labsafety.system.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "circle_comments")
public class CircleComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // circle_posts.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private CirclePost post;

    // accounts.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_account_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 800)
    private String content;

    @Column(nullable = false, length = 20)
    private String status = "PUBLISHED";

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public CircleComment() {}

    public Long getId() { return id; }

    public CirclePost getPost() { return post; }
    public void setPost(CirclePost post) { this.post = post; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
}