package com.cari.web.server.domain;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.cari.web.server.dto.SimilarAesthetic;
import com.cari.web.server.dto.arena.ArenaApiResponse;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("tb_aesthetic")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Aesthetic implements Serializable {
    private static final long serialVersionUID = -3086472542529813307L;

    private static final String COLUMN_URL_SLUG = "url_slug";
    private static final String COLUMN_START_YEAR = "start_year";
    private static final String COLUMN_END_YEAR = "end_year";
    private static final String COLUMN_MEDIA_SOURCE_URL = "media_source_url";

    @Id
    @Column
    private Integer aesthetic;

    @Column
    @NotNull
    private String name;

    @Column(COLUMN_URL_SLUG)
    @JsonAlias({COLUMN_URL_SLUG})
    private String urlSlug;

    @Pattern(regexp = "^[A-Z][a-z]{1,2}")
    private String symbol;

    @Column(COLUMN_START_YEAR)
    @JsonAlias({COLUMN_START_YEAR})
    @NotBlank
    private String startYear;

    @Column(COLUMN_END_YEAR)
    @JsonAlias({COLUMN_END_YEAR})
    private String endYear;

    @Column
    @NotNull
    private String description;

    @Column(COLUMN_MEDIA_SOURCE_URL)
    @JsonAlias({COLUMN_MEDIA_SOURCE_URL})
    private String mediaSourceUrl;

    @Transient
    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private List<SimilarAesthetic> similarAesthetics = Collections.emptyList();

    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private List<AestheticMedia> media = Collections.emptyList();

    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private List<AestheticWebsite> websites = Collections.emptyList();

    @Transient
    private ArenaApiResponse galleryContent;

    public static Aesthetic fromResultSet(ResultSet rs, int rowNum) throws SQLException {
        // @formatter:off
        return Aesthetic.builder()
            .aesthetic(rs.getInt("aesthetic"))
            .name(rs.getString("name"))
            .urlSlug(rs.getString("url_slug"))
            .symbol(rs.getString("symbol"))
            .startYear(rs.getString("start_year"))
            .endYear(rs.getString("end_year"))
            .description(rs.getString("description"))
            .mediaSourceUrl(rs.getString("media_source_url"))
            .build();
        // @formatter:on
    }
}
