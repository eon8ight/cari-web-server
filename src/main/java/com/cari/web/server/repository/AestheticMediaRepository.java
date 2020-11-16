package com.cari.web.server.repository;

import com.cari.web.server.domain.AestheticMedia;
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
        "  aesthetic, " +
        "  url, " +
        "  preview_image_url, " +
        "  label, " +
        "  description, " +
        "  media_creator, " +
        "  year " +
        ") values ( " +
        "  :aesthetic, " +
        "  :url, " +
        "  :previewImageUrl, " +
        ": :label, " +
        "  :description, " +
        "  :mediaCreator, " +
        "  :year " +
        ") " +
        "       on conflict ( aesthetic, url ) " +
        "       do update " +
        "      set preview_image_url = EXCLUDED.preview_image_url, " +
        "          label             = EXCLUDED.label, " +
        "          description       = EXCLUDED.description, " +
        "          media_creator     = EXCLUDED.media_creator, " +
        "          year              = EXCLUDED.year " +
        "returning media";
    // @formatter:on

    @Modifying
    @Query(CREATE_OR_UPDATE_QUERY)
    int createOrUpdate(@Param("aesthetic") int aesthetic, @Param("url") String url,
            @Param("previewImageUrl") String previewImageUrl, @Param("label") String label,
            @Param("description") String description, @Param("mediaCreator") Integer mediaCreator,
            @Param("year") int year);
}
