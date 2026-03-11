package com.labsafety.system.circle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePostRequest {

    @Size(max = 120, message = "title too long")
    private String title;

    @NotBlank(message = "content cannot be blank")
    private String content;

    public CreatePostRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}