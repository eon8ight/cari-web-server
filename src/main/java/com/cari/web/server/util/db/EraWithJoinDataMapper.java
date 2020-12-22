package com.cari.web.server.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.cari.web.server.domain.db.Era;
import com.cari.web.server.domain.db.EraSpecifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;

public class EraWithJoinDataMapper implements RowMapper<Era> {

    @Override
    public Era mapRow(ResultSet rs, int rowNum) throws SQLException {
        Era era = Era.fromResultSet(rs, rowNum);
        ObjectMapper mapper = new ObjectMapper();

        try {
            try {
                EraSpecifier eraSpecifier =
                        mapper.readValue(rs.getString("specifier"), EraSpecifier.class);

                era.setSpecifier(eraSpecifier);
            } catch (SQLException ex) {
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        return era;
    }
}
