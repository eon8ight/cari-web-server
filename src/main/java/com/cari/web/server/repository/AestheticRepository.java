package com.cari.web.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cari.web.server.domain.Aesthetic;

@Repository
public interface AestheticRepository extends JpaRepository<Aesthetic, Integer> {

    @Query("select a from Aesthetic a where urlSlug = ?1")
    Aesthetic findByUrlSlug(String urlSlug);

    @Query("select name, urlSlug from Aesthetic")
    List<String[]> findAllNames();
}
