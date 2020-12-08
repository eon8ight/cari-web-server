package com.cari.web.server.domain.db;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_media_creator")
public class MediaCreator implements Serializable {
    private static final long serialVersionUID = -2739822601756198464L;

    private static final String COLUMN_MEDIA_CREATOR = "media_creator";

    @Id
    @Column(COLUMN_MEDIA_CREATOR)
    @JsonAlias({COLUMN_MEDIA_CREATOR})
    private Integer mediaCreator;

    @NotNull
    private String name;
}
