package ru.clevertec;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.clevertec.lucene.Indexer;
import ru.clevertec.repository.impl.SearchRepositoryImpl;

@SpringBootApplication
@EnableFeignClients
@EnableJpaRepositories(repositoryBaseClass = SearchRepositoryImpl.class)
public class NewsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsServiceApplication.class, args);
    }

    @Bean
    public ApplicationRunner buildIndex(Indexer indexer) {
        return (ApplicationArguments args) ->
                indexer.indexPersistedData("ru.clevertec.entity.News");
    }
}

