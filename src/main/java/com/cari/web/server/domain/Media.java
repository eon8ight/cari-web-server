package com.cari.web.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_media")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Media {

    @Id
    @Column(name = "media", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer media;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "preview_image_url")
    private String previewImageUrl;

    @Column(name = "label")
    private String label;

    @Column(name = "description")
    private String description;

    @JoinColumn(name = "media_creator")
    @OneToOne
    private MediaCreator mediaCreator;

    @Column(name = "year")
    private Integer year;
}
