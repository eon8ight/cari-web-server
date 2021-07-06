package com.cari.web.server.repository;

import com.cari.web.server.domain.db.UpdateLog;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UpdateLogRepository extends PagingAndSortingRepository<UpdateLog, Integer> {
}
