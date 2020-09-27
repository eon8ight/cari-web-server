package com.cari.web.server.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_media")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Media implements Serializable {

    private static final long serialVersionUID = -4116792078351646162L;

    private static final String COLUMN_PREVIEW_IMAGE_URL = "preview_image_url";
    private static final String COLUMN_MEDIA_CREATOR = "media_creator";

    @Id
    @Column(name = "media", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer media;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = COLUMN_PREVIEW_IMAGE_URL)
    @JsonAlias({ COLUMN_PREVIEW_IMAGE_URL })
    private String previewImageUrl;

    @Column(name = "label")
    private String label;

    @Column(name = "description")
    private String description;

    @JoinColumn(name = COLUMN_MEDIA_CREATOR)
    @JsonAlias({ COLUMN_MEDIA_CREATOR })
    @OneToOne
    private MediaCreator mediaCreator;

    @Column(name = "year")
    private Integer year;
}
