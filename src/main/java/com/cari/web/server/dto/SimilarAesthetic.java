package com.cari.web.server.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimilarAesthetic implements Serializable {

    private static final long serialVersionUID = -8280267300482396296L;

    private String name;

    @JsonAlias("url_slug")
    private String urlSlug;

    private String description;
}