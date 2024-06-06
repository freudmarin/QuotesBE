package com.marin.socialnetwork.repositories;

import com.marin.socialnetwork.entities.Author;
import com.marin.socialnetwork.entities.Quote;
import com.marin.socialnetwork.entities.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class QuoteRepositoryCustomImpl implements QuoteRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Quote> searchQuotes(String text, String author, List<String> tags) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Quote> query = cb.createQuery(Quote.class);
        Root<Quote> quote = query.from(Quote.class);

        // Join authors and tags
        Join<Quote, Author> authorJoin = quote.join("author", JoinType.LEFT);
        SetJoin<Quote, Tag> tagJoin = quote.joinSet("tags", JoinType.LEFT);

        // Build predicates
        List<Predicate> predicates = new ArrayList<>();

        if (text != null) {
            predicates.add(cb.like(quote.get("text"), "%" + text + "%"));
        }
        if (author != null) {
            predicates.add(cb.like(authorJoin.get("name"), "%" + author + "%"));
        }
        if (tags != null && !tags.isEmpty()) {
            predicates.add(tagJoin.get("name").in(tags));
        }

        query.select(quote).distinct(true).where(cb.or(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }
}
