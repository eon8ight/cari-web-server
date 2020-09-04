package com.cari.web.server.service.impl;

import java.util.List;
import java.util.Optional;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.service.IAestheticService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Aesthetic findByUrlSlug(String urlSlug) {
        return repository.findByUrlSlug(urlSlug);
    }

    @Override
    public List<Aesthetic> findForAestheticsList() {
        return repository.findForAestheticsList();
    }

    @Override
    public List<Aesthetic> findSimilarAesthetics(int aesthetic) {
        return repository.findSimilarAesthetics(aesthetic);
    }
}
