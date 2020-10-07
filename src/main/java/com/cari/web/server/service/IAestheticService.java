package com.cari.web.server.service;

import java.util.Map;
import com.cari.web.server.domain.Aesthetic;
import org.springframework.data.domain.Page;

public interface IAestheticService {
    Page<Aesthetic> findAll(Map<String, String> filters);

    Aesthetic findByUrlSlug(String urlSlug);
}
