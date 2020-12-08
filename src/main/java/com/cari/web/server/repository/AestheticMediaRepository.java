package com.cari.web.server.repository;

import java.util.List;
import com.cari.web.server.domain.db.AestheticMedia;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AestheticMediaRepository extends CrudRepository<AestheticMedia, Integer> {

    // @formatter:off
    String CREATE_OR_UPDATE_QUERY =
        "insert into tb_aesthetic_media ( " +
        "    aesthetic, " +
        "    url, " +
        "    preview_image_url, " +
        "    label, " +
        "    description, " +
        "    media_creator, " +
        "    year " +
        ") values ( " +
        "    :aesthetic, " +
        "    :url, " +
        "    :previewImageUrl, " +
        "    :label, " +
        "    :description, " +
        "    :mediaCreator, " +
        "    :year " +
        ") " +
        "       on conflict ( aesthetic, url ) " +
        "       do update " +
        "      set preview_image_url = EXCLUDED.preview_image_url, " +
        "          label             = EXCLUDED.label, " +
        "          description       = EXCLUDED.description, " +
        "          media_creator     = EXCLUDED.media_creator, " +
        "          year              = EXCLUDED.year " +
        "returning aesthetic_media";

    String DELETE_BY_AESTHETIC_EXCEPT_QUERY =
        "delete from tb_aesthetic_media " +
        "      where aesthetic = :aesthetic " +
        "        and aesthetic_media not in ( :excludedAestheticMedia )";

    String DELETE_BY_AESTHETIC_QUERY =
        "delete from tb_aesthetic_media " +
        "      where aesthetic = :aesthetic";
    // @formatter:on

    @Query(CREATE_OR_UPDATE_QUERY)
    int createOrUpdate(@Param("aesthetic") int aesthetic, @Param("url") String url,
            @Param("previewImageUrl") String previewImageUrl, @Param("label") String label,
            @Param("description") String description, @Param("mediaCreator") Integer mediaCreator,
            @Param("year") int year);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_EXCEPT_QUERY)
    void deleteByAestheticExcept(@Param("aesthetic") int aesthetic,
            @Param("excludedAestheticMedia") List<Integer> excludedAestheticMedia);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_QUERY)
    void deleteByAesthetic(@Param("aesthetic") int aesthetic);
}
