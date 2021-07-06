package com.cari.web.server.dto.response;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.cari.web.server.domain.CariFieldError;
import com.cari.web.server.enums.RequestStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CariResponse implements Serializable {
    private static final long serialVersionUID = 3803442021113784136L;

    private RequestStatus status;

    private String message;

    private List<CariFieldError> fieldErrors;

    private Map<String, Object> updatedData;

    public static CariResponse success() {
        return CariResponse.builder().status(RequestStatus.SUCCESS).build();
    }

    public static CariResponse failure(String message) {
        return CariResponse.builder().status(RequestStatus.FAILURE).message(message).build();
    }

    public static CariResponse failure(List<CariFieldError> fieldErrors) {
        return CariResponse.builder().status(RequestStatus.FAILURE).fieldErrors(fieldErrors)
                .build();
    }
}
