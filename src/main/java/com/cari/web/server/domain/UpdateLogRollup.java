package com.cari.web.server.domain;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class UpdateLogRollup implements Serializable {

    private Date created;

    @JsonAlias("update_log")
    private List<UpdateLogRollupEntry> updateLog;

    public static UpdateLogRollup fromResultSet(ResultSet rs, int rowNum) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();

        Date created = rs.getDate("created");
        UpdateLogRollupEntry[] entries;

        try {
            entries = mapper.readValue(rs.getString("update_log"), UpdateLogRollupEntry[].class);
        } catch (JsonProcessingException ex) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ex.getLocalizedMessage());
        }

        return new UpdateLogRollup(created, Arrays.asList(entries));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateLogRollupEntry implements Serializable {

        private String description;

        @JsonAlias("aesthetic_url_slug")
        private String aestheticUrlSlug;
    }
}
