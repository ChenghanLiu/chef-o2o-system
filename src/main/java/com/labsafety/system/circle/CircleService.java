package com.labsafety.system.circle;

import com.labsafety.system.circle.dto.*;
import com.labsafety.system.circle.repo.*;
import com.labsafety.system.user.Role;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CircleService {

    private final CirclePostRepository postRepository;
    private final CircleCommentRepository commentRepository;
    private final UserRepository userRepository;

    public CircleService(CirclePostRepository postRepository,
                         CircleCommentRepository commentRepository,
                         UserRepository userRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
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

    private void requireUserOrChef(User me) {
        if (me.getRole() != Role.USER && me.getRole() != Role.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only USER/CHEF can do this");
        }
    }

    private PostResponse toPostResponse(PostCardView v) {
        PostResponse r = new PostResponse();
        r.setId(v.getId());
        r.setAuthorAccountId(v.getAuthorAccountId());
        r.setAuthorUsername(v.getAuthorUsername());
        r.setTitle(v.getTitle());
        r.setContent(v.getContent());
        r.setStatus(v.getStatus());
        r.setCreatedAt(v.getCreatedAt());
        r.setUpdatedAt(v.getUpdatedAt());
        r.setCommentCount(v.getCommentCount() == null ? 0L : v.getCommentCount());
        return r;
    }

    private CommentResponse toCommentResponse(CommentView v) {
        CommentResponse r = new CommentResponse();
        r.setId(v.getId());
        r.setPostId(v.getPostId());
        r.setAuthorAccountId(v.getAuthorAccountId());
        r.setAuthorUsername(v.getAuthorUsername());
        r.setContent(v.getContent());
        r.setStatus(v.getStatus());
        r.setCreatedAt(v.getCreatedAt());
        return r;
    }

    // -------- user/chef APIs --------

    @Transactional
    public PostResponse createPost(String identifier, CreatePostRequest req) {
        User me = requireActiveUser(identifier);
        requireUserOrChef(me);

        CirclePost p = new CirclePost();
        p.setAuthor(me);
        p.setTitle(req.getTitle());
        p.setContent(req.getContent());
        p.setStatus("PUBLISHED");

        CirclePost saved = postRepository.save(p);

        // Return as card view (with username + counts) for stable JSON
        PostCardView v = postRepository.findPostCardById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Post created but not found"));
        return toPostResponse(v);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> listPublishedPosts(Pageable pageable) {
        return postRepository.findPublishedPostCards(pageable).map(this::toPostResponse);
    }

    @Transactional(readOnly = true)
    public PostResponse getPublishedPostDetail(Long postId) {
        PostCardView v = postRepository.findPostCardById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!"PUBLISHED".equals(v.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return toPostResponse(v);
    }

    @Transactional
    public CommentResponse addComment(String identifier, Long postId, CreateCommentRequest req) {
        User me = requireActiveUser(identifier);
        requireUserOrChef(me);

        CirclePost post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!"PUBLISHED".equals(post.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not available");
        }

        CircleComment c = new CircleComment();
        c.setPost(post);
        c.setAuthor(me);
        c.setContent(req.getContent());
        c.setStatus("PUBLISHED");

        CircleComment saved = commentRepository.save(c);

        // We return via a tiny lookup from projection list by post, but simplest:
        CommentResponse r = new CommentResponse();
        r.setId(saved.getId());
        r.setPostId(postId);
        r.setAuthorAccountId(me.getId());
        r.setAuthorUsername(me.getUsername());
        r.setContent(saved.getContent());
        r.setStatus(saved.getStatus());
        r.setCreatedAt(saved.getCreatedAt());
        return r;
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> listPublishedComments(Long postId, Pageable pageable) {
        // If post is hidden, treat as not found for user-side
        PostCardView v = postRepository.findPostCardById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        if (!"PUBLISHED".equals(v.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }

        return commentRepository.findPublishedCommentsByPost(postId, pageable).map(this::toCommentResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> myPosts(String identifier, Pageable pageable) {
        User me = requireActiveUser(identifier);
        requireUserOrChef(me);
        return postRepository.findMyPostCards(me.getId(), pageable).map(this::toPostResponse);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> myComments(String identifier, Pageable pageable) {
        User me = requireActiveUser(identifier);
        requireUserOrChef(me);
        return commentRepository.findMyCommentCards(me.getId(), pageable).map(this::toCommentResponse);
    }

    // -------- admin APIs --------

    private User requireAdmin(String identifier) {
        User me = requireActiveUser(identifier);
        if (me.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN");
        }
        return me;
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> adminListPosts(String identifier, String status, Pageable pageable) {
        requireAdmin(identifier);
        if (status == null || status.isBlank()) {
            // default: show all published first (simple behavior)
            return postRepository.findPublishedPostCards(pageable).map(this::toPostResponse);
        }
        return postRepository.findPostCardsByStatus(status, pageable).map(this::toPostResponse);
    }

    @Transactional
    public void adminSetPostStatus(String identifier, Long postId, String status) {
        requireAdmin(identifier);
        CirclePost post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        post.setStatus(status);
        postRepository.save(post);
    }

    @Transactional
    public void adminDeletePost(String identifier, Long postId) {
        requireAdmin(identifier);
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        // comments cascade delete (FK ON DELETE CASCADE)
        postRepository.deleteById(postId);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> adminListComments(String identifier, String status, Pageable pageable) {
        requireAdmin(identifier);
        if (status == null || status.isBlank()) {
            return commentRepository.findCommentCardsByStatus("PUBLISHED", pageable).map(this::toCommentResponse);
        }
        return commentRepository.findCommentCardsByStatus(status, pageable).map(this::toCommentResponse);
    }

    @Transactional
    public void adminSetCommentStatus(String identifier, Long commentId, String status) {
        requireAdmin(identifier);
        CircleComment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        c.setStatus(status);
        commentRepository.save(c);
    }

    @Transactional
    public void adminDeleteComment(String identifier, Long commentId) {
        requireAdmin(identifier);
        if (!commentRepository.existsById(commentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
        commentRepository.deleteById(commentId);
    }
}