package com.cari.web.server.dto.request;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.cari.web.server.domain.SimilarAesthetic;
import com.cari.web.server.domain.db.AestheticWebsite;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AestheticEditRequest implements Serializable {

    private static final long serialVersionUID = -7739431384984288058L;

    private Integer aesthetic;

    private String description;

    private Integer endEra;

    private String mediaSourceUrl;

    private String name;

    private Integer startEra;

    private String symbol;

    private Integer displayImageFile;

    private Boolean isDraft;

    private MultipartFile displayImage;

    private List<Map<Object, Object>> websites;

    private List<Map<Object, Object>> similarAesthetics;

    private List<Map<Object, Object>> media;

    public List<AestheticWebsite> getWebsiteObjects() {
        if (websites == null) {
            return Collections.emptyList();
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return websites.stream().map(map -> {
            AestheticWebsite website = objectMapper.convertValue(map, AestheticWebsite.class);
            website.setAesthetic(aesthetic);
            return website;
        }).collect(Collectors.toList());
    }

    public List<SimilarAesthetic> getSimilarAestheticObjects() {
        if (similarAesthetics == null) {
            return Collections.emptyList();
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return similarAesthetics.stream()
                .map(map -> objectMapper.convertValue(map, SimilarAesthetic.class))
                .collect(Collectors.toList());
    }

    public AestheticMediaEditRequest getMediaObjects() {
        return AestheticMediaEditRequest.builder().aesthetic(aesthetic).media(media).build();
    }
}
