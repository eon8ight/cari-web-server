package com.cari.web.server.dto.response;

import java.io.Serializable;
import java.util.Map;
import com.cari.web.server.enums.TokenValidity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckedTokenResponse implements Serializable {

    private static final long serialVersionUID = 3860082214698425118L;

    private TokenValidity status;

    private Map<String, Object> tokenClaims;

    public static CheckedTokenResponse valid(Map<String, Object> tokenClaims) {
        return CheckedTokenResponse.builder().status(TokenValidity.VALID).tokenClaims(tokenClaims)
                .build();
    }

    public static CheckedTokenResponse invalid() {
        return CheckedTokenResponse.builder().status(TokenValidity.INVALID).build();
    }
}
