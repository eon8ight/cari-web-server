package com.cari.web.server.repository;

import java.util.List;
import com.cari.web.server.domain.db.AestheticRelationship;
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
        "    from_aesthetic, " +
        "    to_aesthetic, " +
        "    description " +
        ") values ( " +
        "    :fromAesthetic, " +
        "    :toAesthetic, " +
        "    :description " +
        "), ( " +
        "    :toAesthetic, " +
        "    :fromAesthetic, " +
        "    :reverseDescription " +
        ") " +
        "       on conflict ( from_aesthetic, to_aesthetic ) " +
        "       do update " +
        "      set description = EXCLUDED.description " +
        "returning aesthetic_relationship";

    String DELETE_BY_AESTHETIC_EXCEPT_QUERY =
        "delete from tb_aesthetic_relationship " +
        "      where ( " +
        "                 from_aesthetic = :aesthetic " +
        "              or to_aesthetic   = :aesthetic " +
        "            ) " +
        "        and aesthetic_relationship not in ( :excludedAestheticRelationships )";

    String DELETE_BY_AESTHETIC_QUERY =
        "delete from tb_aesthetic_relationship " +
        "      where from_aesthetic = :aesthetic " +
        "         or to_aesthetic   = :aesthetic";
    // @formatter:on

    @Query(CREATE_OR_UPDATE_QUERY)
    List<Integer> createOrUpdate(@Param("fromAesthetic") int fromAesthetic,
            @Param("toAesthetic") int toAesthetic, @Param("description") String description,
            @Param("reverseDescription") String reverseDescription);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_EXCEPT_QUERY)
    void deleteByAestheticExcept(@Param("aesthetic") int aesthetic,
            @Param("excludedAestheticRelationships") List<Integer> excludedAestheticRelationships);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_QUERY)
    void deleteByAesthetic(@Param("aesthetic") int aesthetic);
}
