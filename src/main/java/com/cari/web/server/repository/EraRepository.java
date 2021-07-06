package com.cari.web.server.repository;

import java.util.List;
import com.cari.web.server.domain.db.Era;
import com.cari.web.server.util.db.EraWithJoinDataMapper;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EraRepository extends PagingAndSortingRepository<Era, Integer> {

    String FIND_ALL_WITH_ERA_SPECIFIER_QUERY = """
              select e.*,
                     to_jsonb(es.*) as specifier
                from tb_era e
                join tb_era_specifier es
                  on e.era_specifier = es.era_specifier
            order by e.year,
                  e.era_specifier""";

    @Query(value = FIND_ALL_WITH_ERA_SPECIFIER_QUERY, rowMapperClass = EraWithJoinDataMapper.class)
    List<Era> findAllWithEraSpecifier();
}
