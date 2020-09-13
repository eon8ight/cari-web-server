package com.cari.web.server.controller;

import java.util.MissingResourceException;
import java.util.Optional;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.dto.ArenaApiResponse;
import com.cari.web.server.service.IAestheticService;
import com.cari.web.server.service.IArenaApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AestheticController {

    @Autowired
    private IAestheticService aestheticService;

    @Autowired
    private IArenaApiService arenaApiService;

    @GetMapping("/aesthetic/findForList")
    public Page<Aesthetic> findAll(@RequestParam Optional<Integer> page) {
        return aestheticService.findAll(page);
    }

    @GetMapping("/aesthetic/findForPage/{urlSlug}")
    public Aesthetic find(@PathVariable String urlSlug,
            @RequestParam Optional<Boolean> includeSimilarAesthetics,
            @RequestParam Optional<Boolean> includeMedia,
            @RequestParam Optional<Boolean> includeGalleryContent) {
        Aesthetic aesthetic = aestheticService.findByUrlSlug(urlSlug);
        int pkAesthetic = aesthetic.getAesthetic();

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
