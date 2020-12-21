package com.cari.web.server.dto.response.arena;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArenaEmbed implements Serializable {

    private static final long serialVersionUID = 1954999127553289560L;

    private String url;

    private String type;

    private String title;

    @JsonProperty("author_url")
    private String authorUrl;

    @JsonProperty("source_url")
    private String sourceUrl;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    private int width;

    private int height;

    private String html;
}
