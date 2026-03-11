package com.labsafety.system.news.repo;

import com.labsafety.system.news.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {

    @Query(value = """
        SELECT
          n.id AS id,
          n.title AS title,
          n.content AS content,
          n.status AS status,
          n.created_at AS createdAt,
          n.updated_at AS updatedAt
        FROM news n
        WHERE n.status = 'PUBLISHED'
        ORDER BY n.created_at DESC, n.id DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM news n
        WHERE n.status = 'PUBLISHED'
        """,
            nativeQuery = true)
    Page<NewsView> findPublished(Pageable pageable);

    @Query(value = """
        SELECT
          n.id AS id,
          n.title AS title,
          n.content AS content,
          n.status AS status,
          n.created_at AS createdAt,
          n.updated_at AS updatedAt
        FROM news n
        WHERE n.status = :status
        ORDER BY n.created_at DESC, n.id DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM news n
        WHERE n.status = :status
        """,
            nativeQuery = true)
    Page<NewsView> findByStatus(@Param("status") String status, Pageable pageable);

    @Query(value = """
        SELECT
          n.id AS id,
          n.title AS title,
          n.content AS content,
          n.status AS status,
          n.created_at AS createdAt,
          n.updated_at AS updatedAt
        FROM news n
        WHERE n.id = :id
        LIMIT 1
        """,
            nativeQuery = true)
    Optional<NewsView> findViewById(@Param("id") Long id);
}