package com.marin.socialnetwork.repositories;


import com.marin.socialnetwork.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByNameIn(Set<String> names);

    @Query("SELECT t.name FROM Tag t WHERE t.name IN :names")
    Set<String> findNamesByNamesIn(@Param("names") List<String> names);
}
