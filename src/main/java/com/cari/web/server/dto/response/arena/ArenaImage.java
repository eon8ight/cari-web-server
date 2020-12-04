package com.cari.web.server.dto.response.arena;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArenaImage implements Serializable {

    private static final long serialVersionUID = 1929060722366533676L;

    private String filename;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    private ArenaImageScale thumb;

    private ArenaImageScale square;

    private ArenaImageScale display;

    private ArenaImageScale large;

    private ArenaImageScale original;
}
