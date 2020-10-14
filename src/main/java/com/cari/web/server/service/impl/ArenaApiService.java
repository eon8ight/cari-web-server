package com.cari.web.server.service.impl;

import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.dto.arena.ArenaApiResponse;
import com.cari.web.server.service.IArenaApiService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ArenaApiService implements IArenaApiService {
    private static final int MAX_PER_PAGE = 15;

    private ArenaApiResponse callApi(String url) {
        RestTemplate restTemplate = new RestTemplate();
        ArenaApiResponse response = restTemplate.getForObject(url, ArenaApiResponse.class);

        return response;
    }

    @Override
    public ArenaApiResponse findInitialBlocksForPagination(Aesthetic aesthetic) {
        StringBuilder urlBuilder = new StringBuilder(aesthetic.getMediaSourceUrl())
                .append("?page=1&per=").append(MAX_PER_PAGE);

        ArenaApiResponse response;

        try {
            response = callApi(urlBuilder.toString());
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                response = null;
            } else {
                throw ex;
            }
        }

        return response;
    }

    public ArenaApiResponse findBlocksForPagination(Aesthetic aesthetic, int page) {
        StringBuilder urlBuilder = new StringBuilder(aesthetic.getMediaSourceUrl()).append("?page=")
                .append(page).append("&per=").append(MAX_PER_PAGE);

        return callApi(urlBuilder.toString());
    }
}
