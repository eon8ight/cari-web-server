package com.cari.web.server.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class FileUploadException extends Exception {

    private static final long serialVersionUID = 6325705742172187220L;

    private String message;

    private String key;
}
