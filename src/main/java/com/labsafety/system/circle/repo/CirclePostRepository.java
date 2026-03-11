package com.labsafety.system.circle.repo;

import com.labsafety.system.circle.CirclePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CirclePostRepository extends JpaRepository<CirclePost, Long> {

    @Query(value = """
        SELECT
          p.id AS id,
          p.author_account_id AS authorAccountId,
          a.username AS authorUsername,
          p.title AS title,
          p.content AS content,
          p.status AS status,
          p.created_at AS createdAt,
          p.updated_at AS updatedAt,
          (
            SELECT COUNT(*)
            FROM circle_comments c
            WHERE c.post_id = p.id AND c.status = 'PUBLISHED'
          ) AS commentCount
        FROM circle_posts p
        JOIN accounts a ON a.id = p.author_account_id
        WHERE p.status = 'PUBLISHED'
        ORDER BY p.created_at DESC, p.id DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM circle_posts p
        WHERE p.status = 'PUBLISHED'
        """,
            nativeQuery = true)
    Page<PostCardView> findPublishedPostCards(Pageable pageable);

    @Query(value = """
        SELECT
          p.id AS id,
          p.author_account_id AS authorAccountId,
          a.username AS authorUsername,
          p.title AS title,
          p.content AS content,
          p.status AS status,
          p.created_at AS createdAt,
          p.updated_at AS updatedAt,
          (
            SELECT COUNT(*)
            FROM circle_comments c
            WHERE c.post_id = p.id AND c.status = 'PUBLISHED'
          ) AS commentCount
        FROM circle_posts p
        JOIN accounts a ON a.id = p.author_account_id
        WHERE p.status = :status
        ORDER BY p.created_at DESC, p.id DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM circle_posts p
        WHERE p.status = :status
        """,
            nativeQuery = true)
    Page<PostCardView> findPostCardsByStatus(@Param("status") String status, Pageable pageable);

    @Query(value = """
        SELECT
          p.id AS id,
          p.author_account_id AS authorAccountId,
          a.username AS authorUsername,
          p.title AS title,
          p.content AS content,
          p.status AS status,
          p.created_at AS createdAt,
          p.updated_at AS updatedAt,
          (
            SELECT COUNT(*)
            FROM circle_comments c
            WHERE c.post_id = p.id AND c.status = 'PUBLISHED'
          ) AS commentCount
        FROM circle_posts p
        JOIN accounts a ON a.id = p.author_account_id
        WHERE p.author_account_id = :authorId
        ORDER BY p.created_at DESC, p.id DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM circle_posts p
        WHERE p.author_account_id = :authorId
        """,
            nativeQuery = true)
    Page<PostCardView> findMyPostCards(@Param("authorId") Long authorId, Pageable pageable);

    @Query(value = """
        SELECT
          p.id AS id,
          p.author_account_id AS authorAccountId,
          a.username AS authorUsername,
          p.title AS title,
          p.content AS content,
          p.status AS status,
          p.created_at AS createdAt,
          p.updated_at AS updatedAt,
          (
            SELECT COUNT(*)
            FROM circle_comments c
            WHERE c.post_id = p.id AND c.status = 'PUBLISHED'
          ) AS commentCount
        FROM circle_posts p
        JOIN accounts a ON a.id = p.author_account_id
        WHERE p.id = :postId
        LIMIT 1
        """,
            nativeQuery = true)
    Optional<PostCardView> findPostCardById(@Param("postId") Long postId);
}