package com.cari.web.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_aesthetic")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aesthetic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer aesthetic;

    private String name;

    private String urlSlug;

    private String symbol;

    private Integer startYear;

    private Integer endYear;

    private String description;
}
