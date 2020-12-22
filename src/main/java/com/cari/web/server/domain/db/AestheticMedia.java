package com.cari.web.server.domain.db;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("tb_aesthetic_media")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class AestheticMedia implements Serializable {
    private static final long serialVersionUID = -4258598762766168605L;

    private static final String COLUMN_AESTHETIC_MEDIA = "aesthetic_media";
    private static final String COLUMN_MEDIA_FILE = "media_file";
    private static final String COLUMN_MEDIA_THUMBNAIL_FILE = "media_thumbnail_file";
    private static final String COLUMN_MEDIA_PREVIEW_FILE = "media_preview_file";
    private static final String COLUMN_MEDIA_CREATOR = "media_creator";

    @Id
    @Column(COLUMN_AESTHETIC_MEDIA)
    @JsonAlias(COLUMN_AESTHETIC_MEDIA)
    private Integer aestheticMedia;

    @Column
    private Integer aesthetic;

    @Column(COLUMN_MEDIA_FILE)
    @JsonAlias(COLUMN_MEDIA_FILE)
    private Integer mediaFile;

    @Column(COLUMN_MEDIA_THUMBNAIL_FILE)
    @JsonAlias(COLUMN_MEDIA_THUMBNAIL_FILE)
    private Integer mediaThumbnailFile;

    @Column(COLUMN_MEDIA_PREVIEW_FILE)
    @JsonAlias(COLUMN_MEDIA_PREVIEW_FILE)
    private Integer mediaPreviewFile;

    @Column
    private String label;

    @Column
    private String description;

    @Column(COLUMN_MEDIA_CREATOR)
    @JsonAlias(COLUMN_MEDIA_CREATOR)
    private Integer mediaCreator;

    @Column
    private Integer year;

    @Transient
    private MediaCreator creator;

    @Transient
    @JsonAlias("original_file")
    private CariFile originalFile;

    @Transient
    @JsonAlias("thumbnail_file")
    private CariFile thumbnailFile;

    @Transient
    @JsonAlias("preview_file")
    private CariFile previewFile;

    @Transient
    @JsonAlias("file_url")
    private String fileUrl;

    @Transient
    @JsonAlias("preview_file_url")
    private String previewFileUrl;
}
