package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.AuthorDTO;
import com.marin.quotesdashboardbackend.dtos.AuthorPageDTO;
import com.marin.quotesdashboardbackend.entities.Author;
import com.marin.quotesdashboardbackend.repositories.AuthorRepository;
import com.marin.quotesdashboardbackend.retrofit.Authors;
import com.marin.quotesdashboardbackend.retrofit.RetrofitClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthorService {

    private final Authors apiService;
    private static final String serviceBaseUrl = "http://localhost:8081";
    private final AuthorRepository authorRepository;
    public AuthorService(AuthorRepository authorRepository) {
        this.apiService = RetrofitClient.getClient(serviceBaseUrl).create(Authors.class);
        this.authorRepository = authorRepository;
    }

    public void syncAuthors() {
        int page = 1;
        int size = 100;
        while (true) {
            Call<AuthorPageDTO> call = apiService.listAuthors(page, size, "asc", "name", null);
            try {
                Response<AuthorPageDTO> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<AuthorDTO> authors = response.body().getResults();
                    if (authors.isEmpty()) {
                        break; // Exit loop if no more authors
                    }
                    Set<Author> newAuthors = authors.stream()
                            .map(dto -> new Author(dto.getName(), dto.getBio(), dto.getDescription(), dto.getLink()))
                            .collect(Collectors.toSet());

                    Set<String> existingAuthorNames = new HashSet<>(authorRepository.findNamesByNamesIn(newAuthors.stream()
                            .map(Author::getName)
                            .collect(Collectors.toList())));

                    List<Author> authorsToAdd = newAuthors.stream()
                            .filter(author -> !existingAuthorNames.contains(author.getName()))
                            .collect(Collectors.toList());

                    authorRepository.saveAll(authorsToAdd);
                    page++;
                } else {
                    log.error("Failed to fetch authors. Status: {}, Message: {}", response.code(), response.message());
                    break;
                }
            } catch (IOException e) {
                log.error("Error fetching authors: {}", e.getMessage(), e);
                break;
            }
        }
    }
}
