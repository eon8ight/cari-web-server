package com.cari.web.server.dto.response;

import java.io.Serializable;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInviteResponse implements Serializable {

    private static final long serialVersionUID = 3918093176468720847L;

    private RequestStatus status;

    private String message;

    private Entity entity;

    public static UserInviteResponse success(Entity entity) {
        // @formatter:off
        return UserInviteResponse.builder()
            .status(RequestStatus.SUCCESS)
            .entity(entity)
            .build();
        // @formatter:on
    }

    public static UserInviteResponse failure(String message) {
        // @formatter:off
        return UserInviteResponse.builder()
            .status(RequestStatus.FAILURE)
            .message(message)
            .build();
        // @formatter:on
    }
}
