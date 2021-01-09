package com.cari.web.server.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.cari.web.server.domain.db.Route;
import org.springframework.jdbc.core.RowMapper;

public class RouteWithJoinDataMapper implements RowMapper<Route> {

    @Override
    public Route mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Route.fromResultSet(rs, rowNum);
    }
}
