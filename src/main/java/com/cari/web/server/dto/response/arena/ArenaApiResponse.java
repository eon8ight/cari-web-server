package com.cari.web.server.dto.response.arena;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArenaApiResponse implements Serializable {
    private static final long serialVersionUID = -4425120038501378562L;

    private int length;

    private List<ArenaContent> contents;
}
