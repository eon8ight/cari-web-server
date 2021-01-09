package com.cari.web.server.domain;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RouteWithRoles implements Serializable {

    private static final long serialVersionUID = 688570778521142856L;

    @JsonAlias("route_url")
    private String routeUrl;

    @JsonAlias("http_method_label")
    private String httpMethodLabel;

    private String[] roles;
}
