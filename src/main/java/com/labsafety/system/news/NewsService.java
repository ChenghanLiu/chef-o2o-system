package com.labsafety.system.news;

import com.labsafety.system.news.dto.CreateNewsRequest;
import com.labsafety.system.news.dto.NewsResponse;
import com.labsafety.system.news.dto.UpdateNewsRequest;
import com.labsafety.system.news.repo.NewsRepository;
import com.labsafety.system.news.repo.NewsView;
import com.labsafety.system.user.Role;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    public NewsService(NewsRepository newsRepository, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }

    private User requireActiveUser(String identifier) {
        User me = userRepository.findByUsername(identifier)
                .orElseGet(() -> userRepository.findByPhone(identifier)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found")));

        if (!"ACTIVE".equals(me.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account disabled");
        }
        return me;
    }

    private void requireAdmin(String identifier) {
        User me = requireActiveUser(identifier);
        if (me.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN");
        }
    }

    private NewsResponse toResponse(NewsView v) {
        NewsResponse r = new NewsResponse();
        r.setId(v.getId());
        r.setTitle(v.getTitle());
        r.setContent(v.getContent());
        r.setStatus(v.getStatus());
        r.setCreatedAt(v.getCreatedAt());
        r.setUpdatedAt(v.getUpdatedAt());
        return r;
    }

    // -------- user readonly --------

    @Transactional(readOnly = true)
    public Page<NewsResponse> listPublished(Pageable pageable) {
        return newsRepository.findPublished(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public NewsResponse getPublishedDetail(Long id) {
        NewsView v = newsRepository.findViewById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));

        if (!"PUBLISHED".equals(v.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found");
        }
        return toResponse(v);
    }

    // -------- admin --------

    @Transactional(readOnly = true)
    public Page<NewsResponse> adminList(String identifier, String status, Pageable pageable) {
        requireAdmin(identifier);
        if (status == null || status.isBlank()) {
            return newsRepository.findPublished(pageable).map(this::toResponse);
        }
        return newsRepository.findByStatus(status, pageable).map(this::toResponse);
    }

    @Transactional
    public NewsResponse adminCreate(String identifier, CreateNewsRequest req) {
        requireAdmin(identifier);

        News n = new News();
        n.setTitle(req.getTitle());
        n.setContent(req.getContent());
        n.setStatus("PUBLISHED");

        News saved = newsRepository.save(n);

        NewsView v = newsRepository.findViewById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Created but not found"));
        return toResponse(v);
    }

    @Transactional
    public NewsResponse adminUpdate(String identifier, Long id, UpdateNewsRequest req) {
        requireAdmin(identifier);

        News n = newsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));

        n.setTitle(req.getTitle());
        n.setContent(req.getContent());

        newsRepository.save(n);

        NewsView v = newsRepository.findViewById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Updated but not found"));
        return toResponse(v);
    }

    @Transactional
    public void adminSetStatus(String identifier, Long id, String status) {
        requireAdmin(identifier);

        News n = newsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));

        n.setStatus(status);
        newsRepository.save(n);
    }

    @Transactional
    public void adminDelete(String identifier, Long id) {
        requireAdmin(identifier);

        if (!newsRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found");
        }
        newsRepository.deleteById(id);
    }
}