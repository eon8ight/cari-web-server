package com.cari.web.server.domain;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class UpdateLogRollup implements Serializable {

    private Date created;

    private List<String> entries;

    public static UpdateLogRollup fromResultSet(ResultSet rs, int rowNum) throws SQLException {
        Date created = rs.getDate("created");
        String[] entries = (String[]) rs.getArray("entries").getArray();
        return new UpdateLogRollup(created, Arrays.asList(entries));
    }
}
