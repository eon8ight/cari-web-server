package com.cari.web.server.repository;

import java.util.List;
import com.cari.web.server.domain.db.Route;
import com.cari.web.server.util.db.RouteWithJoinDataMapper;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface RouteRepository extends CrudRepository<Route, Integer> {

    String FIND_ALL_QUERY = """
              select ru.*,
                     hm.label as http_method_label,
                     array_agg( rr.role ) as roles
                from tb_role_route rr
                join tb_route ru
                  on rr.route = ru.route
            join tb_http_method hm
                  on ru.http_method = hm.http_method
            group by ru.route,
                     hm.label""";

    @Query(value = FIND_ALL_QUERY, rowMapperClass = RouteWithJoinDataMapper.class)
    List<Route> findAll();
}
