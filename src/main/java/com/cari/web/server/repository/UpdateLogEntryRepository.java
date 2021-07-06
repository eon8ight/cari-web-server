package com.cari.web.server.repository;

import com.cari.web.server.domain.db.UpdateLogEntry;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UpdateLogEntryRepository
        extends PagingAndSortingRepository<UpdateLogEntry, Integer> {
}
