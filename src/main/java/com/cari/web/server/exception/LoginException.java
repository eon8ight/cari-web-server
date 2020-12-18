package com.cari.web.server.exception;

import java.util.Arrays;
import java.util.List;
import com.cari.web.server.domain.CariFieldError;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LoginException extends RuntimeException {

    private static final long serialVersionUID = 512646940743883600L;

    private List<CariFieldError> errors;

    public LoginException(CariFieldError... errors) {
        this.errors = Arrays.asList(errors);
    }
}
