package com.cari.web.server.domain.db;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("tb_http_method")
public class CariHttpMethod implements Serializable {

    private static final long serialVersionUID = 1883824808564356393L;

    private static final String COLUMN_HTTP_METHOD = "http_method";

    @Id
    @Column(COLUMN_HTTP_METHOD)
    @JsonAlias(COLUMN_HTTP_METHOD)
    private int httpMethod;

    @Column
    private String label;
}
