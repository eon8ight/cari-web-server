package com.cari.web.server.repository;

import java.util.Optional;
import com.cari.web.server.domain.db.Entity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends CrudRepository<Entity, Integer> {

    // @formatter:off
    String FIND_BY_USERNAME_OR_EMAIL_ADDRESS_QUERY =
        "select * " +
        "  from tb_entity " +
        " where username      = :usernameOrEmailAddress " +
        "    or email_address = :usernameOrEmailAddress";

    String FIND_BY_USERNAME_QUERY =
        "select * " +
        "  from tb_entity " +
        " where username = :username";

    String FIND_BY_EMAIL_ADDRESS_QUERY =
        "select * " +
        "  from tb_entity " +
        " where email_address = :emailAddress";
    // @formatter:on

    @Query(FIND_BY_USERNAME_OR_EMAIL_ADDRESS_QUERY)
    Optional<Entity> findByUsernameOrEmailAddress(
            @Param("usernameOrEmailAddress") String usernameOrEmailAddress);

    @Query(FIND_BY_USERNAME_QUERY)
    Optional<Entity> findByUsername(@Param("username") String username);

    @Query(FIND_BY_EMAIL_ADDRESS_QUERY)
    Optional<Entity> findByEmailAddress(@Param("emailAddress") String emailAddress);
}
