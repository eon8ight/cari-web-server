package com.cari.web.server.domain.db;

import java.io.Serializable;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_aesthetic_relationship")
public class AestheticRelationship implements Serializable {
    private static final long serialVersionUID = -3625374001558766927L;

    private static final String COLUMN_FROM_AESTHETIC = "from_aesthetic";
    private static final String COLUMN_TO_AESTHETIC = "to_aesthetic";

    @Id
    @Column("aesthetic_relationship")
    private int aestheticRelationship;

    @Column(COLUMN_FROM_AESTHETIC)
    @JsonAlias(COLUMN_FROM_AESTHETIC)
    private int fromAesthetic;

    @Column(COLUMN_TO_AESTHETIC)
    @JsonAlias(COLUMN_TO_AESTHETIC)
    private int toAesthetic;

    @Column
    private String description;

    @Transient
    private Aesthetic fromAestheticObj;

    @Transient
    private Aesthetic toAestheticObj;
}
