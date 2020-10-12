package com.cari.web.server.controller;

import java.util.Map;
import com.cari.web.server.domain.Aesthetic;
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
    @Autowired
    private IAestheticService aestheticService;

    @Autowired
    private IArenaApiService arenaApiService;

    @GetMapping("/aesthetic/findForList")
    public ResponseEntity<Page<Aesthetic>> findAll(@RequestParam Map<String, String> filters) {
        Page<Aesthetic> aesthetics = aestheticService.findAll(filters);

        aesthetics.forEach(aesthetic -> {
            aesthetic.setWebsites(null);
            aesthetic.setMedia(null);
            aesthetic.setSimilarAesthetics(null);
        });

        ResponseEntity<Page<Aesthetic>> response =
                ResponseEntity.status(HttpStatus.OK).body(aesthetics);
        return response;
    }

    @GetMapping("/aesthetic/findForPage/{urlSlug}")
    public Aesthetic find(@PathVariable String urlSlug) {
        Aesthetic aesthetic = aestheticService.findByUrlSlug(urlSlug);

        if (aesthetic.getMediaSourceUrl() != null) {
            ArenaApiResponse galleryContent = arenaApiService.findInitialBlocksForPagination(aesthetic);
            aesthetic.setGalleryContent(galleryContent);
        }

        return aesthetic;
    }

    @GetMapping("/aesthetic/findGalleryContent/{aesthetic}")
    public ArenaApiResponse findGalleryContent(@PathVariable int aesthetic,
            @RequestParam int page) {
        Aesthetic aestheticObj = aestheticService.find(aesthetic);

        if (aestheticObj.getMediaSourceUrl() == null) {
            return null;
        }

        ArenaApiResponse arenaApiResponse =
                arenaApiService.findBlocksForPagination(aestheticObj, page);

        return arenaApiResponse;
    }
}
