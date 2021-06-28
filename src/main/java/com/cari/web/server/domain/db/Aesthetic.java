package com.cari.web.server.domain.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.cari.web.server.domain.SimilarAesthetic;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("tb_aesthetic")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Aesthetic extends ModifiableTable {

    private static final long serialVersionUID = -3086472542529813307L;

    private static final String COLUMN_URL_SLUG = "url_slug";
    private static final String COLUMN_START_ERA = "start_era";
    private static final String COLUMN_END_ERA = "end_era";
    private static final String COLUMN_MEDIA_SOURCE_URL = "media_source_url";
    private static final String COLUMN_START_YEAR = "start_year";
    private static final String COLUMN_END_YEAR = "end_year";
    private static final String COLUMN_DISPLAY_IMAGE_FILE = "display_image_file";
    private static final String COLUMN_IS_DRAFT = "is_draft";

    private static final String FIELD_DISPLAY_IMAGE_URL = "display_image_url";

    @Id
    @Column
    @EqualsAndHashCode.Exclude
    private Integer aesthetic;

    @Column
    private String name;

    @Column(COLUMN_URL_SLUG)
    @JsonAlias(COLUMN_URL_SLUG)
    private String urlSlug;

    @Column
    private String symbol;

    @Column(COLUMN_START_ERA)
    @JsonAlias(COLUMN_START_ERA)
    private Integer startEra;

    @Column(COLUMN_END_ERA)
    @JsonAlias(COLUMN_END_ERA)
    private Integer endEra;

    @Column
    private String description;

    @Column(COLUMN_MEDIA_SOURCE_URL)
    @JsonAlias(COLUMN_MEDIA_SOURCE_URL)
    private String mediaSourceUrl;

    @Column(COLUMN_DISPLAY_IMAGE_FILE)
    @JsonAlias(COLUMN_DISPLAY_IMAGE_FILE)
    private Integer displayImageFile;

    @Column(COLUMN_IS_DRAFT)
    @JsonAlias(COLUMN_IS_DRAFT)
    private Boolean isDraft;

    @Transient
    @EqualsAndHashCode.Exclude
    private List<SimilarAesthetic> similarAesthetics;

    @Transient
    @EqualsAndHashCode.Exclude
    private List<AestheticMedia> media;

    @Transient
    @EqualsAndHashCode.Exclude
    private List<AestheticWebsite> websites;

    @Transient
    @EqualsAndHashCode.Exclude
    @JsonAlias(COLUMN_START_YEAR)
    private String startYear;

    @Transient
    @EqualsAndHashCode.Exclude
    @JsonAlias(COLUMN_END_YEAR)
    private String endYear;

    @Transient
    @EqualsAndHashCode.Exclude
    @JsonAlias(FIELD_DISPLAY_IMAGE_URL)
    private String displayImageUrl;

    @Transient
    @EqualsAndHashCode.Exclude
    @JsonAlias("display_image")
    private CariFile displayImage;

    public static Aesthetic fromResultSet(ResultSet rs, int rowNum) throws SQLException {
        String startEraString = rs.getString(COLUMN_START_ERA);
        Integer startEra = null;

        if (startEraString != null) {
            startEra = Integer.parseInt(startEraString);
        }

        String endEraString = rs.getString(COLUMN_END_ERA);
        Integer endEra = null;

        if (endEraString != null) {
            endEra = Integer.parseInt(endEraString);
        }

        String displayImageFileString = rs.getString(COLUMN_DISPLAY_IMAGE_FILE);
        Integer displayImageFile = null;

        if (displayImageFileString != null) {
            displayImageFile = Integer.parseInt(displayImageFileString);
        }

        String isDraftString = rs.getString(COLUMN_IS_DRAFT);
        Boolean isDraft = null;

        if (isDraftString != null) {
            isDraft = isDraftString.startsWith("t");
        }

        // @formatter:off
        AestheticBuilder builder = Aesthetic.builder()
            .aesthetic(rs.getInt("aesthetic"))
            .name(rs.getString("name"))
            .urlSlug(rs.getString(COLUMN_URL_SLUG))
            .symbol(rs.getString("symbol"))
            .startEra(startEra)
            .endEra(endEra)
            .description(rs.getString("description"))
            .mediaSourceUrl(rs.getString(COLUMN_MEDIA_SOURCE_URL))
            .displayImageFile(displayImageFile)
            .isDraft(isDraft);
        // @formatter:on

        try {
            builder.displayImageUrl(rs.getString(FIELD_DISPLAY_IMAGE_URL));
        } catch (SQLException e) {
        }

        try {
            builder.startYear(rs.getString(COLUMN_START_YEAR));
        } catch (SQLException ex) {
        }

        try {
            builder.endYear(rs.getString(COLUMN_END_YEAR));
        } catch (SQLException ex) {
        }

        return builder.build();
    }
}
