package com.cari.web.server.dto.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequestToken implements Serializable {

    private static final long serialVersionUID = 6077013706409821068L;

    private String token;
}
