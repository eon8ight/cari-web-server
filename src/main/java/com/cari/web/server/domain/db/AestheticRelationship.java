package com.cari.web.server.domain.db;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("tb_aesthetic_relationship")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AestheticRelationship extends ModifiableTable implements EditableAestheticAttachment {

    private static final long serialVersionUID = -3625374001558766927L;

    private static final String COLUMN_FROM_AESTHETIC = "from_aesthetic";
    private static final String COLUMN_TO_AESTHETIC = "to_aesthetic";

    private static final int ALTERNATE_KEY_HASH_PRIME = 1009;

    @Id
    @Column("aesthetic_relationship")
    @EqualsAndHashCode.Exclude
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
    @EqualsAndHashCode.Exclude
    private Aesthetic from;

    @Transient
    @EqualsAndHashCode.Exclude
    private Aesthetic to;

    @Override
    public int alternateKeyHash() {
        // Taken from OpenJDK's implementation of Arrays.hashCode(int[]), unrolled, and with a
        // different prime
        int result = ALTERNATE_KEY_HASH_PRIME + fromAesthetic;
        result = ALTERNATE_KEY_HASH_PRIME * result + toAesthetic;
        return result;
    }
}
