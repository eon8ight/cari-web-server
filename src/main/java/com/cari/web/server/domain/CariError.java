package com.cari.web.server.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CariError implements Serializable {
    
    private static final long serialVersionUID = 3803442021113784136L;

    private String message;
}