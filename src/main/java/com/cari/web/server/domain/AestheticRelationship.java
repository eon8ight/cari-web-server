package com.cari.web.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_aesthetic_relationship",
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_aesthetic", "to_aesthetic"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AestheticRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer aestheticRelationship;

    @JoinColumn(name = "from_aesthetic", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Aesthetic fromAesthetic;

    @JoinColumn(name = "to_aesthetic", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Aesthetic toAesthetic;

    @Column(name = "description")
    private String description;
}
