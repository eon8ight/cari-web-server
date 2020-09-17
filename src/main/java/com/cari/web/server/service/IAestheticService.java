package com.cari.web.server.service;

import java.util.List;
import java.util.Optional;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.domain.Media;
import com.cari.web.server.domain.Website;
import com.cari.web.server.dto.SimilarAesthetic;
import org.springframework.data.domain.Page;

public interface IAestheticService {

    Page<Aesthetic> findAll(Optional<Integer> page, Optional<String> sortField,
            Optional<Boolean> asc);

    Aesthetic findByUrlSlug(String urlSlug);

    List<Media> findMedia(int aesthetic);

    List<Website> findWebsites(int aesthetic);

    List<SimilarAesthetic> findSimilarAesthetics(int aesthetic);
}
