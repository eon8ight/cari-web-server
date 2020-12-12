package com.cari.web.server.repository;

import com.cari.web.server.domain.db.File;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends CrudRepository<File, Integer> {
    // This interface purposely left blank
}
