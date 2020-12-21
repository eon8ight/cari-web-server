package com.cari.web.server.dto.response.arena;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
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

    private ArenaSource source;

    private ArenaImage image;

    private ArenaEmbed embed;

    private ArenaAttachment attachment;

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
}
