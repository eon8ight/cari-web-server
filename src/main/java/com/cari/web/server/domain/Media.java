package com.cari.web.server.domain;

import java.io.Serializable;
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
@Table("tb_media")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Media implements Serializable {
    private static final long serialVersionUID = -4116792078351646162L;

    private static final String COLUMN_PREVIEW_IMAGE_URL = "preview_image_url";
    private static final String COLUMN_MEDIA_CREATOR = "media_creator";

    @Id
    private int media;

    private String url;

    @Column(COLUMN_PREVIEW_IMAGE_URL)
    @JsonAlias({COLUMN_PREVIEW_IMAGE_URL})
    private String previewImageUrl;

    private String label;

    private String description;

    @Column(COLUMN_MEDIA_CREATOR)
    @JsonAlias({COLUMN_MEDIA_CREATOR})
    @MappedCollection
    private MediaCreator mediaCreator;

    private Integer year;
}
