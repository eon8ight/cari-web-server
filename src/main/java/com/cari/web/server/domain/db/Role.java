package com.cari.web.server.domain.db;

import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.security.core.GrantedAuthority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role implements GrantedAuthority {

    private static final long serialVersionUID = -7049685159055159803L;

    @Id
    @Column
    private int role;

    @Column
    @NotNull
    private String label;

    @Override
    public String getAuthority() {
        return Integer.toString(role);
    }
}
