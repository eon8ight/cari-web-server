package com.cari.web.server.domain.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"accountNonExpired", "accountNonLocked", "authorities",
        "credentialsNonExpired", "enabled", "password", "passwordHash"})
public class Entity implements UserDetails {

    private static final long serialVersionUID = -1485850596199572023L;

    private static final String COLUMN_EMAIL_ADDRESS = "email_address";
    private static final String COLUMN_PASSWORD_HASH = "password_hash";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_PROFILE_IMAGE_FILE = "profile_image_file";
    private static final String COLUMN_FAVORITE_AESTHETIC = "favorite_aesthetic";
    private static final String COLUMN_DISPLAY_ON_TEAM_PAGE = "display_on_team_page";

    @Id
    @Column
    private Integer entity;

    @Column
    private String username;

    @Column(COLUMN_EMAIL_ADDRESS)
    @JsonAlias(COLUMN_EMAIL_ADDRESS)
    private String emailAddress;

    @Column(COLUMN_PASSWORD_HASH)
    @JsonAlias(COLUMN_PASSWORD_HASH)
    private String passwordHash;

    @Column
    private Integer inviter;

    @Column
    private Timestamp invited;

    @Column
    private Timestamp registered;

    @Column
    private Timestamp confirmed;

    @Column(COLUMN_FIRST_NAME)
    @JsonAlias(COLUMN_FIRST_NAME)
    private String firstName;

    @Column(COLUMN_LAST_NAME)
    @JsonAlias(COLUMN_LAST_NAME)
    private String lastName;

    private String biography;

    private String title;

    @Column(COLUMN_PROFILE_IMAGE_FILE)
    @JsonAlias(COLUMN_PROFILE_IMAGE_FILE)
    private Integer profileImageFile;

    @Column(COLUMN_FAVORITE_AESTHETIC)
    @JsonAlias(COLUMN_FAVORITE_AESTHETIC)
    private Integer favoriteAesthetic;

    @Column(COLUMN_DISPLAY_ON_TEAM_PAGE)
    @JsonAlias(COLUMN_DISPLAY_ON_TEAM_PAGE)
    private boolean displayOnTeamPage;

    @Transient
    private CariFile profileImage;

    @Transient
    private List<Role> roles;

    @Transient
    private Aesthetic favoriteAestheticData;

    @Transient
    private String rolesForDisplay;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return confirmed != null;
    }

    public static Entity fromResultSet(ResultSet rs, int rowNum) throws SQLException {
        Integer profileImageFile = null;
        String profileImageFileString = rs.getString(COLUMN_PROFILE_IMAGE_FILE);

        if (profileImageFileString != null) {
            profileImageFile = Integer.parseInt(profileImageFileString);
        }

        Integer favoriteAesthetic = null;
        String favoriteAestheticString = rs.getString(COLUMN_FAVORITE_AESTHETIC);

        if (favoriteAestheticString != null) {
            favoriteAesthetic = Integer.parseInt(favoriteAestheticString);
        }

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
            .firstName(rs.getString(COLUMN_FIRST_NAME))
            .lastName(rs.getString(COLUMN_LAST_NAME))
            .biography(rs.getString("biography"))
            .title(rs.getString("title"))
            .profileImageFile(profileImageFile)
            .favoriteAesthetic(favoriteAesthetic)
            .build();
        // @formatter:on
    }
}
