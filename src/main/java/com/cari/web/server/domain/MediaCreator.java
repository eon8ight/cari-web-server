package com.cari.web.server.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_media_creator")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaCreator implements Serializable {

    private static final long serialVersionUID = -2739822601756198464L;

    private static final String COLUMN_MEDIA_CREATOR = "media_creator";

    @Id
    @Column(name = COLUMN_MEDIA_CREATOR, nullable = false, unique = true)
    @JsonAlias({ COLUMN_MEDIA_CREATOR })
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer mediaCreator;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
