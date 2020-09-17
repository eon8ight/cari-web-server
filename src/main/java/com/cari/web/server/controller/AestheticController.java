package com.cari.web.server.controller;

import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.domain.CariError;
import com.cari.web.server.dto.ArenaApiResponse;
import com.cari.web.server.service.IAestheticService;
import com.cari.web.server.service.IArenaApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AestheticController {

    private static final List<String> VALID_SORT_FIELDS = Arrays.asList("name", "startYear");

    @Autowired
    private IAestheticService aestheticService;

    @Autowired
    private IArenaApiService arenaApiService;

    @GetMapping("/aesthetic/findForList")
    public ResponseEntity<Object> findAll(@RequestParam Optional<Integer> page,
            @RequestParam Optional<String> sortField, @RequestParam Optional<Boolean> asc) {
        if (sortField.isPresent() && !VALID_SORT_FIELDS.contains(sortField.get())) {
            CariError error = new CariError("sortField must be one of the following: "
                    + String.join(", ", VALID_SORT_FIELDS.toArray(new String[0])));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        Page<Aesthetic> aesthetics = aestheticService.findAll(page, sortField, asc);
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).body(aesthetics);
        return response;
    }

    @GetMapping("/aesthetic/findForPage/{urlSlug}")
    public Aesthetic find(@PathVariable String urlSlug,
            @RequestParam Optional<Boolean> includeSimilarAesthetics,
            @RequestParam Optional<Boolean> includeMedia,
            @RequestParam Optional<Boolean> includeGalleryContent) {
        Aesthetic aesthetic = aestheticService.findByUrlSlug(urlSlug);
        int pkAesthetic = aesthetic.getAesthetic();

        aesthetic.setWebsites(aestheticService.findWebsites(pkAesthetic));

        if (includeSimilarAesthetics.orElse(false)) {
            aesthetic.setSimilarAesthetics(aestheticService.findSimilarAesthetics(pkAesthetic));
        }

        if (includeMedia.orElse(false)) {
            aesthetic.setMedia(aestheticService.findMedia(pkAesthetic));
        }

        if (includeGalleryContent.orElse(false)) {
            ArenaApiResponse galleryContent;

            try {
                galleryContent = arenaApiService.findInitialBlocksForPagination(pkAesthetic);
            } catch (MissingResourceException ex) {
                galleryContent = null;
            }

            aesthetic.setGalleryContent(galleryContent);
        }

        return aesthetic;
    }

    @GetMapping("/aesthetic/findGalleryContent/{aesthetic}")
    public ArenaApiResponse findGalleryContent(@PathVariable int aesthetic,
            @RequestParam int page) {
        ArenaApiResponse arenaApiResponse;

        try {
            arenaApiResponse = arenaApiService.findBlocksForPagination(aesthetic, page);
        } catch (MissingResourceException ex) {
            arenaApiResponse = null;
        }

        return arenaApiResponse;
    }
}
