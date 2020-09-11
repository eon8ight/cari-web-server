package com.cari.web.server.service.impl;

import java.util.List;
import java.util.Optional;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.domain.Media;
import com.cari.web.server.domain.Website;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.service.IAestheticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AestheticService implements IAestheticService {

    @Autowired
    private AestheticRepository repository;

    @Override
    public Optional<Aesthetic> find(int aesthetic) {
        return repository.findById(aesthetic);
    }

    @Override
    public Page<Aesthetic> findAll(Optional<Integer> page) {
        return repository.findAll(PageRequest.of(page.orElse(0), 15));
    }

    @Override
    public Aesthetic findByUrlSlug(String urlSlug) {
        return repository.findByUrlSlug(urlSlug);
    }

    @Override
    public List<Media> findMedia(int aesthetic) {
        return repository.findMedia(aesthetic);
    }

    @Override
    public List<Website> findWebsites(int aesthetic) {
        return repository.findWebsites(aesthetic);
    }

    @Override
    public List<Aesthetic> findSimilarAesthetics(int aesthetic) {
        return repository.findSimilarAesthetics(aesthetic);
    }
}
