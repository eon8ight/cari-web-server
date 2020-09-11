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
@Table(name = "tb_aesthetic_website",
        uniqueConstraints = @UniqueConstraint(columnNames = {"aesthetic", "website"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AestheticWebsite {
    
    @Id
    @Column(name = "aesthetic_website")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer aestheticMedia;

    @JoinColumn(name = "aesthetic", nullable = false)
    @OneToOne
    private Aesthetic aesthetic;

    @JoinColumn(name = "website", nullable = false)
    @OneToOne
    private Website website;
}
