package com.cari.web.server.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("tb_role")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role implements GrantedAuthority {

    private static final long serialVersionUID = -7049685159055159803L;

    public static final int ADMIN = 1;

    @Id
    @Column
    private Integer role;

    @Column
    private String label;

    @Override
    public String getAuthority() {
        return Integer.toString(role);
    }
}
