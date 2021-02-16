package com.cari.web.server.repository;

import java.util.List;
import java.util.Optional;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.util.db.AestheticWithJoinDataMapper;
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
        "         to_jsonb(wt.*) as type " +
        "    from tb_aesthetic_website w " +
        "    join tb_website_type wt " +
        "      on w.website_type = wt.website_type " +
        "), tt_media as ( " +
        "     select m.*, " +
        "            to_jsonb(f.*)  as original_file, " +
        "            to_jsonb(tf.*) as thumbnail_file, " +
        "            to_jsonb(pf.*) as preview_file, " +
        "            to_jsonb(mc.*) as creator_object " +
        "       from tb_aesthetic_media m " +
        "       join tb_file f " +
        "         on m.media_file = f.file " +
        "       join tb_file tf " +
        "         on m.media_thumbnail_file = tf.file " +
        "       join tb_file pf " +
        "         on m.media_preview_file = pf.file " +
        "  left join tb_media_creator mc " +
        "         on m.media_creator = mc.media_creator" +
        ")" +
        "   select a.*, " +
        "          ess.label || ' ' || es.year || 's' as start_year, " +
        "          ees.label || ' ' || ee.year || 's' as end_year, " +
        "          to_jsonb(f.*)                      as display_image, " +
        "          jsonb_agg(distinct w.*)            as websites, " +
        "          jsonb_agg(distinct m.*)            as media, " +
        "          jsonb_agg(distinct case " +
        "            when to_a.aesthetic is not null then " +
        "              jsonb_build_object( " +
        "                'aesthetic',            to_a.aesthetic, " +
        "                'name',                 to_a.name, " +
        "                'url_slug',             to_a.url_slug, " +
        "                'description',          ar.description, " +
        "                'startYear',            to_a_ess.label || ' ' || to_a_es.year || 's', " +
        "                'endYear',              to_a_ees.label || ' ' || to_a_ee.year || 's', " +
        "                'approximateStartYear', fn_get_approximate_start_year(to_a.aesthetic), " +
        "                'approximateEndYear',   fn_get_approximate_end_year(to_a.aesthetic) " +
        "              ) " +
        "            else " +
        "              null " +
        "          end) as similar_aesthetics " +
        "     from tb_aesthetic a " +
        "left join tb_file f " +
        "       on a.display_image_file = f.file " +
        "left join tb_era es " +
        "       on a.start_era = es.era " +
        "left join tb_era_specifier ess  " +
        "       on es.era_specifier = ess.era_specifier " +
        "left join tb_era ee " +
        "       on a.end_era = ee.era " +
        "left join tb_era_specifier ees  " +
        "       on ee.era_specifier = ees.era_specifier " +
        "left join tt_website w " +
        "       on a.aesthetic = w.aesthetic " +
        "left join tt_media m " +
        "       on a.aesthetic = m.aesthetic " +
        "left join tb_aesthetic_relationship ar " +
        "       on a.aesthetic = ar.from_aesthetic " +
        "left join tb_aesthetic to_a " +
        "       on ar.to_aesthetic = to_a.aesthetic " +
        "left join tb_era to_a_es " +
        "       on to_a.start_era = to_a_es.era " +
        "left join tb_era_specifier to_a_ess " +
        "       on to_a_es.era_specifier = to_a_ess.era_specifier " +
        "left join tb_era to_a_ee " +
        "       on to_a.end_era = to_a_ee.era " +
        "left join tb_era_specifier to_a_ees " +
        "       on to_a_ee.era_specifier = to_a_ees.era_specifier " +
        "    where a.url_slug = :urlSlug " +
        " group by a.aesthetic, " +
        "          f.file, " +
        "          ess.label, " +
        "          es.year, " +
        "          ees.label, " +
        "          ee.year";

    String FIND_FOR_EDIT_QUERY =
        "with tt_website as ( " +
        "  select aw.aesthetic_website, " +
        "         aw.aesthetic, " +
        "         aw.url, " +
        "         aw.website_type " +
        "    from tb_aesthetic_website aw " +
        "), tt_media as ( " +
        "     select am.aesthetic_media, " +
        "            am.aesthetic, " +
        "            am.label, " +
        "            am.description, " +
        "            am.year, " +
        "            am.media_file, " +
        "            am.media_thumbnail_file, " +
        "            am.media_preview_file, " +
        "            f.url as file_url, " +
        "            pf.url as preview_file_url, " +
        "            mc.media_creator " +
        "       from tb_aesthetic_media am " +
        "       join tb_file f " +
        "         on am.media_file = f.file " +
        "       join tb_file pf " +
        "         on am.media_preview_file = pf.file " +
        "  left join tb_media_creator mc " +
        "         on am.media_creator = mc.media_creator " +
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
        "   select a.*, " +
        "          to_jsonb(f.*) as display_image, " +
        "          jsonb_pretty(jsonb_agg(distinct to_jsonb(w.*) - 'aesthetic'))      as websites, " +
        "          jsonb_pretty(jsonb_agg(distinct to_jsonb(m.*) - 'aesthetic'))      as media, " +
        "          jsonb_pretty(jsonb_agg(distinct to_jsonb(r.*) - 'this_aesthetic')) as similar_aesthetics " +
        "     from tb_aesthetic a " +
        "left join tb_file f " +
        "       on a.display_image_file = f.file " +
        "left join tt_website w " +
        "       on a.aesthetic = w.aesthetic " +
        "left join tt_media m " +
        "       on a.aesthetic = m.aesthetic " +
        "left join tt_relationship r " +
        "       on a.aesthetic = r.this_aesthetic " +
        "    where a.aesthetic = :aesthetic " +
        " group by a.aesthetic, " +
        "          f.file";

    String FIND_AESTHETIC_NAMES_QUERY =
        "select aesthetic, " +
        "       name " +
        "  from tb_aesthetic " +
        " where name ilike '%' || :query || '%'";

    String FIND_BY_NAME_OR_URL_SLUG_QUERY =
        "select * " +
        "  from tb_aesthetic " +
        " where lower(name)     = lower(:name) " +
        "    or lower(url_slug) = lower(:urlSlug)";

    String FIND_BY_SYMBOL_QUERY =
        "select * " +
        " from tb_aesthetic " +
        "where lower(symbol) = lower(:symbol)";
    // @formatter:on

    @Query(value = FIND_FOR_PAGE_QUERY, rowMapperClass = AestheticWithJoinDataMapper.class)
    Optional<Aesthetic> findForPage(@Param("urlSlug") String urlSlug);

    @Query(value = FIND_FOR_EDIT_QUERY, rowMapperClass = AestheticWithJoinDataMapper.class)
    Optional<Aesthetic> findForEdit(@Param("aesthetic") int aesthetic);

    @Query(FIND_AESTHETIC_NAMES_QUERY)
    List<Aesthetic> findNames(@Param("query") String query);

    @Query(FIND_BY_NAME_OR_URL_SLUG_QUERY)
    Optional<Aesthetic> findByNameOrUrlSlug(@Param("name") String name,
            @Param("urlSlug") String urlSlug);

    @Query(FIND_BY_SYMBOL_QUERY)
    Optional<Aesthetic> findBySymbol(@Param("symbol") String symbol);

    default Aesthetic createOrUpdate(Aesthetic aesthetic) {
        if (aesthetic.getAesthetic() == null) {
            return save(aesthetic);
        }

        Optional<Aesthetic> existingAestheticOptional = findById(aesthetic.getAesthetic());

        if (existingAestheticOptional.isPresent()) {
            Aesthetic existingAesthetic = existingAestheticOptional.get();

            if (aesthetic.equals(existingAesthetic)) {
                return existingAesthetic;
            }

            aesthetic.setCreator(existingAesthetic.getCreator());
            aesthetic.setCreated(existingAesthetic.getCreated());
        }

        return save(aesthetic);
    }
}
