package com.cari.web.server.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_table_name")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableName {

    private static final String COLUMN_TABLE_NAME = "table_name";
    private static final String COLUMN_DISPLAY_NAME = "display_name";

    public static final int TB_AESTHETIC = 1;

    @Id
    @Column(COLUMN_TABLE_NAME)
    private int tableName;

    @Column
    private String name;

    @Column(COLUMN_DISPLAY_NAME)
    private String displayName;
}
