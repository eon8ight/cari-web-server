package com.cari.web.server.service;

import java.util.List;

import com.cari.web.server.domain.Aesthetic;

public interface IAestheticService {

    Aesthetic findByUrlSlug(String urlSlug);

    List<String[]> findAllNames();
}
