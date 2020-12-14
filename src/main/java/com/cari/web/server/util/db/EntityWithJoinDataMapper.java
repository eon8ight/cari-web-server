package com.cari.web.server.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.domain.db.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;

public class EntityWithJoinDataMapper implements RowMapper<Entity> {

    private static final Role[] EMPTY_ROLES = new Role[] {null};

    @Override
    public Entity mapRow(ResultSet rs, int rowNum) throws SQLException {
        Entity entity = Entity.fromResultSet(rs, rowNum);
        ObjectMapper mapper = new ObjectMapper();

        try {
            Role[] roles = mapper.readValue(rs.getString("roles"), Role[].class);

            if (!Arrays.equals(EMPTY_ROLES, roles)) {
                entity.setRoles(Arrays.asList(roles));
            }

            try {
                String profileImageFileString = rs.getString("profile_image");

                if (profileImageFileString != null) {
                    CariFile profileImageFile = mapper.readValue(profileImageFileString, CariFile.class);
                    entity.setProfileImage(profileImageFile);
                }
            } catch (SQLException ex) {
                // Ignore
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return entity;
    }
}
