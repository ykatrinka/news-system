package ru.clevertec.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.clevertec.cache.CustomCache;
import ru.clevertec.entity.News;

import java.util.Optional;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class NewsCacheAspect {

    private final CustomCache<Long, News> cache;

    @Pointcut("execution(* ru.clevertec.repository.NewsRepository.save(..))")
    public void saveNewsMethod() {
    }

    @Pointcut("execution(* ru.clevertec.repository.NewsRepository.delete(..))")
    public void deleteNewsMethod() {
    }

    @Pointcut("execution(* ru.clevertec.repository.NewsRepository.findById(..))")
    public void findByIdNewsMethod() {
    }

    @Around("saveNewsMethod()")
    public Object aroundSaveNewsMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        News news = (News) joinPoint.getArgs()[0];
        News savedNews = (News) joinPoint.proceed();

        if (savedNews != null && savedNews.getId() != null) {
            cache.put(news.getId(), news);
            log.info("News with id {} was added to cache", savedNews.getId());
        }
        return savedNews;
    }

    @Around("deleteNewsMethod()")
    public Object aroundDeleteNewsMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Long newsId = ((News) joinPoint.getArgs()[0]).getId();
        joinPoint.proceed();
        cache.delete(newsId);
        log.info("News with id {} was removed from cache", newsId);
        return newsId;
    }


    @Around("findByIdNewsMethod()")
    public Object aroundFindByIdNewsMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Long newsId = (Long) joinPoint.getArgs()[0];
        Optional<News> foundNews = cache.get(newsId);
        foundNews.ifPresent(comment ->
                log.info("News with id {} was got from cache", newsId));

        if (foundNews.isEmpty()) {
            foundNews = (Optional<News>) joinPoint.proceed();
            foundNews.ifPresent(news -> cache.put(newsId, news));
            log.info("News with id {} was added from cache", newsId);
        }

        return foundNews;
    }
}
