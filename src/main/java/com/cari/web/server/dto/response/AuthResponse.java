package com.cari.web.server.dto.response;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import com.cari.web.server.enums.RequestStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse implements Serializable {
    private static final long serialVersionUID = 3803442021113784136L;

    private RequestStatus status;

    private String message;

    private transient Optional<String> token;

    private List<CariFieldError> fieldErrors;

    public static AuthResponse success() {
        return success(Optional.empty());
    }

    public static AuthResponse success(Optional<String> token) {
        // @formatter:off
        return AuthResponse.builder()
            .status(RequestStatus.SUCCESS)
            .token(token)
            .build();
        // @formatter:on
    }

    public static AuthResponse failure(String message) {
        // @formatter:off
        return AuthResponse.builder()
            .status(RequestStatus.FAILURE)
            .message(message)
            .build();
        // @formatter:on
    }

    public static AuthResponse failure(List<CariFieldError> fieldErrors) {
        // @formatter:off
        return AuthResponse.builder()
            .status(RequestStatus.FAILURE)
            .fieldErrors(fieldErrors)
            .build();
        // @formatter:on
    }
}
