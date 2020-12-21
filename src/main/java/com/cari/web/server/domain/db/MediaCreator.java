package com.cari.web.server.domain.db;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("tb_media_creator")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaCreator implements Serializable {
    private static final long serialVersionUID = -2739822601756198464L;

    private static final String COLUMN_MEDIA_CREATOR = "media_creator";

    @Id
    @Column(COLUMN_MEDIA_CREATOR)
    @JsonAlias(COLUMN_MEDIA_CREATOR)
    private Integer mediaCreator;

    @Column
    private String name;
}
