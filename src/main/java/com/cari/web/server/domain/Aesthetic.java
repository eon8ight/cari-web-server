package com.cari.web.server.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.cari.web.server.dto.ArenaApiResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_aesthetic")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Aesthetic {

        @Id
        @Column(name = "aesthetic", nullable = false, unique = true)
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer aesthetic;

        @Column(name = "name", nullable = false, unique = true)
        private String name;

        @Column(name = "url_slug", nullable = false, unique = true)
        private String urlSlug;

        @Column(name = "symbol", nullable = false, length = 3, unique = true)
        private String symbol;

        @Column(name = "start_year", nullable = false)
        private Integer startYear;

        @Column(name = "end_year")
        private Integer endYear;

        @Column(name = "description", nullable = false)
        private String description;

        @Transient
        private List<Aesthetic> similarAesthetics;

        @Transient
        private List<Media> media;

        @Transient
        private List<Website> websites;

        @Transient
        private ArenaApiResponse galleryContent;
}
