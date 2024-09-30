package ru.clevertec.conf;

import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "ru.clevertec.feignclient")
public class NewsFeignConfiguration {
}
