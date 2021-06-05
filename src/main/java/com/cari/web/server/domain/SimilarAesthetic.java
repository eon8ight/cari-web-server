package com.cari.web.server.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import com.cari.web.server.domain.db.AestheticRelationship;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SimilarAesthetic implements Serializable {
    private static final long serialVersionUID = -8280267300482396296L;

    private int aesthetic;

    private String name;

    @JsonAlias("url_slug")
    private String urlSlug;

    private String description;

    @JsonAlias("reverse_description")
    private String reverseDescription;

    @JsonAlias("start_year")
    private String startYear;

    @JsonAlias("end_year")
    private String endYear;

    @JsonAlias("approximate_start_year")
    private Integer approximateStartYear;

    @JsonAlias("approximate_end_year")
    private Integer approximateEndYear;

    @JsonAlias("display_image_url")
    private String displayImageUrl;

    public List<AestheticRelationship> toAestheticRelationships() {
        // @formatter:off
        AestheticRelationship relationship = AestheticRelationship.builder()
            .toAesthetic(aesthetic)
            .description(description)
            .build();

        AestheticRelationship reverseRelationship = AestheticRelationship.builder()
            .fromAesthetic(aesthetic)
            .description(reverseDescription)
            .build();
        // @formatter:on

        return Arrays.asList(relationship, reverseRelationship);
    }
}
