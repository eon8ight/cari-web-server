package com.cari.web.server.repository;

import java.util.List;
import com.cari.web.server.domain.db.AestheticWebsite;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AestheticWebsiteRepository
        extends EditableAestheticAttachmentRepository<AestheticWebsite> {

    // @formatter:off
    String DELETE_BY_AESTHETIC_EXCEPT_QUERY =
        "delete from tb_aesthetic_website " +
        "      where aesthetic = :aesthetic " +
        "        and aesthetic_website not in ( :excludedAestheticWebsites )";

    String DELETE_BY_AESTHETIC_QUERY =
        "delete from tb_aesthetic_website " +
        "      where aesthetic = :aesthetic";

    String FIND_BY_AESTHETIC_QUERY =
        "select * " +
        "  from tb_aesthetic_website " +
        " where aesthetic = :aesthetic";
    // @formatter:on

    @Modifying
    @Query(DELETE_BY_AESTHETIC_EXCEPT_QUERY)
    void deleteByAestheticExcept(@Param("aesthetic") int aesthetic,
            @Param("excludedAestheticWebsites") List<Integer> excludedAestheticWebsites);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_QUERY)
    void deleteByAesthetic(@Param("aesthetic") int aesthetic);

    @Query(FIND_BY_AESTHETIC_QUERY)
    List<AestheticWebsite> findByAesthetic(@Param("aesthetic") int aesthetic);

    default List<AestheticWebsite> createOrUpdateForAesthetic(int pkAesthetic,
            List<AestheticWebsite> aestheticWebsites) {
        return createOrUpdateForAesthetic(pkAesthetic, aestheticWebsites, this::findByAesthetic,
                (pk, w) -> w.setAesthetic(pk),
                (from, to) -> to.setAestheticWebsite(from.getAestheticWebsite()));
    }
}
