package com.cari.web.server.dto.response;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.cari.web.server.enums.RequestStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.validation.FieldError;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CariResponse implements Serializable {
    private static final long serialVersionUID = 3803442021113784136L;

    private RequestStatus status;

    private String message;

    private List<FieldError> fieldErrors;

    private Map<String, Object> updatedData;

    public static CariResponse success() {
        return CariResponse.builder().status(RequestStatus.SUCCESS).build();
    }

    public static CariResponse failure(String message) {
        // @formatter:off
        return CariResponse.builder()
            .status(RequestStatus.FAILURE)
            .message(message)
            .build();
        // @formatter:on
    }

    public static CariResponse failure(List<FieldError> fieldErrors) {
        // @formatter:off
        return CariResponse.builder()
            .status(RequestStatus.FAILURE)
            .fieldErrors(fieldErrors)
            .build();
        // @formatter:on
    }
}
