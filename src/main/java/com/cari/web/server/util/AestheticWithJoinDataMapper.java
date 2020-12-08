package com.cari.web.server.util;

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
            AestheticWebsite[] websites = mapper.readValue(rs.getString("websites"), AestheticWebsite[].class);
            AestheticMedia[] media = mapper.readValue(rs.getString("media"), AestheticMedia[].class);

            SimilarAesthetic[] similarAesthetics =
                    mapper.readValue(rs.getString("similar_aesthetics"), SimilarAesthetic[].class);

            if (!Arrays.equals(EMPTY_WEBSITES, websites)) {
                aesthetic.setWebsites(Arrays.asList(websites));
            }

            if (!Arrays.equals(EMPTY_MEDIA, media)) {
                aesthetic.setMedia(Arrays.asList(media));
            }

            if (!Arrays.equals(EMPTY_SIMILAR_AESTHETICS, similarAesthetics)) {
                aesthetic.setSimilarAesthetics(Arrays.asList(similarAesthetics));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return aesthetic;
    }
}
