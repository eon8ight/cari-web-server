package com.cari.web.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.service.IAestheticService;

@RestController
public class AestheticController {

    @Autowired
    private IAestheticService aestheticService;

    @GetMapping("/aesthetics/findAllNames")
    public List<String[]> findAllNames() {
        return aestheticService.findAllNames();
    }

    @GetMapping("/aesthetic/{urlSlug}")
    public Aesthetic findByUrlSlug(@PathVariable String urlSlug) {
        return aestheticService.findByUrlSlug(urlSlug);
    }
}
