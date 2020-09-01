package com.cari.web.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Aesthetic {

    private Long aesthetic;
    private String name;
    private String urlName;
    private String symbol;
    private Integer startYear;
    private Integer endYear;
    private String description;

}
