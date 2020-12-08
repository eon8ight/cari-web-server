package com.cari.web.server.service;

import java.util.Optional;
import com.cari.web.server.dto.response.arena.ArenaApiResponse;

public interface ArenaApiService {

    Optional<ArenaApiResponse> findBlocksForPagination(String mediaSourceUrl, int page);
}
