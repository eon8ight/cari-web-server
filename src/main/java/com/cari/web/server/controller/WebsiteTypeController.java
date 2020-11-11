package com.cari.web.server.controller;

import java.util.List;
import com.cari.web.server.domain.WebsiteType;
import com.cari.web.server.service.IWebsiteTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebsiteTypeController {
    @Autowired
    private IWebsiteTypeService websiteTypeService;

    @GetMapping("/websiteTypes")
    public List<WebsiteType> findAll() {
        return websiteTypeService.findAll();
    }
}
