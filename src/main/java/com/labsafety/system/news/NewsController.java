package com.labsafety.system.news;

import com.labsafety.system.news.dto.NewsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public Page<NewsResponse> list(Pageable pageable) {
        return newsService.listPublished(pageable);
    }

    @GetMapping("/{id}")
    public NewsResponse detail(@PathVariable Long id) {
        return newsService.getPublishedDetail(id);
    }
}