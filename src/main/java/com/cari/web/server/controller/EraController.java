package com.cari.web.server.controller;

import java.util.List;
import com.cari.web.server.domain.db.Era;
import com.cari.web.server.service.EraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EraController {

    @Autowired
    private EraService eraService;

    @GetMapping("/eras")
    public List<Era> findAll() {
        return eraService.findAll();
    }
}
