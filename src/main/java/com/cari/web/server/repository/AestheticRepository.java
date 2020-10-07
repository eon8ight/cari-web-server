package com.cari.web.server.repository;

import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.util.AestheticFindByUrlSlugRowMapper;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AestheticRepository extends PagingAndSortingRepository<Aesthetic, Integer> {
    String FIND_BY_URL_SLUG_QUERY =
        "with tt_website as ( " +
        "  select w.*, " +
        "         to_jsonb(wt.*) as website_type " +
        "    from tb_website w " +
        "    join tb_website_type wt " +
        "      on w.website_type = wt.website_type " +
        "), tt_media as ( " +
        "     select m.*, " +
        "            to_jsonb(mc.*) as media_creator " +
        "       from tb_media m " +
        "  left join tb_media_creator mc " +
        "         on m.media_creator = mc.media_creator" +
        ")" +
        "   select a.*, " +
        "          jsonb_agg(distinct w.*)    as websites, " +
        "          jsonb_agg(distinct m.*)    as media, " +
        "          jsonb_agg(distinct to_a.*) as similar_aesthetics " +
        "     from tb_aesthetic a " +
        "left join tb_aesthetic_website aw " +
        "       on a.aesthetic = aw.aesthetic " +
        "left join tt_website w " +
        "       on aw.website = w.website " +
        "left join tb_aesthetic_media am " +
        "       on a.aesthetic = am.aesthetic " +
        "left join tt_media m " +
        "       on am.media = m.media " +
        "left join tb_aesthetic_relationship ar " +
        "       on a.aesthetic = ar.from_aesthetic " +
        "left join tb_aesthetic to_a " +
        "       on ar.to_aesthetic = to_a.aesthetic " +
        "    where a.url_slug = :urlSlug " +
        " group by a.aesthetic";

    String GET_WEBSITE_URL_BY_TYPE_QUERY = 
        "select w.url " +
        "  from tb_website w " +
        "  join tb_aesthetic_website aw " +
        "    on w.website = aw.website " +
        " where aw.aesthetic = :aesthetic " +
        "   and w.website_type = :websiteType";

    @Query(value = FIND_BY_URL_SLUG_QUERY, rowMapperClass = AestheticFindByUrlSlugRowMapper.class)
    Aesthetic findByUrlSlug(@Param("urlSlug") String urlSlug);

    @Query(GET_WEBSITE_URL_BY_TYPE_QUERY)
    String getWebsiteUrlByType(@Param("aesthetic") int aesthetic,
            @Param("websiteType") int websiteType);
}
