package com.cari.web.server.domain.db;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("tb_role_route")
public class RoleRoute implements Serializable {

    private static final long serialVersionUID = 4774435241096423407L;

    private static final String COLUMN_ROLE_ROUTE = "role_route";

    @Id
    @Column(COLUMN_ROLE_ROUTE)
    @JsonAlias(COLUMN_ROLE_ROUTE)
    private int roleRoute;

    @Column
    private int role;

    @Column
    private int route;
}
