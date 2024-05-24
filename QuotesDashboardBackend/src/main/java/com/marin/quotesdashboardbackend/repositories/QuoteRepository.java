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


    @Query("SELECT q FROM Quote q JOIN FETCH q.author JOIN FETCH q.tags")
    List<Quote> findAllWithTagsAndAuthors();


    @Query(value = "SELECT q.id as quoteId, q.text as quoteText, " +
            "a.id as authorId, a.name as authorName, " +
            "t.id as tagId, t.name as tagName " +
            "FROM quotes q " +
            "LEFT JOIN authors a ON q.author_id = a.id " +
            "LEFT JOIN quote_tag qt ON q.id = qt.quote_id " +
            "LEFT JOIN tags t ON qt.tag_id = t.id " +
            "WHERE (:text IS NULL OR q.text LIKE :text) " +
            "OR (:author IS NULL OR a.name LIKE :author) " +
            "OR (:tags IS NULL OR t.name IN (:tags))",
            nativeQuery = true)
    List<Object[]> findQuotes(@Param("text") String text,
                              @Param("author") String author,
                              @Param("tags") List<String> tags);
}
