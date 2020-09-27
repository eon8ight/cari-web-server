package com.cari.web.server.service.impl;

import java.util.Map;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.service.IAestheticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AestheticService implements IAestheticService {

    private static final int MAX_PER_PAGE = 20;

    private static final String FILTER_PAGE = "page";
    private static final String FILTER_SORT_FIELD = "sortField";
    private static final String FILTER_ASC = "asc";
    private static final String FILTER_KEYWORD = "keyword";

    @Autowired
    private AestheticRepository repository;

    @Override
    public Page<Aesthetic> findAll(Map<String, String> filters) {
        int page = Integer.parseInt(filters.getOrDefault(FILTER_PAGE, "0"));
        String sortField = filters.get(FILTER_SORT_FIELD);
        boolean asc = Boolean.parseBoolean(filters.getOrDefault(FILTER_ASC, "true"));
        String keyword = filters.get(FILTER_KEYWORD);

        PageRequest pageRequest;

        if (sortField != null) {
            Sort.Direction sortDirection = asc ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = Sort.by(new Sort.Order(sortDirection, sortField));
            pageRequest = PageRequest.of(page, MAX_PER_PAGE, sort);
        } else {
            pageRequest = PageRequest.of(page, MAX_PER_PAGE);
        }

        Page<Aesthetic> aesthetics;

        if (keyword != null) {
            Aesthetic aesthetic = new Aesthetic();
            aesthetic.setName(keyword);
            aesthetic.setDescription(keyword);

            ExampleMatcher matcher = ExampleMatcher.matchingAny()
                    .withMatcher("name", name -> name.contains().ignoreCase())
                    .withMatcher("description", description -> description.contains().ignoreCase());

            Example<Aesthetic> example = Example.of(aesthetic, matcher);
            aesthetics = repository.findAll(example, pageRequest);
        } else {
            aesthetics = repository.findAll(pageRequest);
        }

        return aesthetics;
    }

    @Override
    public Aesthetic findByUrlSlug(String urlSlug) {
        return repository.findByUrlSlug(urlSlug);
    }
}
