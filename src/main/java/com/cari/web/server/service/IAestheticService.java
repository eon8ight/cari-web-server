package com.cari.web.server.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.dto.AestheticName;
import org.springframework.data.domain.Page;

public interface IAestheticService {
    Page<Aesthetic> findAll(Map<String, String> filters);

    Aesthetic findByUrlSlug(String urlSlug);

    Aesthetic findByPk(int aesthetic);

    Aesthetic find(int aesthetic);

    List<AestheticName> findNames(Optional<String> query);
}
