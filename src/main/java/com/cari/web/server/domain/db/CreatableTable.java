package com.cari.web.server.domain.db;

import java.io.Serializable;
import java.sql.Timestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public abstract class CreatableTable implements Serializable {

    private static final long serialVersionUID = -1518390950542483389L;

    @Column
    @CreatedBy
    @EqualsAndHashCode.Exclude
    protected int creator;

    @Column
    @CreatedDate
    @EqualsAndHashCode.Exclude
    protected Timestamp created;
}
