package com.cari.web.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_media_creator")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaCreator {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer mediaCreator;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
