package ru.clevertec.feignclient.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.feignclient.NewsFeignClient;
import ru.clevertec.feignclient.NewsFeignService;

@Service
@RequiredArgsConstructor
public class NewsFeignServiceImpl implements NewsFeignService {

    private final NewsFeignClient newsFeignClient;

    @Override
    public boolean isNotExistsNews(final Long newsId) {
        return !newsFeignClient.isExistsNews(newsId);
    }
}
