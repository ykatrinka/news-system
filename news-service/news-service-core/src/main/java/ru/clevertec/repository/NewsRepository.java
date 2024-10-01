package ru.clevertec.repository;

import org.springframework.stereotype.Repository;
import ru.clevertec.entity.News;

@Repository
public interface NewsRepository extends SearchRepository<News, Long> {
}
