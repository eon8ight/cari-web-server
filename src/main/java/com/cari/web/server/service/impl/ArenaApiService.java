package com.cari.web.server.service.impl;

import java.util.MissingResourceException;
import com.cari.web.server.dto.ArenaApiResponse;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.service.IArenaApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ArenaApiService implements IArenaApiService {
    private static final String ARENA_API_BASE_URL = "https://api.are.na/v2/";
    private static final String ARENA_API_CHANNELS_BASE_URL = ARENA_API_BASE_URL + "channels";

    private static final int MAX_PER_PAGE = 15;

    @Autowired
    private AestheticRepository repository;

    private String getUrl(int aesthetic) throws MissingResourceException {
        String arenaUrl = repository.getWebsiteUrlByType(aesthetic, 1);

        if (arenaUrl == null) {
            StringBuilder errorMessageBuilder = new StringBuilder("Aesthetic ").append(aesthetic)
                    .append(" does not have an Are.na website.");

            throw new MissingResourceException(errorMessageBuilder.toString(), "AestheticWebsite",
                    "website");
        }

        String[] urlParts = arenaUrl.split("/");
        String slug = urlParts[urlParts.length - 1];
        return ARENA_API_CHANNELS_BASE_URL + "/" + slug;
    }

    private ArenaApiResponse callApi(String url) {
        RestTemplate restTemplate = new RestTemplate();
        ArenaApiResponse response = restTemplate.getForObject(url, ArenaApiResponse.class);

        return response;
    }

    @Override
    public ArenaApiResponse findInitialBlocksForPagination(int aesthetic)
            throws MissingResourceException {
        String url;

        try {
            url = getUrl(aesthetic);
        } catch (MissingResourceException ex) {
            throw ex;
        }

        StringBuilder urlBuilder =
                new StringBuilder(url).append("?page=1&per=").append(MAX_PER_PAGE);

        ArenaApiResponse response;

        try {
            response = callApi(urlBuilder.toString());
        } catch (HttpClientErrorException ex) {
            if(ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                response = null;
            } else {
                throw ex;
            }
        }

        return response;
    }

    public ArenaApiResponse findBlocksForPagination(int aesthetic, int page)
            throws MissingResourceException {
        String url;

        try {
            url = getUrl(aesthetic);
        } catch (MissingResourceException ex) {
            throw ex;
        }

        StringBuilder urlBuilder = new StringBuilder(url).append("/contents?page=").append(page)
                .append("&per=").append(MAX_PER_PAGE);

        return callApi(urlBuilder.toString());
    }
}
