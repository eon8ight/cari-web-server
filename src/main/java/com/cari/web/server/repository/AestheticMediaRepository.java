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
        "    media_file, " +
        "    media_thumbnail_file, " +
        "    media_preview_file, " +
        "    label, " +
        "    description, " +
        "    media_creator, " +
        "    year " +
        ") values ( " +
        "    :aesthetic, " +
        "    :mediaFile, " +
        "    :mediaThumbnailFile, " +
        "    :mediaPreviewFile, " +
        "    :label, " +
        "    :description, " +
        "    :mediaCreator, " +
        "    :year " +
        ") " +
        "       on conflict ( aesthetic, media_file ) " +
        "       do update " +
        "      set media_thumbnail_file = EXCLUDED.media_thumbnail_file, " +
        "          media_preview_file   = EXCLUDED.media_preview_file, " +
        "          label                = EXCLUDED.label, " +
        "          description          = EXCLUDED.description, " +
        "          media_creator        = EXCLUDED.media_creator, " +
        "          year                 = EXCLUDED.year " +
        "returning aesthetic_media";

    String UPDATE_EXCEPT_FILES_QUERY =
        "   update tb_aesthetic_media " +
        "      set label         = :label, " +
        "          description   = :description, " +
        "          media_creator = :mediaCreator, " +
        "          year          = :year " +
        "    where aesthetic  = :aesthetic " +
        "      and media_file = :mediaFile " +
        "returning aesthetic_media ";

    String DELETE_BY_AESTHETIC_EXCEPT_QUERY =
        "delete from tb_aesthetic_media " +
        "      where aesthetic = :aesthetic " +
        "        and aesthetic_media not in ( :excludedAestheticMedia )";

    String DELETE_BY_AESTHETIC_QUERY =
        "delete from tb_aesthetic_media " +
        "      where aesthetic = :aesthetic";
    // @formatter:on

    @Query(CREATE_OR_UPDATE_QUERY)
    int createOrUpdate(@Param("aesthetic") int aesthetic, @Param("mediaFile") int mediaFile,
            @Param("mediaThumbnailFile") int mediaThumbnailFile,
            @Param("mediaPreviewFile") int mediaPreviewFile, @Param("label") String label,
            @Param("description") String description, @Param("mediaCreator") Integer mediaCreator,
            @Param("year") int year);

    @Query(UPDATE_EXCEPT_FILES_QUERY)
    int updateExceptFiles(@Param("aesthetic") int aesthetic, @Param("label") String label,
            @Param("description") String description, @Param("mediaCreator") Integer mediaCreator,
            @Param("year") int year);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_EXCEPT_QUERY)
    void deleteByAestheticExcept(@Param("aesthetic") int aesthetic,
            @Param("excludedAestheticMedia") List<Integer> excludedAestheticMedia);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_QUERY)
    void deleteByAesthetic(@Param("aesthetic") int aesthetic);

    default int createOrUpdate(AestheticMedia media) {
        return createOrUpdate(media.getAesthetic(), media.getMediaFile(),
                media.getMediaThumbnailFile(), media.getMediaPreviewFile(), media.getLabel(),
                media.getDescription(), media.getMediaCreator(), media.getYear());
    }

    default int updateExceptFiles(AestheticMedia media) {
        return updateExceptFiles(media.getAesthetic(), media.getLabel(), media.getDescription(),
                media.getMediaCreator(), media.getYear());
    }
}
