package com.cari.web.server.service.impl;

import java.util.List;
import com.cari.web.server.domain.db.Era;
import com.cari.web.server.repository.EraRepository;
import com.cari.web.server.service.EraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EraServiceImpl implements EraService {

    @Autowired
    private EraRepository eraRepository;

    public List<Era> findAll() {
        return eraRepository.findAllWithEraSpecifier();
    }
}
