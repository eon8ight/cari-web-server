package com.cari.web.server.repository;

import java.util.List;
import java.util.function.BiConsumer;
import com.cari.web.server.domain.db.AestheticRelationship;
import com.cari.web.server.dto.DatabaseUpsertResult;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AestheticRelationshipRepository
        extends EditableAestheticAttachmentRepository<AestheticRelationship> {

    String DELETE_BY_AESTHETIC_EXCEPT_QUERY = """
            delete from tb_aesthetic_relationship
                  where (
                             from_aesthetic = :aesthetic
                          or to_aesthetic   = :aesthetic
                        )
                    and aesthetic_relationship not in ( :excludedAestheticRelationships )""";

    String DELETE_BY_AESTHETIC_QUERY = """
            delete from tb_aesthetic_relationship
                  where from_aesthetic = :aesthetic
                     or to_aesthetic   = :aesthetic""";

    String FIND_BY_AESTHETIC_QUERY = """
            select *
              from tb_aesthetic_relationship
             where from_aesthetic = :aesthetic
                or to_aesthetic   = :aesthetic""";

    @Modifying
    @Query(DELETE_BY_AESTHETIC_EXCEPT_QUERY)
    int deleteByAestheticExcept(@Param("aesthetic") int aesthetic,
            @Param("excludedAestheticRelationships") List<Integer> excludedAestheticRelationships);

    @Modifying
    @Query(DELETE_BY_AESTHETIC_QUERY)
    int deleteByAesthetic(@Param("aesthetic") int aesthetic);

    @Query(FIND_BY_AESTHETIC_QUERY)
    List<AestheticRelationship> findByAesthetic(@Param("aesthetic") int aesthetic);

    default DatabaseUpsertResult<AestheticRelationship> createOrUpdateForAesthetic(int pkAesthetic,
            List<AestheticRelationship> aestheticRelationships) {
        BiConsumer<Integer, AestheticRelationship> relationshipAestheticAssigner = (pk, r) -> {
            if (r.getFromAesthetic() == null) {
                r.setFromAesthetic(pk);
            } else {
                r.setToAesthetic(pk);
            }
        };

        return createOrUpdateForAesthetic(pkAesthetic, aestheticRelationships,
                this::findByAesthetic, relationshipAestheticAssigner,
                (from, to) -> to.setAestheticRelationship(from.getAestheticRelationship()));
    }
}
