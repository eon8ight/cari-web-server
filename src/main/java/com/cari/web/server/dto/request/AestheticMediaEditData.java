package com.cari.web.server.dto.request;

import java.io.File;
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
public class AestheticMediaEditData implements Serializable {

    private static final long serialVersionUID = 2611482984847835691L;

    private Integer mediaCreator;

    private String mediaCreatorName;

    private String description;

    private String label;

    private int year;

    private Integer mediaFile;

    private Integer mediaThumbnailFile;

    private Integer mediaPreviewFile;

    private MultipartFile fileObject;

    private File copiedFileObject;

    public static AestheticMediaEditData fromMap(Map<Object, Object> map) {
        String mediaCreatorStr = (String) map.get("mediaCreator");
        Integer mediaCreator = mediaCreatorStr == null || mediaCreatorStr.isEmpty() ? null
                : Integer.parseInt(mediaCreatorStr);

        String mediaFileStr = (String) map.get("mediaFile");
        Integer file = mediaFileStr == null || mediaFileStr.isEmpty() ? null
                : Integer.parseInt(mediaFileStr);

        String mediaThumbnailFileStr = (String) map.get("mediaThumbnailFile");
        Integer thumbnailFile =
                mediaThumbnailFileStr == null || mediaThumbnailFileStr.isEmpty() ? null
                        : Integer.parseInt(mediaThumbnailFileStr);

        String mediaPreviewFileStr = (String) map.get("mediaPreviewFile");
        Integer previewFile = mediaPreviewFileStr == null || mediaPreviewFileStr.isEmpty() ? null
                : Integer.parseInt(mediaPreviewFileStr);

        // @formatter:off
        return AestheticMediaEditData.builder()
            .mediaCreator(mediaCreator)
            .mediaCreatorName((String) map.get("mediaCreatorName"))
            .description((String) map.get("description"))
            .label((String) map.get("label"))
            .year(Integer.parseInt((String) map.get("year")))
            .mediaFile(file)
            .mediaThumbnailFile(thumbnailFile)
            .mediaPreviewFile(previewFile)
            .fileObject((MultipartFile) map.get("fileObject"))
            .build();
        // @formatter:on
    }
}
