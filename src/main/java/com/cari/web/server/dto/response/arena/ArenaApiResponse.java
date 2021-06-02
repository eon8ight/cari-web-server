package com.cari.web.server.dto.response.arena;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArenaApiResponse implements Serializable {
    private static final long serialVersionUID = -4425120038501378562L;

    private Integer length;

    private List<ArenaContent> contents;

    private Integer errorStatusCode;

    private String errorMessage;
}
