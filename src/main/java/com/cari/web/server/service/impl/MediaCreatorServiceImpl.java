package com.cari.web.server.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.cari.web.server.domain.db.MediaCreator;
import com.cari.web.server.repository.MediaCreatorRepository;
import com.cari.web.server.service.MediaCreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class MediaCreatorServiceImpl implements MediaCreatorService {

    @Autowired
    private MediaCreatorRepository mediaCreatorRepository;

    @Override
    public List<MediaCreator> findAll() {
        return StreamSupport.stream(
                mediaCreatorRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).spliterator(),
                false).collect(Collectors.toList());
    }
}
