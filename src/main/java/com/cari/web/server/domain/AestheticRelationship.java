package com.cari.web.server.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_aesthetic_relationship",
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_aesthetic", "to_aesthetic"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AestheticRelationship implements Serializable {

    private static final long serialVersionUID = -3625374001558766927L;

    @Id
    @Column(name = "aesthetic_relationship", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer aestheticRelationship;

    @JoinColumn(name = "from_aesthetic", nullable = false)
    @OneToOne
    private Aesthetic fromAesthetic;

    @JoinColumn(name = "to_aesthetic", nullable = false)
    @OneToOne
    private Aesthetic toAesthetic;

    @Column(name = "description")
    private String description;
}
