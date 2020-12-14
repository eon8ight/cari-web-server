package com.cari.web.server.repository;

import com.cari.web.server.domain.db.CariFile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends CrudRepository<CariFile, Integer> {
    // This interface purposely left blank
}
