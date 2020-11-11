package com.cari.web.server.repository;

import com.cari.web.server.domain.WebsiteType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsiteTypeRepository extends PagingAndSortingRepository<WebsiteType, Integer> {
    // This interface purposely left blank
}
