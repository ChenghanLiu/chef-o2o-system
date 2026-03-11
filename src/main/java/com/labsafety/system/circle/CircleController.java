package com.labsafety.system.circle;

import com.labsafety.system.circle.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/circle")
public class CircleController {

    private final CircleService circleService;

    public CircleController(CircleService circleService) {
        this.circleService = circleService;
    }

    @PostMapping("/posts")
    public PostResponse createPost(@Valid @RequestBody CreatePostRequest req, Authentication auth) {
        return circleService.createPost(auth.getName(), req);
    }

    @GetMapping("/posts")
    public Page<PostResponse> listPosts(Pageable pageable) {
        return circleService.listPublishedPosts(pageable);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse postDetail(@PathVariable Long postId) {
        return circleService.getPublishedPostDetail(postId);
    }

    @PostMapping("/posts/{postId}/comments")
    public CommentResponse addComment(@PathVariable Long postId,
                                      @Valid @RequestBody CreateCommentRequest req,
                                      Authentication auth) {
        return circleService.addComment(auth.getName(), postId, req);
    }

    @GetMapping("/posts/{postId}/comments")
    public Page<CommentResponse> listComments(@PathVariable Long postId, Pageable pageable) {
        return circleService.listPublishedComments(postId, pageable);
    }

    @GetMapping("/me/posts")
    public Page<PostResponse> myPosts(Pageable pageable, Authentication auth) {
        return circleService.myPosts(auth.getName(), pageable);
    }

    @GetMapping("/me/comments")
    public Page<CommentResponse> myComments(Pageable pageable, Authentication auth) {
        return circleService.myComments(auth.getName(), pageable);
    }
}