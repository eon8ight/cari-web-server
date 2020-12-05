package com.cari.web.server.dto.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CariFieldError implements Serializable {

    private static final long serialVersionUID = 1L;

    private String field;

    private String message;
}
