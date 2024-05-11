package com.marin.quotesdashboardbackend.repositories;

import com.marin.quotesdashboardbackend.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findAuthorByName(String name);
    @Query("SELECT a.name FROM Author a WHERE a.name IN :names")
    Set<String> findNamesByNamesIn(@Param("names") List<String> names);
}
