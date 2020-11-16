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
    String CREATE_OR_UPDATE_QUERY =
        "insert into tb_aesthetic_website ( " +
        "  aesthetic, " +
        "  website " +
        ") values ( " +
        "  :aesthetic, " +
        "  :website, " +
        ") " +
        "       on conflict ( aesthetic, website ) " +
        "       do update " +
        "      set website = EXCLUDED.website " +
        "returning aesthetic_website";
    // @formatter:on

    @Modifying
    @Query(CREATE_OR_UPDATE_QUERY)
    int createOrUpdate(@Param("aesthetic") int aesthetic, @Param("website") int website);
}
