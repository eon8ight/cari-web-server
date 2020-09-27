package com.cari.web.server.repository;

import com.cari.web.server.domain.Aesthetic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AestheticRepository extends JpaRepository<Aesthetic, Integer> {

    Aesthetic findByUrlSlug(String urlSlug);

    @Query("select aw.website.url from AestheticWebsite aw where aw.aesthetic.aesthetic = :aesthetic and aw.website.websiteType.websiteType = :websiteType")
    String getWebsiteUrlByType(@Param("aesthetic") int aesthetic,
            @Param("websiteType") int websiteType);
}
