package com.cari.web.server.domain.db;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("tb_era_specifier")
public class EraSpecifier implements Serializable {

    private static final long serialVersionUID = 5896328852358113449L;

    private static final String COLUMN_ERA_SPECIFIER = "era_specifier";

    @Id
    @Column(COLUMN_ERA_SPECIFIER)
    @JsonAlias(COLUMN_ERA_SPECIFIER)
    private int eraSpecifier;

    private String label;

    private int weight;
}
