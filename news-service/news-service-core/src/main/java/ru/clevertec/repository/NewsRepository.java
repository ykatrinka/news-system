package ru.clevertec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clevertec.entity.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
}
