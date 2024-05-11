package com.marin.quotesdashboardbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
    @Id
    private String id;
    private String name;
    private String slug;

    @Column(nullable = true)
    private int quoteCount;

    @Column(nullable = true)
    private LocalDateTime dateAdded;

    @Column(nullable = true)
    private LocalDateTime dateModified;

    @ManyToMany(mappedBy = "tags")
    private Set<Quote> quotes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return id != null && id.equals(tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
