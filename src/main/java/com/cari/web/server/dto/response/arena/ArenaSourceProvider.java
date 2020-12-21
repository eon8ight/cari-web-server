package com.cari.web.server.dto.response.arena;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArenaSourceProvider implements Serializable {

    private static final long serialVersionUID = 5883948498588595083L;

    private String name;

    private String url;
}