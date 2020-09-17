package com.cari.web.server.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.domain.Media;
import com.cari.web.server.domain.Website;
import com.cari.web.server.dto.SimilarAesthetic;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.service.IAestheticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AestheticService implements IAestheticService {

    private static final int MAX_PER_PAGE = 20;

    @Autowired
    private AestheticRepository repository;

    @Override
    public Page<Aesthetic> findAll(Optional<Integer> page, Optional<String> sortField,
            Optional<Boolean> asc) {
        PageRequest pageRequest;

        if (sortField.isPresent()) {
            Sort.Direction sortDirection =
                    asc.orElse(false) ? Sort.Direction.ASC : Sort.Direction.DESC;

            Sort sort = Sort.by(new Sort.Order(sortDirection, sortField.get()));
            pageRequest = PageRequest.of(page.orElse(0), MAX_PER_PAGE, sort);
        } else {
            pageRequest = PageRequest.of(page.orElse(0), MAX_PER_PAGE);
        }

        return repository.findAll(pageRequest);
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
    public List<SimilarAesthetic> findSimilarAesthetics(int aesthetic) {
        List<Object[]> obj = repository.findSimilarAesthetics(aesthetic);

        List<SimilarAesthetic> similarAesthetics =
                obj.stream().map(o -> new SimilarAesthetic((Aesthetic) o[0], (String) o[1]))
                        .collect(Collectors.toList());

        return similarAesthetics;
    }
}
