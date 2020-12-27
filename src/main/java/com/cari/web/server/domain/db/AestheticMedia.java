package com.cari.web.server.domain.db;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("tb_aesthetic_media")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AestheticMedia extends ModifiableTable implements EditableAestheticAttachment {

    private static final long serialVersionUID = -4258598762766168605L;

    private static final String COLUMN_AESTHETIC_MEDIA = "aesthetic_media";
    private static final String COLUMN_MEDIA_FILE = "media_file";
    private static final String COLUMN_MEDIA_THUMBNAIL_FILE = "media_thumbnail_file";
    private static final String COLUMN_MEDIA_PREVIEW_FILE = "media_preview_file";
    private static final String COLUMN_MEDIA_CREATOR = "media_creator";

    @Id
    @Column(COLUMN_AESTHETIC_MEDIA)
    @JsonAlias(COLUMN_AESTHETIC_MEDIA)
    @EqualsAndHashCode.Exclude
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
    @JsonAlias("creator_object")
    @EqualsAndHashCode.Exclude
    private MediaCreator creatorObject;

    @Transient
    @JsonAlias("original_file")
    @EqualsAndHashCode.Exclude
    private CariFile originalFile;

    @Transient
    @JsonAlias("thumbnail_file")
    @EqualsAndHashCode.Exclude
    private CariFile thumbnailFile;

    @Transient
    @JsonAlias("preview_file")
    @EqualsAndHashCode.Exclude
    private CariFile previewFile;

    @Transient
    @JsonAlias("file_url")
    @EqualsAndHashCode.Exclude
    private String fileUrl;

    @Transient
    @JsonAlias("preview_file_url")
    @EqualsAndHashCode.Exclude
    private String previewFileUrl;

    @Override
    public int alternateKeyHash() {
        return Objects.hash(aesthetic, mediaFile);
    }
}
