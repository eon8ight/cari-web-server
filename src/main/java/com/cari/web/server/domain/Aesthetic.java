package com.cari.web.server.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_aesthetic")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aesthetic {

        @Id
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

        @JoinTable(name = "tb_aesthetic_relationship",
                        joinColumns = @JoinColumn(name = "from_aesthetic"),
                        inverseJoinColumns = @JoinColumn(name = "to_aesthetic"))
        @OneToMany(fetch = FetchType.LAZY)
        @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
        private List<Aesthetic> similarAesthetics;

        @JoinTable(name = "tb_aesthetic_media", joinColumns = @JoinColumn(name = "aesthetic"),
                        inverseJoinColumns = @JoinColumn(name = "media"))
        @OneToMany(fetch = FetchType.LAZY)
        @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
        private List<Media> media;
}
