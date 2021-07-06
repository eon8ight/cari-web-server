package com.cari.web.server.domain.db;

import java.io.Serializable;
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
@Table("tb_event_type")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventType implements Serializable {

    private static final String COLUMN_EVENT_TYPE = "event_type";

    public static final int CREATED = 1;
    public static final int UPDATED = 2;

    @Id
    @Column(COLUMN_EVENT_TYPE)
    private int eventType;

    @Column
    private String label;
}
