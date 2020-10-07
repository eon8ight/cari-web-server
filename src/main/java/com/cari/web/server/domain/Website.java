package com.cari.web.server.domain;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_website")
public class Website implements Serializable {
    private static final long serialVersionUID = -8924709708781146434L;

    private static final String COLUMN_WEBSITE_TYPE = "website_type";

    @Id
    private int website;

    private String url;

    @Column(COLUMN_WEBSITE_TYPE)
    @JsonAlias({COLUMN_WEBSITE_TYPE})
    @MappedCollection
    private WebsiteType websiteType;
}
