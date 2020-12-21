package com.cari.web.server.dto.response.arena;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArenaSource implements Serializable {

    private static final long serialVersionUID = 6242135197557039905L;

    private String url;

    private String title;

    private ArenaSourceProvider provider;
}
