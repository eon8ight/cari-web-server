package com.cari.web.server.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ArenaContent implements Serializable {
    private static final long serialVersionUID = -1100385390369922189L;

    private int id;

    private String title;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private String state;

    @JsonProperty("comment_count")
    private int commentCount;

    @JsonProperty("generated_title")
    private String generatedTitle;

    @JsonProperty("content_html")
    private String contentHtml;

    @JsonProperty("description_html")
    private String descriptionHtml;

    private String visibility;

    private String content;

    private String description;

    private ArenaImage image;

    private String metadata;

    @JsonProperty("base_class")
    private String baseClass;

    @JsonProperty("class")
    private String clazz;

    private int position;

    private boolean selected;

    @JsonProperty("connection_id")
    private int connectionId;

    @JsonProperty("connected_at")
    private LocalDateTime connectedAt;

    @JsonProperty("connected_by_user_id")
    private int connectedByUserId;

    @JsonProperty("connected_by_username")
    private String connectedByUsername;

    @JsonProperty("connected_by_user_slug")
    private String connectedByUserSlug;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ArenaImage implements Serializable {

        private static final long serialVersionUID = 1929060722366533676L;

        private String filename;

        @JsonProperty("content_type")
        private String contentType;

        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;

        private ArenaImageScale thumb;

        private ArenaImageScale square;

        private ArenaImageScale display;

        private ArenaImageScale large;

        private ArenaImageOriginalScale original;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        private static class ArenaImageScale implements Serializable {

            private static final long serialVersionUID = 5591895042955700664L;

            private String url;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        private static class ArenaImageOriginalScale extends ArenaImageScale {

            private static final long serialVersionUID = 77895881789592999L;

            @JsonProperty("file_size")
            private int fileSize;

            @JsonProperty("file_size_display")
            private String fileSizeDisplay;
        }
    }
}
