package com.cari.web.server.service;

import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.dto.ArenaApiResponse;

public interface IArenaApiService {
    ArenaApiResponse findInitialBlocksForPagination(Aesthetic aesthetic);

    ArenaApiResponse findBlocksForPagination(Aesthetic aesthetic, int page);
}
