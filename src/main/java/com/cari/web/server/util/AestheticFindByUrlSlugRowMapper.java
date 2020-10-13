package com.cari.web.server.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.domain.Media;
import com.cari.web.server.domain.Website;
import com.cari.web.server.dto.SimilarAesthetic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;

public class AestheticFindByUrlSlugRowMapper implements RowMapper<Aesthetic> {
    public Aesthetic mapRow(ResultSet rs, int rowNum) throws SQLException {
        Aesthetic aesthetic = Aesthetic.fromResultSet(rs, rowNum);

        ObjectMapper mapper = new ObjectMapper();

        try {
            Website[] websites = mapper.readValue(rs.getString("websites"), Website[].class);
            Media[] media = mapper.readValue(rs.getString("media"), Media[].class);

            SimilarAesthetic[] similarAesthetics =
                    mapper.readValue(rs.getString("similar_aesthetics"), SimilarAesthetic[].class);

            aesthetic.setWebsites(Arrays.asList(websites));
            aesthetic.setMedia(Arrays.asList(media));
            aesthetic.setSimilarAesthetics(Arrays.asList(similarAesthetics));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return aesthetic;
    }
}
