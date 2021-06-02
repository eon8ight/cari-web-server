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
            HttpStatus statusCode = ex.getStatusCode();
            String errorMessage;

            switch (statusCode) {
                case NOT_FOUND:
                    errorMessage =
                            "Please either remove the Are.na link from the aesthetic or update it to an existing Are.na.";

                    break;
                case UNAUTHORIZED:
                    errorMessage =
                            "This usually means the Are.na is private. Please make it public or remove it from the aesthetic.";

                    break;
                default:
                    errorMessage = "This is an unhandled exception.";
                    break;
            }

            // @formatter:off
            ArenaApiResponse responseObject =  ArenaApiResponse.builder()
                    .errorStatusCode(statusCode.value())
                    .errorMessage(errorMessage)
                    .build();
            // @formatter:on

            response = Optional.of(responseObject);
        }

        return response;
    }
}
