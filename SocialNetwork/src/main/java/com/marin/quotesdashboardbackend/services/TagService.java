package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.api.TagDTO;
import com.marin.quotesdashboardbackend.entities.Tag;
import com.marin.quotesdashboardbackend.repositories.TagRepository;
import com.marin.quotesdashboardbackend.retrofit.RetrofitClient;
import com.marin.quotesdashboardbackend.retrofit.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TagService {

    private final Tags apiService;
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
        this.apiService = RetrofitClient.getClient().create(Tags.class);
    }

    public void syncTags() {
        Call<List<TagDTO>> call = apiService.listTags("name", "asc");
        try {
            Response<List<TagDTO>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                List<TagDTO> tags = response.body();
                if (tags.isEmpty()) {
                    return;
                }
                Set<Tag> newTags = tags.stream()
                        .map(dto -> new Tag(dto.getName(), dto.getSlug(), dto.getQuoteCount(),
                                LocalDate.parse(dto.getDateAdded(), DateTimeFormatter.ISO_LOCAL_DATE),
                                LocalDate.parse(dto.getDateModified(), DateTimeFormatter.ISO_LOCAL_DATE)))
                        .collect(Collectors.toSet());

                Set<String> existingTags = new HashSet<>(tagRepository.findNamesByNamesIn(newTags.stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList())));

                List<Tag> tagsToAdd = newTags.stream()
                        .filter(tag -> !existingTags.contains(tag.getName()))
                        .toList();

                tagRepository.saveAll(tagsToAdd);
            } else {
                log.error("Failed to fetch tags. Status: {}, Message: {}", response.code(), response.message());
            }
        } catch (IOException e) {
            log.error("Error fetching tags: {}", e.getMessage(), e);
        }
    }
}
