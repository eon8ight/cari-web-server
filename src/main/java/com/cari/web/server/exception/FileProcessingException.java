package com.cari.web.server.exception;

import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FileProcessingException extends Exception {

    private static final long serialVersionUID = 8651682979506486907L;

    private String message;

    private Optional<Integer> listIndex;

    public FileProcessingException(String message) {
        this.message = message;
        listIndex = Optional.empty();
    }

    public FileProcessingException(String message, int listIndex) {
        this.message = message;
        this.listIndex = Optional.of(listIndex);
    }
}
