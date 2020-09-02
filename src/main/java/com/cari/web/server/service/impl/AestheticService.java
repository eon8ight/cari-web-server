package com.cari.web.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.service.IAestheticService;

@Service
public class AestheticService implements IAestheticService {

    @Autowired
    private AestheticRepository repository;

    @Override
    public Aesthetic findByUrlSlug(String urlSlug) {
        return repository.findByUrlSlug(urlSlug);
    }

    @Override
    public List<String[]> findAllNames() {
        return repository.findAllNames();
    }
}
