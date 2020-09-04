package com.cari.web.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cari.web.server.domain.Aesthetic;

@Repository
public interface AestheticRepository extends JpaRepository<Aesthetic, Integer> {

    @Query("select a from Aesthetic a")
    List<Aesthetic> findForAestheticsList();

    @Query("select similarAesthetics from Aesthetic a")
    List<Aesthetic> findSimilarAesthetics(int aesthetic);

    @Query("select a from Aesthetic a where urlSlug = ?1")
    Aesthetic findByUrlSlug(String urlSlug);
}