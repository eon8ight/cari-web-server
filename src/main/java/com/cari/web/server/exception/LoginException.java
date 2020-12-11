package com.cari.web.server.exception;

import java.util.Arrays;
import java.util.List;
import org.springframework.validation.FieldError;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LoginException extends Exception {

    private static final long serialVersionUID = 512646940743883600L;

    private List<FieldError> errors;

    public LoginException(FieldError... errors) {
        this.errors = Arrays.asList(errors);
    }
}
