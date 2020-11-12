package com.cari.web.server.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.dto.AestheticName;
import com.cari.web.server.dto.arena.ArenaApiResponse;
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
    public Page<Aesthetic> findForList(@RequestParam Map<String, String> filters) {
        Page<Aesthetic> aesthetics = aestheticService.findForList(filters);

        aesthetics.forEach(aesthetic -> {
            aesthetic.setWebsites(null);
            aesthetic.setMedia(null);
            aesthetic.setSimilarAesthetics(null);
        });

        return aesthetics;
    }

    @GetMapping("/aesthetic/findForPage/{urlSlug}")
    public Aesthetic findForPage(@PathVariable String urlSlug) {
        Aesthetic aesthetic = aestheticService.findForPage(urlSlug);

        if (aesthetic.getMediaSourceUrl() != null) {
            ArenaApiResponse galleryContent =
                    arenaApiService.findInitialBlocksForPagination(aesthetic);

            aesthetic.setGalleryContent(galleryContent);
        }

        return aesthetic;
    }

    @GetMapping("/aesthetic/findForEdit/{aesthetic}")
    public Aesthetic findForEdit(@PathVariable int aesthetic) {
        return aestheticService.findForEdit(aesthetic);
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

    @GetMapping("/aesthetic/names")
    public List<AestheticName> findNames(@RequestParam Optional<String> query) {
        return aestheticService.findNames(query);
    }
}
