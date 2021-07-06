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
@Table("tb_updatable_field")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdatableField {

    private static final String COLUMN_UPDATABLE_FIELD = "updatable_field";
    private static final String COLUMN_TABLE_NAME = "table_name";

    public static final int AESTHETIC_AESTHETIC = 1;
    public static final int AESTHETIC_NAME = 2;
    public static final int AESTHETIC_START_ERA = 3;
    public static final int AESTHETIC_END_ERA = 4;
    public static final int AESTHETIC_DESCRIPTION = 5;
    public static final int AESTHETIC_ARENA_GALLERY_URL = 6;
    public static final int AESTHETIC_THUMBNAIL = 7;
    public static final int AESTHETIC_WEBSITES = 8;
    public static final int AESTHETIC_MEDIA = 9;
    public static final int AESTHETIC_RELATED_AESTHETICS = 10;

    @Id
    @Column(COLUMN_UPDATABLE_FIELD)
    private int updatableField;

    @Column(COLUMN_TABLE_NAME)
    private int tableName;

    @Column
    private String name;
}
