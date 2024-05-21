package com.marin.quotesdashboardbackend.repositories;

import com.marin.quotesdashboardbackend.entities.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    @Query("SELECT q.text FROM Quote q WHERE q.text IN :texts")
    Set<String> findTextsByContent(@Param("texts") List<String> texts);

    @Query("SELECT q FROM Quote q JOIN q.tags t WHERE t.name IN :tagNames GROUP BY q.id")
    List<Quote> findByTagNames(@Param("tagNames") List<String> tagNames);
}
