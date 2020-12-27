package com.cari.web.server.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.cari.web.server.domain.db.CreatableTable;
import com.cari.web.server.domain.db.EditableAestheticAttachment;
import org.springframework.data.repository.CrudRepository;

public interface EditableAestheticAttachmentRepository<T extends CreatableTable & EditableAestheticAttachment>
        extends CrudRepository<T, Integer> {

    default List<T> createOrUpdateForAesthetic(int pkAesthetic, List<T> objects,
            Function<Integer, List<T>> findByAesthetic, BiConsumer<Integer, T> aestheticAssigner,
            BiConsumer<T, T> primaryKeyCopier) {
        List<T> existing = findByAesthetic.apply(pkAesthetic);

        Map<Integer, T> payloadToPk = existing.stream().collect(Collectors.toMap(
                t -> ((T) t).alternateKeyHash(), Function.identity()));

        List<T> toUpdate = new ArrayList<>();
        List<T> toIgnore = new ArrayList<>();

        objects.forEach(object -> {
            aestheticAssigner.accept(pkAesthetic, object);
            T equivalent = payloadToPk.get(object.alternateKeyHash());

            if (equivalent == null) {
                toUpdate.add(object);
            } else {
                primaryKeyCopier.accept(equivalent, object);

                if (equivalent.equals(object)) {
                    toIgnore.add(object);
                } else {
                    object.setCreator(equivalent.getCreator());
                    object.setCreated(equivalent.getCreated());
                    toUpdate.add(object);
                }
            }
        });

        List<T> rval = new ArrayList<>(toIgnore);

        if (!toUpdate.isEmpty()) {
            Iterable<T> saved = saveAll(toUpdate);

            List<T> savedList =
                    StreamSupport.stream(saved.spliterator(), true).collect(Collectors.toList());

            rval.addAll(savedList);
        }

        return rval;
    }
}
