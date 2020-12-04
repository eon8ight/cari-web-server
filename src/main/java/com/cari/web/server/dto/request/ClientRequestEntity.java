package com.cari.web.server.dto.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequestEntity implements Serializable {

    private static final long serialVersionUID = -1627232491882608148L;

    private String username;

    private String emailAddress;

    private String password;
}
