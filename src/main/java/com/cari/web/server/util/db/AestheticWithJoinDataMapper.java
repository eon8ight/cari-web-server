package com.cari.web.server.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import com.cari.web.server.domain.SimilarAesthetic;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.domain.db.AestheticMedia;
import com.cari.web.server.domain.db.AestheticWebsite;
import com.cari.web.server.domain.db.CariFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

public class AestheticWithJoinDataMapper implements RowMapper<Aesthetic> {

    private Logger logger = LoggerFactory.getLogger(AestheticWithJoinDataMapper.class);

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

            try {
                String displayImageString = rs.getString("display_image");

                if (displayImageString != null) {
                    CariFile displayImage =
                            mapper.readValue(displayImageString, CariFile.class);

                    aesthetic.setDisplayImage(displayImage);
                }
            } catch (SQLException ex) {
            }
        } catch (JsonProcessingException e) {
            logger.error("Error occurred while trying to unmarshall an aesthetic with join data!",
                    e);
        }

        return aesthetic;
    }
}
