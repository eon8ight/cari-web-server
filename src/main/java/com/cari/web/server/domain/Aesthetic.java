package com.cari.web.server.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import com.cari.web.server.dto.ArenaApiResponse;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_aesthetic")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "aesthetic")
public class Aesthetic implements Serializable {

        private static final long serialVersionUID = -3086472542529813307L;

        private static final String COLUMN_URL_SLUG = "url_slug";
        private static final String COLUMN_START_YEAR = "start_year";
        private static final String COLUMN_END_YEAR = "end_year";

        @Id
        @Column(name = "aesthetic", nullable = false, unique = true)
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer aesthetic;

        @Column(name = "name", nullable = false, unique = true)
        private String name;

        @Column(name = COLUMN_URL_SLUG, nullable = false, unique = true)
        @JsonAlias({COLUMN_URL_SLUG})
        private String urlSlug;

        @Column(name = "symbol", nullable = false, length = 3, unique = true)
        private String symbol;

        @Column(name = COLUMN_START_YEAR, nullable = false)
        @JsonAlias({COLUMN_START_YEAR})
        private Integer startYear;

        @Column(name = COLUMN_END_YEAR)
        @JsonAlias({COLUMN_END_YEAR})
        private Integer endYear;

        @Column(name = "description", nullable = false)
        private String description;

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "tb_aesthetic_relationship",
                        joinColumns = @JoinColumn(name = "to_aesthetic"),
                        inverseJoinColumns = @JoinColumn(name = "from_aesthetic"))
        @JsonManagedReference
        @JsonBackReference
        private List<Aesthetic> similarAesthetics;

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "tb_aesthetic_media", joinColumns = @JoinColumn(name = "aesthetic"),
                        inverseJoinColumns = @JoinColumn(name = "media"))
        private List<Media> media;

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "tb_aesthetic_website", joinColumns = @JoinColumn(name = "aesthetic"),
                        inverseJoinColumns = @JoinColumn(name = "website"))
        private List<Website> websites;

        private transient ArenaApiResponse galleryContent;
}
