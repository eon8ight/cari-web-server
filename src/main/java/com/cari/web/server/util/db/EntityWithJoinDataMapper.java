package com.cari.web.server.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.domain.db.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

public class EntityWithJoinDataMapper implements RowMapper<Entity> {

    private static Logger logger = LoggerFactory.getLogger(EntityWithJoinDataMapper.class);

    private static final Role[] EMPTY_ROLES = new Role[] {null};

    @Override
    public Entity mapRow(ResultSet rs, int rowNum) throws SQLException {
        Entity entity = Entity.fromResultSet(rs, rowNum);
        ObjectMapper mapper = new ObjectMapper();

        try {
            try {
                Role[] roles = mapper.readValue(rs.getString("roles"), Role[].class);

                if (!Arrays.equals(EMPTY_ROLES, roles)) {
                    entity.setRoles(Arrays.asList(roles));
                }
            } catch (SQLException ex) {
            }

            try {
                String profileImageFileString = rs.getString("profile_image");

                if (profileImageFileString != null) {
                    CariFile profileImageFile =
                            mapper.readValue(profileImageFileString, CariFile.class);

                    entity.setProfileImage(profileImageFile);
                }
            } catch (SQLException ex) {
            }

            try {
                String favoriteAestheticString = rs.getString("favorite_aesthetic_data");

                if (favoriteAestheticString != null) {
                    Aesthetic favoriteAesthetic =
                            mapper.readValue(favoriteAestheticString, Aesthetic.class);

                    entity.setFavoriteAestheticData(favoriteAesthetic);
                }
            } catch (SQLException ex) {
            }

            try {
                String rolesForDisplay = rs.getString("roles_for_display");

                if(rolesForDisplay != null) {
                    entity.setRolesForDisplay(rolesForDisplay);
                }
            } catch (SQLException ex) {
            }
        } catch (JsonProcessingException e) {
            logger.error("Error occurred while trying to unmarshall an entity with join data!",
                    e);
        }

        return entity;
    }
}
