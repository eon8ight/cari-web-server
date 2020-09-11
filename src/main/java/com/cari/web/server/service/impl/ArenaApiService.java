package com.cari.web.server.service.impl;

import com.cari.web.server.dto.ArenaApiResponse;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.service.IArenaApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ArenaApiService implements IArenaApiService {

    private static final int MAX_PER_PAGE = 15;

    @Autowired
    private AestheticRepository repository;

    private String getUrlSlug(int aesthetic) {
        String arenaUrl = repository.getWebsiteUrlByType(aesthetic, 1);
        String[] urlParts = arenaUrl.split("/");
        String slug = urlParts[urlParts.length - 1];
        return slug;
    }

    @Override
    public ArenaApiResponse findInitialBlocksForPagination(int aesthetic) {
        String slug = getUrlSlug(aesthetic);
        StringBuilder urlBuilder = new StringBuilder("https://api.are.na/v2/channels/").append(slug)
                .append("?page=1&per=").append(MAX_PER_PAGE);

        RestTemplate restTemplate = new RestTemplate();
        ArenaApiResponse response =
                restTemplate.getForObject(urlBuilder.toString(), ArenaApiResponse.class);

        return response;
    }

    public ArenaApiResponse findBlocksForPagination(int aesthetic, int page) {
        String slug = getUrlSlug(aesthetic);
        StringBuilder urlBuilder = new StringBuilder("https://api.are.na/v2/channels/").append(slug)
                .append("/contents?page=").append(page).append("&per=").append(MAX_PER_PAGE);

        RestTemplate restTemplate = new RestTemplate();
        ArenaApiResponse response =
                restTemplate.getForObject(urlBuilder.toString(), ArenaApiResponse.class);
        
        return response;
    }
}
