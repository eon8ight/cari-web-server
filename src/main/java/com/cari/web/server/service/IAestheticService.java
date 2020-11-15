package com.cari.web.server.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.cari.web.server.domain.Aesthetic;
import org.springframework.data.domain.Page;

public interface IAestheticService {
    Page<Aesthetic> findForList(Map<String, String> filters);

    Aesthetic findForPage(String urlSlug);

    Aesthetic findForEdit(int aesthetic);

    Aesthetic find(int aesthetic);

    List<Aesthetic> findNames(Optional<String> query);

    String edit(Aesthetic aesthetic);
}
