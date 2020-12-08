package com.cari.web.server.repository;

import java.util.Optional;
import com.cari.web.server.domain.db.MessageTemplate;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageTemplateRepository extends CrudRepository<MessageTemplate, Integer> {

    // @formatter:off
    String FIND_BY_LABEL_QUERY =
        "select * " +
        "  from tb_message_template " +
        " where label = :label";
    // @formatter:on

    @Query(FIND_BY_LABEL_QUERY)
    Optional<MessageTemplate> findByLabel(@Param("label") String label);
}
