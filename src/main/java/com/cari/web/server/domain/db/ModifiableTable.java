package com.cari.web.server.domain.db;

import java.sql.Timestamp;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public abstract class ModifiableTable extends CreatableTable {

    private static final long serialVersionUID = 199408893184545841L;

    @Column
    @LastModifiedBy
    @EqualsAndHashCode.Exclude
    protected int modifier;

    @Column
    @LastModifiedDate
    @EqualsAndHashCode.Exclude
    protected Timestamp modified;
}
