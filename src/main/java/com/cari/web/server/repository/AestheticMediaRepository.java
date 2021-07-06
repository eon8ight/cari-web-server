package com.cari.web.server.repository;

import java.util.List;
import com.cari.web.server.domain.db.AestheticMedia;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.dto.DatabaseUpsertResult;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AestheticMediaRepository
        extends EditableAestheticAttachmentRepository<AestheticMedia> {

    String FIND_BY_AESTHETIC_QUERY = """
            select *
              from tb_aesthetic_media
             where aesthetic = :aesthetic""";

    String FIND_BY_AESTHETIC_EXCEPT_QUERY = """
            select *
              from tb_aesthetic_media
             where aesthetic = :aesthetic
               and aesthetic_media not in ( :excludedAestheticMedia )""";

    String FIND_FILES_BY_AESTHETIC_QUERY = """
            select f.*
              from tb_file f
              join tb_aesthetic_media am
                on am.media_file           = f.file
                or am.media_thumbnail_file = f.file
                or am.media_preview_file   = f.file
             where am.aesthetic = :aesthetic""";

    String DELETE_BY_AESTHETIC_EXCEPT_QUERY = """
            delete from tb_aesthetic_media
                  where aesthetic = :aesthetic
                    and aesthetic_media not in ( :excludedAestheticMedia )""";

    String DELETE_BY_AESTHETIC_QUERY = """
            delete from tb_aesthetic_media
                  where aesthetic = :aesthetic""";

    String FIND_UNUSED_AESTHETIC_MEDIA_FILES_QUERY = """
            select f.*
              from tb_file f
              join tb_aesthetic_media am
                on am.media_file           = f.file
                or am.media_thumbnail_file = f.file
                or am.media_preview_file   = f.file
             where am.aesthetic = :aesthetic
               and am.aesthetic_media not in ( :excludedAestheticMedia )""";

    @Query(FIND_BY_AESTHETIC_QUERY)
    List<AestheticMedia> findByAesthetic(@Param("aesthetic") int aesthetic);

    @Query(FIND_BY_AESTHETIC_EXCEPT_QUERY)
    List<AestheticMedia> findByAestheticExcept(@Param("aesthetic") int aesthetic,
            @Param("excludedAestheticMedia") List<Integer> excludedAestheticMedia);

    @Query(FIND_FILES_BY_AESTHETIC_QUERY)
    List<CariFile> findFilesByAesthetic(@Param("aesthetic") int aesthetic);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_EXCEPT_QUERY)
    int deleteByAestheticExcept(@Param("aesthetic") int aesthetic,
            @Param("excludedAestheticMedia") List<Integer> excludedAestheticMedia);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_QUERY)
    int deleteByAesthetic(@Param("aesthetic") int aesthetic);

    @Query(FIND_UNUSED_AESTHETIC_MEDIA_FILES_QUERY)
    List<CariFile> findUnusedAestheticMediaFiles(@Param("aesthetic") int aesthetic,
            @Param("excludedAestheticMedia") List<Integer> excludedAestheticMedia);

    default DatabaseUpsertResult<AestheticMedia> createOrUpdateForAesthetic(int pkAesthetic,
            List<AestheticMedia> aestheticMedia) {
        return createOrUpdateForAesthetic(pkAesthetic, aestheticMedia, this::findByAesthetic,
                (pk, m) -> m.setAesthetic(pk),
                (from, to) -> to.setAestheticMedia(from.getAestheticMedia()));
    }
}
