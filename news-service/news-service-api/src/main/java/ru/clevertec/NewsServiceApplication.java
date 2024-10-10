package ru.clevertec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.clevertec.repository.impl.SearchRepositoryImpl;

@SpringBootApplication
@EnableFeignClients
@EnableJpaRepositories(repositoryBaseClass = SearchRepositoryImpl.class)
public class NewsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsServiceApplication.class, args);
    }
}

