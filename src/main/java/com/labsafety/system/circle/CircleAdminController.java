package com.labsafety.system.circle;

import com.labsafety.system.circle.dto.PostResponse;
import com.labsafety.system.circle.dto.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/circle")
public class CircleAdminController {

    private final CircleService circleService;

    public CircleAdminController(CircleService circleService) {
        this.circleService = circleService;
    }

    // posts
    @GetMapping("/posts")
    public Page<PostResponse> listPosts(@RequestParam(required = false) String status,
                                        Pageable pageable,
                                        Authentication auth) {
        return circleService.adminListPosts(auth.getName(), status, pageable);
    }

    @PostMapping("/posts/{postId}/hide")
    public void hidePost(@PathVariable Long postId, Authentication auth) {
        circleService.adminSetPostStatus(auth.getName(), postId, "HIDDEN");
    }

    @PostMapping("/posts/{postId}/publish")
    public void publishPost(@PathVariable Long postId, Authentication auth) {
        circleService.adminSetPostStatus(auth.getName(), postId, "PUBLISHED");
    }

    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable Long postId, Authentication auth) {
        circleService.adminDeletePost(auth.getName(), postId);
    }

    // comments
    @GetMapping("/comments")
    public Page<CommentResponse> listComments(@RequestParam(required = false) String status,
                                              Pageable pageable,
                                              Authentication auth) {
        return circleService.adminListComments(auth.getName(), status, pageable);
    }

    @PostMapping("/comments/{commentId}/hide")
    public void hideComment(@PathVariable Long commentId, Authentication auth) {
        circleService.adminSetCommentStatus(auth.getName(), commentId, "HIDDEN");
    }

    @PostMapping("/comments/{commentId}/publish")
    public void publishComment(@PathVariable Long commentId, Authentication auth) {
        circleService.adminSetCommentStatus(auth.getName(), commentId, "PUBLISHED");
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable Long commentId, Authentication auth) {
        circleService.adminDeleteComment(auth.getName(), commentId);
    }
}