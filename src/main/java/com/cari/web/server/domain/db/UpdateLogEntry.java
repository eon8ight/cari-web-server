package com.cari.web.server.domain.db;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("tb_update_log_entry")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateLogEntry implements Serializable {

    private static final String COLUMN_UPDATE_LOG_ENTRY = "update_log_entry";
    private static final String COLUMN_UPDATE_LOG = "update_log";
    private static final String COLUMN_UPDATABLE_FIELD = "updatable_field";
    private static final String COLUMN_OLD_VALUE = "old_value";
    private static final String COLUMN_NEW_VALUE = "new_value";

    @Id
    @Column(COLUMN_UPDATE_LOG_ENTRY)
    private Integer updateLogEntry;

    @Column(COLUMN_UPDATE_LOG)
    private Integer updateLog;

    @Column(COLUMN_UPDATABLE_FIELD)
    private Integer updatableField;

    @Column(COLUMN_OLD_VALUE)
    private String oldValue;

    @Column(COLUMN_NEW_VALUE)
    private String newValue;
}
