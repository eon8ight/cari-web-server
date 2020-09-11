package com.cari.web.server.service;

import com.cari.web.server.dto.ArenaApiResponse;

public interface IArenaApiService {

    ArenaApiResponse findInitialBlocksForPagination(int aesthetic);

    ArenaApiResponse findBlocksForPagination(int aesthetic, int page);
}
