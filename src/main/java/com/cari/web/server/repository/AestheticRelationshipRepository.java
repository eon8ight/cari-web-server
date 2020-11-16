package com.cari.web.server.repository;

import com.cari.web.server.domain.AestheticRelationship;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AestheticRelationshipRepository
        extends CrudRepository<AestheticRelationship, Integer> {

    // @formatter:off
    String CREATE_OR_UPDATE_QUERY =
        "insert into tb_aesthetic_relationship ( " +
        "  from_aesthetic, " +
        "  to_aesthetic, " +
        "  description " +
        ") values ( " +
        "  :fromAesthetic, " +
        "  :toAesthetic, " +
        "  :description " +
        "), ( " +
        "  :toAesthetic, " +
        "  :fromAesthetic, " +
        "  :reverseDescription " +
        ") " +
        " on conflict ( from_aesthetic, to_aesthetic ) " +
        " do update " +
        "set description = EXCLUDED.description";
    // @formatter:on

    @Modifying
    @Query(CREATE_OR_UPDATE_QUERY)
    void createOrUpdate(@Param("fromAesthetic") int fromAesthetic,
            @Param("toAesthetic") int toAesthetic, @Param("description") String description,
            @Param("reverseDescription") String reverseDescription);
}
