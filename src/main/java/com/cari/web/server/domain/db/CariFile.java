package com.cari.web.server.domain.db;

import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
@Table("tb_file")
public class CariFile implements Serializable {

    private static final long serialVersionUID = 8463393495949610778L;

    private static final String COLUMN_FILE_TYPE = "file_type";

    @Id
    @Column
    private int file;

    @Column(COLUMN_FILE_TYPE)
    @JsonAlias(COLUMN_FILE_TYPE)
    private int fileType;

    @NotNull
    private String url;

    @Column
    private int width;

    @Column
    private int height;

    @Transient
    @Valid
    private FileType type;
}
