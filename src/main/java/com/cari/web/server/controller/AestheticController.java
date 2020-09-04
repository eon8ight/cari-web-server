package com.cari.web.server.controller;

import java.util.List;
import java.util.Optional;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.service.IAestheticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AestheticController {

    @Autowired
    private IAestheticService aestheticService;

    @GetMapping("/aesthetic/findForAestheticsList")
    public List<Aesthetic> findForAestheticsList() {
        return aestheticService.findForAestheticsList();
    }

    @GetMapping("/aesthetic/find/{aesthetic}")
    public Optional<Aesthetic> find(@PathVariable int aesthetic) {
        return aestheticService.find(aesthetic);
    }

    @GetMapping("/aesthetic/findByUrlSlug/{urlSlug}")
    public Aesthetic find(@PathVariable String urlSlug) {
        return aestheticService.findByUrlSlug(urlSlug);
    }

    @GetMapping("/aesthetic/findSimilarAesthetics/{aesthetic}")
    public List<Aesthetic> findSimilarAesthetics(@PathVariable int aesthetic) {
        return aestheticService.findSimilarAesthetics(aesthetic);
    }
}
