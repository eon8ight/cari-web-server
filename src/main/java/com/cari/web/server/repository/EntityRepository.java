package com.cari.web.server.repository;

import java.util.Optional;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.util.db.EntityWithJoinDataMapper;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends CrudRepository<Entity, Integer> {

    String FIND_BY_USERNAME_OR_EMAIL_ADDRESS_QUERY = """
              select e.*,
                     jsonb_agg(distinct r.*) as roles
                from tb_entity e
                join tb_entity_role er
                  on e.entity = er.entity
                join tb_role r
                  on er.role = r.role
               where e.username      = :usernameOrEmailAddress
                  or e.email_address = :usernameOrEmailAddress
                 and e.entity <> 0
            group by e.entity""";

    String FIND_BY_USERNAME_QUERY = """
              select e.*,
                     jsonb_agg(distinct r.*) as roles
                from tb_entity e
                join tb_entity_role er
                  on e.entity = er.entity
                join tb_role r
                  on er.role = r.role
               where e.username = :username
                 and e.entity <> 0
            group by e.entity""";

    String FIND_BY_EMAIL_ADDRESS_QUERY = """
              select e.*,
                     jsonb_agg(distinct r.*) as roles
                from tb_entity e
                join tb_entity_role er
                  on e.entity = er.entity
                join tb_role r
                  on er.role = r.role
               where lower(e.email_address) = lower(:emailAddress)
                 and e.entity <> 0
            group by e.entity""";

    String FIND_BY_PK_QUERY = """
               select e.*,
                      to_jsonb(f.*)           as profile_image,
                      jsonb_agg(distinct r.*) as roles
                 from tb_entity e
                 join tb_entity_role er
                   on e.entity = er.entity
            left join tb_file f
                   on e.profile_image_file = f.file
                 join tb_role r
                   on er.role = r.role
                where e.entity = :entity
                  and e.entity <> 0
             group by e.entity,
                      f.file""";

    @Query(value = FIND_BY_USERNAME_OR_EMAIL_ADDRESS_QUERY,
            rowMapperClass = EntityWithJoinDataMapper.class)
    Optional<Entity> findByUsernameOrEmailAddress(
            @Param("usernameOrEmailAddress") String usernameOrEmailAddress);

    @Query(value = FIND_BY_USERNAME_QUERY, rowMapperClass = EntityWithJoinDataMapper.class)
    Optional<Entity> findByUsername(@Param("username") String username);

    @Query(value = FIND_BY_EMAIL_ADDRESS_QUERY, rowMapperClass = EntityWithJoinDataMapper.class)
    Optional<Entity> findByEmailAddress(@Param("emailAddress") String emailAddress);

    @Query(value = FIND_BY_PK_QUERY, rowMapperClass = EntityWithJoinDataMapper.class)
    Optional<Entity> findByPk(@Param("entity") int entity);
}
