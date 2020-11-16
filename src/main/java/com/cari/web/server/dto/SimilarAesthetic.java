package com.cari.web.server.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SimilarAesthetic implements Serializable {
    private static final long serialVersionUID = -8280267300482396296L;

    @NotNull
    private int aesthetic;

    private String name;

    @JsonAlias("url_slug")
    private String urlSlug;

    @NotNull
    private String description;

    @JsonAlias("reverse_description")
    @NotNull
    private String reverseDescription;
}
