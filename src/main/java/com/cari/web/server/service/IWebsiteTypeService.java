package com.cari.web.server.service;

import java.util.List;
import com.cari.web.server.domain.WebsiteType;

public interface IWebsiteTypeService {
    List<WebsiteType> findAll();
}
