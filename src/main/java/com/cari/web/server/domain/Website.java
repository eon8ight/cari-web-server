package com.cari.web.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_website")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Website {
    
    @Id
    @Column(name = "website", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer website;

    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @JoinColumn(name = "website_type", nullable = false, unique = true)
    @OneToOne
    private WebsiteType websiteType;
}
