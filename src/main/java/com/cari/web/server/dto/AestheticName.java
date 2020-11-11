package com.cari.web.server.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AestheticName implements Serializable {
    private static final long serialVersionUID = 1L;

    private int aesthetic;

    private String name;
}
