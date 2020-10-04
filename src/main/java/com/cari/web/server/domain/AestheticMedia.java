package com.cari.web.server.domain;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_aesthetic_media")
public class AestheticMedia implements Serializable {

    private static final long serialVersionUID = -4258598762766168605L;

    private static final String COLUMN_AESTHETIC_MEDIA = "aesthetic_media";

    @Id
    @Column(COLUMN_AESTHETIC_MEDIA)
    @JsonAlias({COLUMN_AESTHETIC_MEDIA})
    private int aestheticMedia;

    @Column
    private int aesthetic;

    @Column
    private int media;

    @Transient
    private Aesthetic aestheticObj;

    @Transient
    private Media mediaObj;
}
