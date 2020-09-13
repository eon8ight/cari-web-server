package com.cari.web.server.repository;

import java.util.List;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.domain.Media;
import com.cari.web.server.domain.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AestheticRepository extends JpaRepository<Aesthetic, Integer> {

    @Query("select a from Aesthetic a where urlSlug = ?1")
    Aesthetic findByUrlSlug(String urlSlug);

    @Query("select ar.toAesthetic, ar.description from AestheticRelationship ar where ar.fromAesthetic.aesthetic = ?1")
    List<Object[]> findSimilarAesthetics(int aesthetic);

    @Query("select am.media from AestheticMedia am where am.aesthetic.aesthetic = ?1")
    List<Media> findMedia(int aesthetic);

    @Query("select aw.website from AestheticWebsite aw where aw.aesthetic.aesthetic = ?1")
    List<Website> findWebsites(int aesthetic);

    @Query("select aw.website.url from AestheticWebsite aw where aw.aesthetic.aesthetic = ?1 and aw.website.websiteType.websiteType = ?2")
    String getWebsiteUrlByType(int aesthetic, int websiteType);
}