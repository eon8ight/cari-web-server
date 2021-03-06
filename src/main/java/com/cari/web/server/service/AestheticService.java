package com.cari.web.server.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.dto.request.AestheticEditRequest;
import com.cari.web.server.dto.response.CariResponse;
import org.springframework.data.domain.Page;

public interface AestheticService {
    Page<Aesthetic> findForList(Map<String, String> filters);

    Optional<Aesthetic> findForPage(String urlSlug);

    Optional<Aesthetic> findForEdit(int aesthetic);

    Optional<Aesthetic> find(int aesthetic);

    List<Aesthetic> findNames(Optional<String> query);

    List<Aesthetic> findDraft();

    CariResponse createOrUpdate(AestheticEditRequest aesthetic);
}
