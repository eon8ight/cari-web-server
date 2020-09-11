package com.cari.web.server.controller;

import java.util.List;
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

    @GetMapping("/aesthetic/find")
    public Page<Aesthetic> findAll(@RequestParam(required = false) Integer page) {
        return aestheticService.findAll(Optional.ofNullable(page));
    }

    @GetMapping("/aesthetic/find/{aesthetic}")
    public Aesthetic find(@PathVariable int aesthetic) {
        Optional<Aesthetic> aestheticOptional = aestheticService.find(aesthetic);

        if (!aestheticOptional.isEmpty()) {
            Aesthetic aestheticObj = aestheticOptional.get();

            aestheticObj.setSimilarAesthetics(aestheticService.findSimilarAesthetics(aesthetic));
            aestheticObj.setMedia(aestheticService.findMedia(aesthetic));
            aestheticObj.setWebsites(aestheticService.findWebsites(aesthetic));

            return aestheticObj;
        }

        return null;
    }

    @GetMapping("/aesthetic/findByUrlSlug/{urlSlug}")
    public Aesthetic find(@PathVariable String urlSlug) {
        Aesthetic aesthetic = aestheticService.findByUrlSlug(urlSlug);
        int pkAesthetic = aesthetic.getAesthetic();

        aesthetic.setSimilarAesthetics(aestheticService.findSimilarAesthetics(pkAesthetic));
        aesthetic.setMedia(aestheticService.findMedia(pkAesthetic));
        aesthetic.setWebsites(aestheticService.findWebsites(pkAesthetic));
        aesthetic.setGalleryContent(arenaApiService.findInitialBlocksForPagination(pkAesthetic));

        return aesthetic;
    }

    @GetMapping("/aesthetic/findSimilarAesthetics/{aesthetic}")
    public List<Aesthetic> findSimilarAesthetics(@PathVariable int aesthetic) {
        return aestheticService.findSimilarAesthetics(aesthetic);
    }

    @GetMapping("/aesthetic/findGalleryContent/{aesthetic}")
    public ArenaApiResponse findGalleryContent(@PathVariable int aesthetic,
            @RequestParam int page) {
        return arenaApiService.findBlocksForPagination(aesthetic, page);
    }
}
