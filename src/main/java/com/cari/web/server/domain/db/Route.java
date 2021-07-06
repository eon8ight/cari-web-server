package com.cari.web.server.domain.db;

import java.io.Serializable;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("tb_route")
public class Route implements Serializable {

    private static final long serialVersionUID = -8631449240726877313L;

    private static final String COLUMN_HTTP_METHOD = "http_method";
    private static final String COLUMN_HTTP_METHOD_LABEL = "http_method_label";

    @Id
    @Column
    private int route;

    @Column(COLUMN_HTTP_METHOD)
    @JsonAlias(COLUMN_HTTP_METHOD)
    private int httpMethod;

    @Column
    private String url;

    @Transient
    @JsonAlias(COLUMN_HTTP_METHOD_LABEL)
    private String httpMethodLabel;

    @Transient
    private List<Integer> roles;

    public static Route fromResultSet(ResultSet rs, int rowNum) throws SQLException {
        RouteBuilder builder = Route.builder().route(rs.getInt("route"))
                .httpMethod(rs.getInt(COLUMN_HTTP_METHOD)).url(rs.getString("url"));

        try {
            builder.httpMethodLabel(rs.getString(COLUMN_HTTP_METHOD_LABEL));
        } catch (SQLException ex) {
        }

        try {
            Array rolesArray = rs.getArray("roles");
            Integer[] roles = (Integer[]) rolesArray.getArray();
            builder.roles(Arrays.asList(roles));
        } catch (SQLException ex) {
        }

        return builder.build();
    }
}
