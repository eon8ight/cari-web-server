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
@Table("tb_aesthetic_website")
public class AestheticWebsite implements Serializable {

    private static final long serialVersionUID = 8251957923213915929L;

    private static final String COLUMN_AESTHETIC_WEBSITE = "aesthetic_website";

    @Id
    @Column(COLUMN_AESTHETIC_WEBSITE)
    @JsonAlias({COLUMN_AESTHETIC_WEBSITE})
    private int aestheticWebsite;

    @Column
    private int aesthetic;

    @Column
    private int website;

    @Transient
    private Aesthetic aestheticObj;

    @Transient
    private Website websiteObj;
}
