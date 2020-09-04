package com.cari.web.server.service;

import java.util.List;
import java.util.Optional;
import com.cari.web.server.domain.Aesthetic;

public interface IAestheticService {

    Optional<Aesthetic> find(int aesthetic);

    Aesthetic findByUrlSlug(String urlSlug);

    List<Aesthetic> findForAestheticsList();

    List<Aesthetic> findSimilarAesthetics(int aesthetic);
}
