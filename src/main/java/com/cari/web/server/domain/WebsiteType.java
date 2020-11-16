package com.cari.web.server.domain;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("tb_website_type")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class WebsiteType implements Serializable {
    private static final long serialVersionUID = 6205086345664654697L;

    private static final String COLUMN_WEBSITE_TYPE = "website_type";
    private static final String COLUMN_VALIDATION_REGEX = "validation_regex";

    @Id
    @Column(COLUMN_WEBSITE_TYPE)
    @JsonAlias({COLUMN_WEBSITE_TYPE})
    private int websiteType;

    private String label;

    @Column(COLUMN_VALIDATION_REGEX)
    @JsonAlias({COLUMN_VALIDATION_REGEX})
    private String validationRegex;
}
