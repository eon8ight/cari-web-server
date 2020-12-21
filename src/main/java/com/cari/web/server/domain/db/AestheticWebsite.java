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
@Table("tb_aesthetic_website")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AestheticWebsite implements Serializable {
    private static final long serialVersionUID = 8251957923213915929L;

    private static final String COLUMN_AESTHETIC_WEBSITE = "aesthetic_website";
    private static final String COLUMN_WEBSITE_TYPE = "website_type";

    @Id
    @Column(COLUMN_AESTHETIC_WEBSITE)
    @JsonAlias(COLUMN_AESTHETIC_WEBSITE)
    private Integer aestheticWebsite;

    @Column
    private Integer aesthetic;

    @Column
    private String url;

    @Column(COLUMN_WEBSITE_TYPE)
    @JsonAlias(COLUMN_WEBSITE_TYPE)
    private Integer websiteType;

    @Transient
    private WebsiteType type;
}
