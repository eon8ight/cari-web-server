package com.cari.web.server.dto.request;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AestheticMediaEditRequest implements Serializable {

    private static final long serialVersionUID = 5991842477325584211L;

    private int aesthetic;

    private List<Map<Object, Object>> media;

    @Transient
    private List<AestheticMediaEditData> mediaObjects;

    public List<AestheticMediaEditData> getMediaObjects() {
        if (mediaObjects == null) {
            mediaObjects = media == null ? Collections.emptyList()
                    : media.stream().map(map -> AestheticMediaEditData.fromMap(map))
                            .collect(Collectors.toList());
        }

        return mediaObjects;
    }
}
