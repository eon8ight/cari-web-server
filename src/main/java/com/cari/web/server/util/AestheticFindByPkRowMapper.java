package com.cari.web.server.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.cari.web.server.domain.Aesthetic;
import org.springframework.jdbc.core.RowMapper;

public class AestheticFindByPkRowMapper implements RowMapper<Aesthetic> {

    @Override
    public Aesthetic mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Aesthetic.fromResultSet(rs, rowNum);
    }
}
