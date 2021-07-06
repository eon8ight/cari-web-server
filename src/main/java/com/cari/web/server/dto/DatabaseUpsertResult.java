package com.cari.web.server.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseUpsertResult<T> {

    private List<T> upsertResultSet;

    boolean changed;
}
