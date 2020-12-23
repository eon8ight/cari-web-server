package com.cari.web.server.repository;

import com.cari.web.server.domain.db.EntityRole;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRoleRepository extends CrudRepository<EntityRole, Integer> {

    // @formatter:off
    String CREATE_QUERY =
        "insert into tb_entity_role ( " +
        "  entity, " +
        "  role " +
        ") values ( " +
        "  :entity, " +
        "  :role " +
        ")";
    // @formatter:on

    @Modifying
    @Query(value = CREATE_QUERY)
    void create(@Param("entity") int entity, @Param("role") int role);

    default void create(EntityRole entityRole) {
        create(entityRole.getEntity(), entityRole.getRole());
    }
}
