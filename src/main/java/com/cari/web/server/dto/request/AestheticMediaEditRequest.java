package com.cari.web.server.dto.request;

import java.io.Serializable;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AestheticMediaEditRequest implements Serializable {

    private static final long serialVersionUID = 2611482984847835691L;

    private Integer mediaCreator;

    private String mediaCreatorName;

    private String description;

    private String label;

    private int year;

    private Integer mediaFile;

    private MultipartFile fileObject;

    public static AestheticMediaEditRequest fromMap(Map<Object, Object> map) {
        String mediaCreatorStr = (String) map.get("mediaCreator");
        Integer mediaCreator = mediaCreatorStr == null ? null : Integer.parseInt(mediaCreatorStr);

        String mediaFileStr = (String) map.get("mediaFile");
        Integer mediaFile = mediaFileStr == null ? null : Integer.parseInt(mediaFileStr);

        // @formatter:off
        return AestheticMediaEditRequest.builder()
            .mediaCreator(mediaCreator)
            .mediaCreatorName((String) map.get("mediaCreatorName"))
            .description((String) map.get("description"))
            .label((String) map.get("label"))
            .year(Integer.parseInt((String) map.get("year")))
            .mediaFile(mediaFile)
            .fileObject((MultipartFile) map.get("fileObject"))
            .build();
        // @formatter:on
    }
}
