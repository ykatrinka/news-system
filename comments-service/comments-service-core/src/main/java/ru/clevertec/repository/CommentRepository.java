package ru.clevertec.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.clevertec.entity.Comment;

@Repository
public interface CommentRepository extends SearchRepository<Comment, Long> {
    Page<Comment> findByNewsId(Long newsId, Pageable pageable);

    void deleteAllByNewsId(Long newsId);
}
