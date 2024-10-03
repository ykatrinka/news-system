package ru.clevertec.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.clevertec.cache.CustomCache;
import ru.clevertec.entity.Comment;

import java.util.Optional;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class CommentsCacheAspect {

    private final CustomCache<Long, Comment> cache;

    @Pointcut("execution(* ru.clevertec.repository.CommentRepository.save(..))")
    public void saveCommentMethod() {
    }

    @Pointcut("execution(* ru.clevertec.repository.CommentRepository.delete(..))")
    public void deleteCommentMethod() {
    }

    @Pointcut("execution(* ru.clevertec.repository.CommentRepository.findById(..))")
    public void findByIdCommentMethod() {
    }

    @Around("saveCommentMethod()")
    public Object aroundSaveCommentMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Comment comment = (Comment) joinPoint.getArgs()[0];
        Comment savedComment = (Comment) joinPoint.proceed();

        if (savedComment != null && savedComment.getId() != null) {
            cache.put(comment.getId(), comment);
            log.info("Comment with id {} was added to cache", savedComment.getId());
        }
        return savedComment;
    }

    @Around("deleteCommentMethod()")
    public Object aroundDeleteCommentMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Long commentId = ((Comment) joinPoint.getArgs()[0]).getId();
        joinPoint.proceed();
        cache.delete(commentId);
        log.info("Comment with id {} was removed from cache", commentId);
        return commentId;
    }


    @Around("findByIdCommentMethod()")
    public Object aroundFindByIdCommentMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Long commentId = (Long) joinPoint.getArgs()[0];
        Optional<Comment> foundComment = cache.get(commentId);
        foundComment.ifPresent(comment ->
                log.info("Comment with id {} was got from cache", commentId));

        if (foundComment.isEmpty()) {
            foundComment = (Optional<Comment>) joinPoint.proceed();
            foundComment.ifPresent(comment -> cache.put(commentId, comment));
            log.info("Comment with id {} was added from cache", commentId);
        }

        return foundComment;
    }
}
