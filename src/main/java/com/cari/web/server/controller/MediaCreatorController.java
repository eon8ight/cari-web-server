package com.cari.web.server.controller;

import java.util.List;
import com.cari.web.server.domain.MediaCreator;
import com.cari.web.server.service.MediaCreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaCreatorController {

    @Autowired
    private MediaCreatorService mediaCreatorService;

    @GetMapping("/mediaCreators")
    public List<MediaCreator> findAll() {
        return mediaCreatorService.findAll();
    }
}
