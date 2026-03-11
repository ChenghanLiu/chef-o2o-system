package com.labsafety.system.circle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCommentRequest {

    @NotBlank(message = "content cannot be blank")
    @Size(max = 800, message = "comment too long")
    private String content;

    public CreateCommentRequest() {}

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}