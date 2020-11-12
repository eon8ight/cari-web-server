package com.cari.web.server.repository;

import java.util.List;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.dto.AestheticName;
import com.cari.web.server.util.AestheticWithJoinDataMapper;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AestheticRepository extends PagingAndSortingRepository<Aesthetic, Integer> {

    // @formatter:off
    String FIND_FOR_PAGE_QUERY =
        "with tt_website as ( " +
        "  select w.*, " +
        "         to_jsonb(wt.*) as website_type " +
        "    from tb_website w " +
        "    join tb_website_type wt " +
        "      on w.website_type = wt.website_type " +
        "), tt_media as ( " +
        "     select m.*, " +
        "            to_jsonb(mi.*) as media_image, " +
        "            to_jsonb(mc.*) as media_creator " +
        "       from tb_media m " +
        "       join tb_media_image mi " +
        "         on m.media_image = mi.media_image " +
        "  left join tb_media_creator mc " +
        "         on m.media_creator = mc.media_creator" +
        ")" +
        "   select a.*, " +
        "          jsonb_agg(distinct w.*)    as websites, " +
        "          jsonb_agg(distinct m.*)    as media, " +
        "          jsonb_agg(distinct case " +
        "            when to_a.aesthetic is not null then " +
        "              jsonb_build_object( " +
        "                'name', to_a.name, " +
        "                'url_slug', to_a.url_slug, " +
        "                'description', ar.description " +
        "              ) " +
        "            else " +
        "              null " +
        "          end) as similar_aesthetics " +
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

    String FIND_FOR_EDIT_QUERY = "with tt_website as ( " +
        "  select aw.aesthetic, " +
        "         w.website, " +
        "         w.url, " +
        "         jsonb_build_object('website_type', wt.website_type) as website_type " +
        "    from tb_aesthetic_website aw " +
        "    join tb_website w " +
        "      on aw.website = w.website " +
        "    join tb_website_type wt " +
        "      on w.website_type = wt.website_type " +
        "), tt_media as ( " +
        "     select am.aesthetic, " +
        "            m.media, " +
        "            m.label, " +
        "            m.description, " +
        "            m.year, " +
        "            jsonb_build_object( " +
        "              'media_image',       mi.media_image, " +
        "              'url',               mi.url, " +
        "              'preview_image_url', mi.preview_image_url " +
        "            ) as media_image, " +
        "            jsonb_build_object( " +
        "              'media_creator', mc.media_creator, " +
        "              'name',          mc.name " +
        "            ) as media_creator " +
        "       from tb_aesthetic_media am " +
        "       join tb_media m " +
        "         on am.media = m.media " +
        "       join tb_media_image mi " +
        "         on m.media_image = mi.media_image " +
        "  left join tb_media_creator mc " +
        "         on m.media_creator = mc.media_creator " +
        "), tt_relationship as ( " +
        "  select ar.from_aesthetic as this_aesthetic, " +
        "         ar.to_aesthetic as aesthetic, " +
        "         ar.description, " +
        "         rar.description as reverse_description " +
        "    from tb_aesthetic_relationship ar " +
        "    join tb_aesthetic_relationship rar " +
        "      on ar.from_aesthetic = rar.to_aesthetic " +
        "     and ar.to_aesthetic = rar.from_aesthetic " +
        ") " +
        "   select a.aesthetic, " +
        "          a.name, " +
        "          a.url_slug, " +
        "          a.symbol, " +
        "          a.start_year, " +
        "          a.peak_year, " +
        "          a.description, " +
        "          a.media_source_url, " +
        "          jsonb_pretty(jsonb_agg(distinct to_jsonb(w.*) - 'aesthetic'))      as websites, " +
        "          jsonb_pretty(jsonb_agg(distinct to_jsonb(m.*) - 'aesthetic'))      as media, " +
        "          jsonb_pretty(jsonb_agg(distinct to_jsonb(r.*) - 'this_aesthetic')) as similar_aesthetics " +
        "     from tb_aesthetic a " +
        "left join tt_website w " +
        "       on a.aesthetic = w.aesthetic " +
        "left join tt_media m " +
        "       on a.aesthetic = m.aesthetic " +
        "left join tt_relationship r " +
        "       on a.aesthetic = r.this_aesthetic " +
        "    where a.aesthetic = :aesthetic " +
        " group by a.aesthetic ";

    String SIMPLE_FIND_QUERY =
        "select * " +
        "  from tb_aesthetic " +
        " where aesthetic = :aesthetic";

    String FIND_AESTHETIC_NAMES_QUERY =
        "select aesthetic, " +
        "       name " +
        "  from tb_aesthetic " +
        " where name ilike '%' || :query || '%'";
    // @formatter:on

    @Query(value = FIND_FOR_PAGE_QUERY, rowMapperClass = AestheticWithJoinDataMapper.class)
    Aesthetic findForPage(@Param("urlSlug") String urlSlug);

    @Query(value = FIND_FOR_EDIT_QUERY, rowMapperClass = AestheticWithJoinDataMapper.class)
    Aesthetic findForEdit(@Param("aesthetic") int aesthetic);

    @Query(value = SIMPLE_FIND_QUERY)
    Aesthetic simpleFind(@Param("aesthetic") int aesthetic);

    @Query(value = FIND_AESTHETIC_NAMES_QUERY)
    List<AestheticName> findNames(@Param("query") String query);
}
