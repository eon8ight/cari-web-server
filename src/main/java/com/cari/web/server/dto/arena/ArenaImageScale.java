package com.cari.web.server.dto.arena;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ArenaImageScale implements Serializable {

    private static final long serialVersionUID = 5591895042955700664L;

    private String url;

    @JsonProperty("file_size")
    private int fileSize;

    @JsonProperty("file_size_display")
    private String fileSizeDisplay;
}
