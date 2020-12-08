package com.cari.web.server.service;

import java.util.List;
import com.cari.web.server.domain.db.WebsiteType;

public interface WebsiteTypeService {

    List<WebsiteType> findAll();
}
