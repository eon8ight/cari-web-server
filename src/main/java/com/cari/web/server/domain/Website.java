package com.cari.web.server.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_website")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Website implements Serializable {
    
    private static final long serialVersionUID = -8924709708781146434L;

    private static final String COLUMN_WEBSITE_TYPE = "website_type";

    @Id
    @Column(name = "website", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer website;

    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @JoinColumn(name = COLUMN_WEBSITE_TYPE, nullable = false, unique = true)
    @JsonAlias({ COLUMN_WEBSITE_TYPE })
    @OneToOne
    private WebsiteType websiteType;
}
