package com.cari.web.server.dto.arena;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArenaAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("file_size")
    private int fileSize;

    @JsonProperty("file_size_display")
    private String fileSizeDisplay;

    @JsonProperty("content_type")
    private String contentType;

    private String extension;

    private String url;
}
