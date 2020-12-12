package com.cari.web.server.repository;

import java.util.List;
import com.cari.web.server.domain.db.AestheticWebsite;
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
        "    aesthetic, " +
        "    url, " +
        "    website_type " +
        ") values ( " +
        "    :aesthetic, " +
        "    :url, " +
        "    :websiteType " +
        ") " +
        "       on conflict ( aesthetic, url ) " +
        "       do update " +
        "      set url = EXCLUDED.url, " +
        "          website_type = EXCLUDED.website_type " +
        "returning aesthetic_website";

    String DELETE_BY_AESTHETIC_EXCEPT_QUERY =
        "delete from tb_aesthetic_website " +
        "      where aesthetic = :aesthetic " +
        "        and aesthetic_website not in ( :excludedAestheticWebsites )";

    String DELETE_BY_AESTHETIC_QUERY =
        "delete from tb_aesthetic_website " +
        "      where aesthetic = :aesthetic";
    // @formatter:on

    @Query(CREATE_OR_UPDATE_QUERY)
    int createOrUpdate(@Param("aesthetic") int aesthetic, @Param("url") String url,
            @Param("websiteType") int websiteType);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_EXCEPT_QUERY)
    void deleteByAestheticExcept(@Param("aesthetic") int aesthetic,
            @Param("excludedAestheticWebsites") List<Integer> excludedAestheticWebsites);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_QUERY)
    void deleteByAesthetic(@Param("aesthetic") int aesthetic);
}
