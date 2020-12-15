package com.cari.web.server.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import com.cari.web.server.domain.SimilarAesthetic;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.domain.db.AestheticMedia;
import com.cari.web.server.domain.db.AestheticWebsite;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;

public class AestheticWithJoinDataMapper implements RowMapper<Aesthetic> {

    private static final AestheticWebsite[] EMPTY_WEBSITES = new AestheticWebsite[] {null};
    private static final AestheticMedia[] EMPTY_MEDIA = new AestheticMedia[] {null};

    private static final SimilarAesthetic[] EMPTY_SIMILAR_AESTHETICS =
            new SimilarAesthetic[] {null};

    public Aesthetic mapRow(ResultSet rs, int rowNum) throws SQLException {
        Aesthetic aesthetic = Aesthetic.fromResultSet(rs, rowNum);
        ObjectMapper mapper = new ObjectMapper();

        try {
            try {
                AestheticWebsite[] websites =
                        mapper.readValue(rs.getString("websites"), AestheticWebsite[].class);

                if (!Arrays.equals(EMPTY_WEBSITES, websites)) {
                    aesthetic.setWebsites(Arrays.asList(websites));
                }
            } catch (SQLException ex) {
            }

            try {
                AestheticMedia[] media =
                        mapper.readValue(rs.getString("media"), AestheticMedia[].class);

                if (!Arrays.equals(EMPTY_MEDIA, media)) {
                    aesthetic.setMedia(Arrays.asList(media));
                }
            } catch (SQLException ex) {
            }

            try {
                SimilarAesthetic[] similarAesthetics = mapper
                        .readValue(rs.getString("similar_aesthetics"), SimilarAesthetic[].class);

                if (!Arrays.equals(EMPTY_SIMILAR_AESTHETICS, similarAesthetics)) {
                    aesthetic.setSimilarAesthetics(Arrays.asList(similarAesthetics));
                }
            } catch (SQLException ex) {
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return aesthetic;
    }
}
