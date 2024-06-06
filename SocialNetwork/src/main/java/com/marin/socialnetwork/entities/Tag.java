package com.marin.socialnetwork.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String slug;

    private int quoteCount;

    private LocalDate dateAdded;

    private LocalDate dateModified;

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

    public Tag(String name, String slug, int quoteCount, LocalDate dateAdded, LocalDate dateModified) {
        this.name = name;
        this.slug = slug;
        this.quoteCount = quoteCount;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
    }
}
