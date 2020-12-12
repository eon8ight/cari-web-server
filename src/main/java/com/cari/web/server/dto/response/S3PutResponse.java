package com.cari.web.server.dto.response;

import java.io.Serializable;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.cari.web.server.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class S3PutResponse implements Serializable {

    private static final long serialVersionUID = -525745725708931133L;

    private RequestStatus status;

    private String message;

    private PutObjectResult putObjectResult;

    private String key;

    public static S3PutResponse success(PutObjectResult putObjectResult, String key) {
        // @formatter:off
        return S3PutResponse.builder()
            .status(RequestStatus.SUCCESS)
            .putObjectResult(putObjectResult)
            .key(key)
            .build();
        // @formatter:on
    }

    public static S3PutResponse failure(String message) {
        // @formatter:off
        return S3PutResponse.builder()
            .status(RequestStatus.FAILURE)
            .message(message)
            .build();
        // @formatter:on
    }
}
