package com.labsafety.system.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateNewsRequest {

    @NotBlank
    @Size(max = 120)
    private String title;

    @NotBlank
    private String content;

    public CreateNewsRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}