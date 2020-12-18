package com.cari.web.server.dto.request;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AestheticEditRequest implements Serializable {

    private static final long serialVersionUID = -7739431384984288058L;

    private int aesthetic;

    private String description;

    private String endYear;

    private List<Map<Object, Object>> media;

    private String mediaSourceUrl;

    private String name;

    private List<Map<Object, Object>> similarAesthetics;

    private String startYear;

    private String symbol;

    private List<Map<Object, Object>> websites;
}
