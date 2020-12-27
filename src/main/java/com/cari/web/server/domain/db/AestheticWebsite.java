package com.cari.web.server.domain.db;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("tb_aesthetic_website")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AestheticWebsite extends ModifiableTable implements EditableAestheticAttachment {

    private static final long serialVersionUID = 8251957923213915929L;

    private static final String COLUMN_AESTHETIC_WEBSITE = "aesthetic_website";
    private static final String COLUMN_WEBSITE_TYPE = "website_type";

    @Id
    @Column(COLUMN_AESTHETIC_WEBSITE)
    @JsonAlias(COLUMN_AESTHETIC_WEBSITE)
    @EqualsAndHashCode.Exclude
    private Integer aestheticWebsite;

    @Column
    private Integer aesthetic;

    @Column
    private String url;

    @Column(COLUMN_WEBSITE_TYPE)
    @JsonAlias(COLUMN_WEBSITE_TYPE)
    private Integer websiteType;

    @Transient
    @EqualsAndHashCode.Exclude
    private WebsiteType type;

    @Override
    public int alternateKeyHash() {
        return Objects.hash(aesthetic, url);
    }
}
