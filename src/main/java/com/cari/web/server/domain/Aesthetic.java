package com.cari.web.server.domain;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.cari.web.server.dto.SimilarAesthetic;
import com.cari.web.server.dto.arena.ArenaApiResponse;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table("tb_aesthetic")
public class Aesthetic implements Serializable {
    private static final long serialVersionUID = -3086472542529813307L;

    private static final String COLUMN_URL_SLUG = "url_slug";
    private static final String COLUMN_START_YEAR = "start_year";
    private static final String COLUMN_PEAK_YEAR = "peak_year";
    private static final String COLUMN_MEDIA_SOURCE_URL = "media_source_url";

    @Id
    private int aesthetic;

    private String name;

    @Column(COLUMN_URL_SLUG)
    @JsonAlias({COLUMN_URL_SLUG})
    private String urlSlug;

    private String symbol;

    @Column(COLUMN_START_YEAR)
    @JsonAlias({COLUMN_START_YEAR})
    private Integer startYear;

    @Column(COLUMN_PEAK_YEAR)
    @JsonAlias({COLUMN_PEAK_YEAR})
    private Integer peakYear;

    private String description;

    @Column(COLUMN_MEDIA_SOURCE_URL)
    @JsonAlias({COLUMN_MEDIA_SOURCE_URL})
    private String mediaSourceUrl;

    @Embedded.Empty
    private List<SimilarAesthetic> similarAesthetics;

    @Embedded.Empty
    private List<Media> media;

    @Embedded.Empty
    private List<Website> websites;

    @Transient
    private ArenaApiResponse galleryContent;

    public static Aesthetic fromResultSet(ResultSet rs, int rowNum) throws SQLException {
        Integer peakYear = null;
        String peakYearString = rs.getString("peak_year");

        if (peakYearString != null) {
            peakYear = Integer.parseInt(peakYearString);
        }

        Aesthetic aesthetic = new Aesthetic();
        aesthetic.setAesthetic(rs.getInt("aesthetic"));
        aesthetic.setName(rs.getString("name"));
        aesthetic.setUrlSlug(rs.getString("url_slug"));
        aesthetic.setSymbol(rs.getString("symbol"));
        aesthetic.setStartYear(rs.getInt("start_year"));
        aesthetic.setPeakYear(peakYear);
        aesthetic.setDescription(rs.getString("description"));
        aesthetic.setMediaSourceUrl(rs.getString("media_source_url"));

        return aesthetic;
    }
}
