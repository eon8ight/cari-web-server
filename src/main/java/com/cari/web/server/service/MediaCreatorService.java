package com.cari.web.server.service;

import java.util.List;
import com.cari.web.server.domain.db.MediaCreator;

public interface MediaCreatorService {

    List<MediaCreator> findAll();
}
