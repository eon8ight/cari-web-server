package com.cari.web.server.service;

import java.util.List;
import com.cari.web.server.domain.db.Era;

public interface EraService {

    List<Era> findAll();
}
