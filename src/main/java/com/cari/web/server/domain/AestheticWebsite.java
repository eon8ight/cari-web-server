package com.cari.web.server.domain;

import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_aesthetic_website")
public class AestheticWebsite implements Serializable {
    private static final long serialVersionUID = 8251957923213915929L;

    private static final String COLUMN_AESTHETIC_WEBSITE = "aesthetic_website";
    private static final String COLUMN_WEBSITE_TYPE = "website_type";

    @Id
    @Column(COLUMN_AESTHETIC_WEBSITE)
    @JsonAlias({COLUMN_AESTHETIC_WEBSITE})
    private int aestheticWebsite;

    @Column
    @NotNull
    private int aesthetic;

    @Column
    @NotNull
    private String url;

    @Column(COLUMN_WEBSITE_TYPE)
    @JsonAlias({COLUMN_WEBSITE_TYPE})
    @MappedCollection
    @NotNull
    @Valid
    private WebsiteType websiteType;

    @Transient
    private Aesthetic aestheticObj;
}