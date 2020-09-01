package com.cari.web.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AestheticRelation {

    private Long aestheticRelation;
    private Long fromAesthetic;
    private Long toAesthetic;
    private String description;

}

