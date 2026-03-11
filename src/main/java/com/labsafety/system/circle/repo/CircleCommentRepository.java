package com.labsafety.system.circle.repo;

import com.labsafety.system.circle.CircleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CircleCommentRepository extends JpaRepository<CircleComment, Long> {

    @Query(value = """
        SELECT
          c.id AS id,
          c.post_id AS postId,
          c.author_account_id AS authorAccountId,
          a.username AS authorUsername,
          c.content AS content,
          c.status AS status,
          c.created_at AS createdAt
        FROM circle_comments c
        JOIN accounts a ON a.id = c.author_account_id
        WHERE c.post_id = :postId AND c.status = 'PUBLISHED'
        ORDER BY c.created_at ASC, c.id ASC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM circle_comments c
        WHERE c.post_id = :postId AND c.status = 'PUBLISHED'
        """,
            nativeQuery = true)
    Page<CommentView> findPublishedCommentsByPost(@Param("postId") Long postId, Pageable pageable);

    @Query(value = """
        SELECT
          c.id AS id,
          c.post_id AS postId,
          c.author_account_id AS authorAccountId,
          a.username AS authorUsername,
          c.content AS content,
          c.status AS status,
          c.created_at AS createdAt
        FROM circle_comments c
        JOIN accounts a ON a.id = c.author_account_id
        WHERE c.status = :status
        ORDER BY c.created_at DESC, c.id DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM circle_comments c
        WHERE c.status = :status
        """,
            nativeQuery = true)
    Page<CommentView> findCommentCardsByStatus(@Param("status") String status, Pageable pageable);

    @Query(value = """
        SELECT
          c.id AS id,
          c.post_id AS postId,
          c.author_account_id AS authorAccountId,
          a.username AS authorUsername,
          c.content AS content,
          c.status AS status,
          c.created_at AS createdAt
        FROM circle_comments c
        JOIN accounts a ON a.id = c.author_account_id
        WHERE c.author_account_id = :authorId
        ORDER BY c.created_at DESC, c.id DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM circle_comments c
        WHERE c.author_account_id = :authorId
        """,
            nativeQuery = true)
    Page<CommentView> findMyCommentCards(@Param("authorId") Long authorId, Pageable pageable);
}