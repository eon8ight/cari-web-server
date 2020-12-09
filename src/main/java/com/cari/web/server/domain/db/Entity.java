package com.cari.web.server.domain.db;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
    private String username;

    @Column(COLUMN_EMAIL_ADDRESS)
    @JsonAlias({COLUMN_EMAIL_ADDRESS})
    @NotNull
    private String emailAddress;

    @Column(COLUMN_PASSWORD_HASH)
    @JsonAlias({COLUMN_PASSWORD_HASH})
    private String passwordHash;

    @Column
    private int inviter;

    @Column
    @NotNull
    private Timestamp invited;

    @Column
    private Timestamp registered;

    @Column
    private Timestamp confirmed;

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

    public static Entity fromResultSet(ResultSet rs, int rowNum) throws SQLException {
        // @formatter:off
        return Entity.builder()
            .entity(rs.getInt("entity"))
            .emailAddress(rs.getString(COLUMN_EMAIL_ADDRESS))
            .username(rs.getString("username"))
            .passwordHash(rs.getString(COLUMN_PASSWORD_HASH))
            .inviter(rs.getInt("inviter"))
            .invited(rs.getTimestamp("invited"))
            .confirmed(rs.getTimestamp("confirmed"))
            .registered(rs.getTimestamp("registered"))
            .build();
        // @formatter:on
    }
}
