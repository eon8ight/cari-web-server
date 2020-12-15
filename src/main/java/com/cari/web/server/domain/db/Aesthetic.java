package com.cari.web.server.domain.db;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.cari.web.server.domain.SimilarAesthetic;
import com.cari.web.server.dto.response.arena.ArenaApiResponse;
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
    private String name;

    @Column(COLUMN_URL_SLUG)
    @JsonAlias(COLUMN_URL_SLUG)
    private String urlSlug;

    @Column
    private String symbol;

    @Column(COLUMN_START_YEAR)
    @JsonAlias(COLUMN_START_YEAR)
    private String startYear;

    @Column(COLUMN_END_YEAR)
    @JsonAlias(COLUMN_END_YEAR)
    private String endYear;

    @Column
    private String description;

    @Column(COLUMN_MEDIA_SOURCE_URL)
    @JsonAlias(COLUMN_MEDIA_SOURCE_URL)
    private String mediaSourceUrl;

    @Transient
    private List<SimilarAesthetic> similarAesthetics;

    @Transient
    private List<AestheticMedia> media;

    @Transient
    private List<AestheticWebsite> websites;

    @Transient
    private ArenaApiResponse galleryContent;

    public static Aesthetic fromResultSet(ResultSet rs, int rowNum) throws SQLException {
        // @formatter:off
        return Aesthetic.builder()
            .aesthetic(rs.getInt("aesthetic"))
            .name(rs.getString("name"))
            .urlSlug(rs.getString(COLUMN_URL_SLUG))
            .symbol(rs.getString("symbol"))
            .startYear(rs.getString(COLUMN_START_YEAR))
            .endYear(rs.getString(COLUMN_END_YEAR))
            .description(rs.getString("description"))
            .mediaSourceUrl(rs.getString(COLUMN_MEDIA_SOURCE_URL))
            .build();
        // @formatter:on
    }
}
