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
@Table("tb_file")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CariFile implements Serializable {

    private static final long serialVersionUID = 8463393495949610778L;

    private static final String COLUMN_FILE_TYPE = "file_type";

    @Id
    @Column
    private Integer file;

    @Column(COLUMN_FILE_TYPE)
    @JsonAlias(COLUMN_FILE_TYPE)
    private Integer fileType;

    @Column
    private String url;

    @Column
    private Integer width;

    @Column
    private Integer height;

    @Transient
    private FileType type;
}
