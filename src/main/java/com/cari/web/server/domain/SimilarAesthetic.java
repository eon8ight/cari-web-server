package com.cari.web.server.domain;

import java.io.Serializable;
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
}
