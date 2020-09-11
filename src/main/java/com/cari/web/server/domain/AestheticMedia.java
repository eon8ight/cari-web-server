package com.cari.web.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_aesthetic_media",
        uniqueConstraints = @UniqueConstraint(columnNames = {"aesthetic", "media"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AestheticMedia {

    @Id
    @Column(name = "aesthetic_media", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer aestheticMedia;

    @JoinColumn(name = "aesthetic", nullable = false)
    @OneToOne
    private Aesthetic aesthetic;

    @JoinColumn(name = "media", nullable = false)
    @OneToOne
    private Media media;
}
