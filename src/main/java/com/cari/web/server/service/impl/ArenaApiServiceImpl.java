package com.cari.web.server.service.impl;

import java.util.Optional;
import com.cari.web.server.dto.response.arena.ArenaApiResponse;
import com.cari.web.server.service.ArenaApiService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ArenaApiServiceImpl implements ArenaApiService {

    private static final int MAX_PER_PAGE = 15;

    @Override
    public Optional<ArenaApiResponse> findBlocksForPagination(String mediaSourceUrl, int page) {
        StringBuilder urlBuilder = new StringBuilder(mediaSourceUrl).append("?page=").append(page)
                .append("&per=").append(MAX_PER_PAGE);

        Optional<ArenaApiResponse> response;

        try {
            RestTemplate restTemplate = new RestTemplate();

            response = Optional
                    .of(restTemplate.getForObject(urlBuilder.toString(), ArenaApiResponse.class));
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                response = Optional.empty();
            } else {
                throw ex;
            }
        }

        return response;
    }
}
