package com.cari.web.server.domain.db;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EntityRole {

    private static final String COLUMN_ENTITY_ROLE = "entity_role";

    @Id
    @Column(COLUMN_ENTITY_ROLE)
    @JsonAlias(COLUMN_ENTITY_ROLE)
    private Integer entityRole;

    @Column
    private int entity;

    @Column
    private int role;

    public EntityRole(int entity, int role) {
        this.entity = entity;
        this.role = role;
    }
}