package com.cari.web.server.domain.db;

import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_aesthetic_media")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class AestheticMedia implements Serializable {
    private static final long serialVersionUID = -4258598762766168605L;

    private static final String COLUMN_AESTHETIC_MEDIA = "aesthetic_media";
    private static final String COLUMN_PREVIEW_IMAGE_URL = "preview_image_url";
    private static final String COLUMN_MEDIA_CREATOR = "media_creator";

    @Id
    @Column(COLUMN_AESTHETIC_MEDIA)
    @JsonAlias({COLUMN_AESTHETIC_MEDIA})
    private int aestheticMedia;

    @Column
    private int aesthetic;

    @Column
    @NotNull
    private String url;

    @Column(COLUMN_PREVIEW_IMAGE_URL)
    @JsonAlias({COLUMN_PREVIEW_IMAGE_URL})
    @NotNull
    private String previewImageUrl;

    @Column
    private String label;

    @Column
    private String description;

    @Column(COLUMN_MEDIA_CREATOR)
    @JsonAlias({COLUMN_MEDIA_CREATOR})
    @MappedCollection
    @Valid
    private MediaCreator mediaCreator;

    @Column
    private int year;
}
