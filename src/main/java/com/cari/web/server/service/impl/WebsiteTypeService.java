package com.cari.web.server.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.cari.web.server.domain.WebsiteType;
import com.cari.web.server.repository.WebsiteTypeRepository;
import com.cari.web.server.service.IWebsiteTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class WebsiteTypeService implements IWebsiteTypeService {
    @Autowired
    private WebsiteTypeRepository websiteTypeRepository;

    @Override
    public List<WebsiteType> findAll() {
        return StreamSupport.stream(
                websiteTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "label")).spliterator(),
                false).collect(Collectors.toList());
    }
}
