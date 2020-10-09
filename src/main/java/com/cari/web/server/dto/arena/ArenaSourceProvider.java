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
public class ArenaSourceProvider implements Serializable {

    private static final long serialVersionUID = 5883948498588595083L;

    private String name;

    private String url;
}