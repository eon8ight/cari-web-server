package com.cari.web.server.repository;

import com.cari.web.server.domain.MediaCreator;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaCreatorRepository extends PagingAndSortingRepository<MediaCreator, Integer> {

    // @formatter:off
    String CREATE_OR_UPDATE_QUERY =
        "insert into tb_media_creator ( " +
        "  name " +
        ") values ( " +
        "  :name " +
        ") " +
        "       on conflict ( name ) " +
        "       do update " +
        "      set name = EXCLUDED.name " +
        "returning media_creator";
    // @formatter:on

    @Modifying
    @Query(CREATE_OR_UPDATE_QUERY)
    int getOrCreate(@Param("name") String name);
}
