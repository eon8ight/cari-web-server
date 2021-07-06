package com.cari.web.server.domain.db;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_update_log")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateLog implements Serializable {

    private static final String COLUMN_UPDATE_LOG = "update_log";
    private static final String COLUMN_EVENT_TYPE = "event_type";
    private static final String COLUMN_TABLE_NAME = "table_name";
    private static final String COLUMN_PK_VAL = "pk_val";
    private static final String COLUMN_DESCRIPTION_OVERRIDE = "description_override";

    @Id
    @Column(COLUMN_UPDATE_LOG)
    private int updateLog;

    @Column(COLUMN_EVENT_TYPE)
    private int eventType;

    @CreatedDate
    @Column
    private Timestamp created;

    @CreatedBy
    @Column
    private int creator;

    @Column(COLUMN_TABLE_NAME)
    private int tableName;

    @Column(COLUMN_PK_VAL)
    private int pkVal;

    @Column(COLUMN_DESCRIPTION_OVERRIDE)
    private String descriptionOverride;

    @Transient
    private List<UpdateLogEntry> updateLogEntries;
}
