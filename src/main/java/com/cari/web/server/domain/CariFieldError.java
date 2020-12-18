package com.cari.web.server.domain;

import java.io.Serializable;
import java.util.Optional;
import lombok.Data;

@Data
public class CariFieldError implements Serializable {

    private static final long serialVersionUID = 4846094294283733232L;

    private String field;

    private Optional<Integer> index;

    private String message;

    public CariFieldError(String field, String message) {
        this.field = field;
        this.message = message;
        index = Optional.empty();
    }

    public CariFieldError(String field, String message, int index) {
        this.field = field;
        this.message = message;
        this.index = Optional.of(index);
    }
}
