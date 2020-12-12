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
@Table("tb_file_type")
public class FileType implements Serializable {

    private static final long serialVersionUID = -8311730054742861454L;

    private static final String COLUMN_FILE_TYPE = "file_type";

    public static final int FILE_TYPE_IMAGE = 1;

    @Id
    @Column(COLUMN_FILE_TYPE)
    @JsonAlias(COLUMN_FILE_TYPE)
    private int fileType;

    @Column
    @NotNull
    private String label;
}
