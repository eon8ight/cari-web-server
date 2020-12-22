package com.cari.web.server.domain.db;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.fasterxml.jackson.annotation.JsonAlias;
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
@Builder
@Table("tb_era")
public class Era implements Serializable {

    private static final long serialVersionUID = 5118357878745421355L;

    private static final String COLUMN_ERA_SPECIFIER = "era_specifier";

    @Id
    private int era;

    @Column(COLUMN_ERA_SPECIFIER)
    @JsonAlias(COLUMN_ERA_SPECIFIER)
    private int eraSpecifier;

    @Column
    private int year;

    @Transient
    private EraSpecifier specifier;

    public static Era fromResultSet(ResultSet rs, int rowNum) throws SQLException {
        // @formatter:off
        return Era.builder()
            .era(rs.getInt("era"))
            .eraSpecifier(rs.getInt(COLUMN_ERA_SPECIFIER))
            .year(rs.getInt("year"))
            .build();
        // @formatter:on
    }
}
