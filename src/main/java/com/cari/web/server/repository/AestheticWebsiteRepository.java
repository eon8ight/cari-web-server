package com.cari.web.server.repository;

import com.cari.web.server.domain.AestheticWebsite;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AestheticWebsiteRepository extends CrudRepository<AestheticWebsite, Integer> {

    // @formatter:off
    String GET_OR_CREATE_QUERY =
        "insert into tb_aesthetic_website ( " +
        "  aesthetic, " +
        "  url, " +
        "  website_type " +
        ") values ( " +
        "  :aesthetic, " +
        "  :url, " +
        "  :websiteType " +
        ") " +
        "       on conflict ( aesthetic, url ) " +
        "       do update " +
        "      set url = EXCLUDED.url, " +
        "          website_type = EXCLUDED.website_type " +
        "returning website";
    // @formatter:on

    @Modifying
    @Query(GET_OR_CREATE_QUERY)
    int createOrUpdate(@Param("aesthetic") int aesthetic, @Param("url") String url,
            @Param("websiteType") int websiteType);
}
