package com.cari.web.server.repository;

import com.cari.web.server.domain.Website;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsiteRepository extends CrudRepository<Website, Integer> {

    // @formatter:off
    String GET_OR_CREATE_QUERY =
        "insert into tb_website ( " +
        "  url, " +
        "  website_type " +
        ") values ( " +
        "  :url, " +
        "  :websiteType " +
        ") " +
        "       on conflict ( url ) " +
        "       do update " +
        "      set url = EXCLUDED.url " +
        "returning website";
    // @formatter:on

    @Modifying
    @Query(GET_OR_CREATE_QUERY)
    int getOrCreate(@Param("url") String url, @Param("websiteType") int websiteType);
}
