package com.cari.web.server.dto.arena;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArenaSource implements Serializable {

    private static final long serialVersionUID = 6242135197557039905L;

    private String url;

    private String title;

    private ArenaSourceProvider provider;
}