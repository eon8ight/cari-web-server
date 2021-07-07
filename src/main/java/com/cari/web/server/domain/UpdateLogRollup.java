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
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class UpdateLogRollup implements Serializable {

    private Date created;

    private List<Entry> entries;

    public static UpdateLogRollup fromResultSet(ResultSet rs, int rowNum) throws SQLException {
        Date created = rs.getDate("created");
        String entriesString = rs.getString("entries");

        ObjectMapper objectMapper = new ObjectMapper();
        Entry[] entriesArray;

        try {
            entriesArray = objectMapper.readValue(entriesString, Entry[].class);
        } catch (JsonProcessingException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getLocalizedMessage());
        }

        return new UpdateLogRollup(created, Arrays.asList(entriesArray));
    }

    @Data
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Entry implements Serializable {

        @JsonAlias("update_log")
        private int updateLog;

        @JsonAlias("created_time")
        private String createdTime;

        private String creator;

        @JsonAlias("event_type")
        private int eventType;

        @JsonAlias("event_type_label")
        private String eventTypeLabel;

        @JsonAlias("table_display_name")
        private String tableDisplayName;

        @JsonAlias("aesthetic_name")
        private String aestheticName;

        @JsonAlias("aesthetic_url_slug")
        private String aestheticUrlSlug;

        @JsonAlias("updated_fields")
        private List<UpdatedField> updatedFields;

        @JsonAlias("description_override")
        private String descriptionOverride;

        @Data
        @JsonInclude(value = JsonInclude.Include.NON_NULL)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class UpdatedField implements Serializable {

            private String name;

            @JsonAlias("new_value")
            private String newValue;

            @JsonAlias("old_value")
            private String oldValue;
        }
    }
}
