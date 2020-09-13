package com.cari.web.server.dto;

import java.io.Serializable;
import com.cari.web.server.domain.Aesthetic;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimilarAesthetic implements Serializable {

    private static final long serialVersionUID = -8280267300482396296L;
    
    private Aesthetic aesthetic;

    private String description;
}
