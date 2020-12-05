package com.cari.web.server.dto.response;

import java.io.Serializable;
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

    private transient String token;

    private String field;

    public static AuthResponse success(String token) {
        // @formatter:off
        return AuthResponse.builder()
            .status(RequestStatus.SUCCESS)
            .token(token)
            .build();
        // @formatter:on
    }

    public static AuthResponse failure(String message) {
        return failure(message, null);
    }

    public static AuthResponse failure(String message, String field) {
        // @formatter:off
        return AuthResponse.builder()
            .status(RequestStatus.FAILURE)
            .message(message)
            .field(field)
            .build();
        // @formatter:on
    }
}