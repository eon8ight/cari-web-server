package com.cari.web.server.domain;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("tb_entity")
public class Entity implements Serializable {

    private static final long serialVersionUID = -1485850596199572023L;

    private static final String COLUMN_EMAIL_ADDRESS = "email_address";
    private static final String COLUMN_PASSWORD_HASH = "password_hash";

    public static final String ROLE_USER = "USER";

    @Id
    private int entity;

    @Column
    @NotNull
    private String username;

    @Column(COLUMN_EMAIL_ADDRESS)
    @JsonAlias({COLUMN_EMAIL_ADDRESS})
    @NotNull
    private String emailAddress;

    @Column(COLUMN_PASSWORD_HASH)
    @JsonAlias({COLUMN_PASSWORD_HASH})
    @NotNull
    private String passwordHash;

    public UserDetails toUserDetails() {
        // @formatter:off
        return User
            .withUsername(Integer.toString(entity))
            .password(passwordHash)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .roles(Entity.ROLE_USER)
            .build();
        // @formatter:on
    }
}
