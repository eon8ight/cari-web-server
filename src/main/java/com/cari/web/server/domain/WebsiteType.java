package com.cari.web.server.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_website_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteType implements Serializable {
    
    private static final long serialVersionUID = 6205086345664654697L;

    private static final String COLUMN_WEBSITE_TYPE = "website_type";

    @Id
    @Column(name = COLUMN_WEBSITE_TYPE, nullable = false, unique = true)
    @JsonAlias({ COLUMN_WEBSITE_TYPE })
    private Integer websiteType;

    @Column(name = "label", nullable = false, length = 50, unique = true)
    private String label;
}
