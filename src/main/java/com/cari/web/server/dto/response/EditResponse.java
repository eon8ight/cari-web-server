package com.cari.web.server.dto.response;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.cari.web.server.enums.RequestStatus;
import org.springframework.validation.FieldError;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditResponse implements Serializable {

    private static final long serialVersionUID = -7975745000750598570L;

    private RequestStatus status;

    @Builder.Default
    private List<FieldError> errors = Collections.emptyList();

    public static EditResponse success() {
        // @formatter:off
        return EditResponse.builder()
            .status(RequestStatus.SUCCESS)
            .build();
        // @formatter:on
    }

    public static EditResponse failure(FieldError... fieldErrors) {
        // @formatter:off
        return EditResponse
            .builder()
            .status(RequestStatus.FAILURE)
            .errors(Arrays.asList(fieldErrors))
            .build();
        // @formatter:on
    }
}
