package com.labsafety.system.news;

import com.labsafety.system.news.dto.CreateNewsRequest;
import com.labsafety.system.news.dto.NewsResponse;
import com.labsafety.system.news.dto.UpdateNewsRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/news")
public class NewsAdminController {

    private final NewsService newsService;

    public NewsAdminController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public Page<NewsResponse> list(@RequestParam(required = false) String status,
                                   Pageable pageable,
                                   Authentication auth) {
        return newsService.adminList(auth.getName(), status, pageable);
    }

    @PostMapping
    public NewsResponse create(@Valid @RequestBody CreateNewsRequest req, Authentication auth) {
        return newsService.adminCreate(auth.getName(), req);
    }

    @PutMapping("/{id}")
    public NewsResponse update(@PathVariable Long id,
                               @Valid @RequestBody UpdateNewsRequest req,
                               Authentication auth) {
        return newsService.adminUpdate(auth.getName(), id, req);
    }

    @PostMapping("/{id}/hide")
    public void hide(@PathVariable Long id, Authentication auth) {
        newsService.adminSetStatus(auth.getName(), id, "HIDDEN");
    }

    @PostMapping("/{id}/publish")
    public void publish(@PathVariable Long id, Authentication auth) {
        newsService.adminSetStatus(auth.getName(), id, "PUBLISHED");
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        newsService.adminDelete(auth.getName(), id);
    }
}