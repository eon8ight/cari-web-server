package com.cari.web.server.domain.db;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@Table("tb_aesthetic_relationship")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AestheticRelationship implements Serializable {
    private static final long serialVersionUID = -3625374001558766927L;

    private static final String COLUMN_FROM_AESTHETIC = "from_aesthetic";
    private static final String COLUMN_TO_AESTHETIC = "to_aesthetic";

    @Id
    @Column("aesthetic_relationship")
    private Integer aestheticRelationship;

    @Column(COLUMN_FROM_AESTHETIC)
    @JsonAlias(COLUMN_FROM_AESTHETIC)
    private Integer fromAesthetic;

    @Column(COLUMN_TO_AESTHETIC)
    @JsonAlias(COLUMN_TO_AESTHETIC)
    private Integer toAesthetic;

    @Column
    private String description;

    @Transient
    private Aesthetic from;

    @Transient
    private Aesthetic to;
}
