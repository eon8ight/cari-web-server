package com.cari.web.server.domain;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_media_image")
public class MediaImage implements Serializable {

    private static final long serialVersionUID = 1287982115694050011L;

    private static final String COLUMN_MEDIA_IMAGE = "media_image";
    private static final String COLUMN_PREVIEW_IMAGE_URL = "preview_image_url";

    @Id
    @Column(COLUMN_MEDIA_IMAGE)
    @JsonAlias({COLUMN_MEDIA_IMAGE})
    private int mediaImage;

    @Column("url")
    private String url;

    @Column(COLUMN_PREVIEW_IMAGE_URL)
    @JsonAlias({COLUMN_PREVIEW_IMAGE_URL})
    private String previewImageUrl;

}
