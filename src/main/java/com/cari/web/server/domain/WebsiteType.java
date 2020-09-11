package com.cari.web.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_website_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteType {
    
    @Id
    @Column(name = "website_type", nullable = false, unique = true)
    private Integer websiteType;

    @Column(name = "label", nullable = false, length = 50, unique = true)
    private String label;
}
