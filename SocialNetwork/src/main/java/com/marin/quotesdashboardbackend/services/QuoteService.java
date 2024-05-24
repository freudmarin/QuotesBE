package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.api.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.Author;
import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.entities.Tag;
import com.marin.quotesdashboardbackend.repositories.AuthorRepository;
import com.marin.quotesdashboardbackend.repositories.QuoteRepository;
import com.marin.quotesdashboardbackend.repositories.TagRepository;
import com.marin.quotesdashboardbackend.retrofit.Quotes;
import com.marin.quotesdashboardbackend.retrofit.RetrofitClient;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Slf4j
public class QuoteService {
    private final Quotes apiService;
    private final QuoteRepository quoteRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;
    private Quote quoteOfTheDay;

    public QuoteService(QuoteRepository quoteRepository, AuthorRepository authorRepository, TagRepository tagRepository) {
        this.authorRepository = authorRepository;
        this.apiService = RetrofitClient.getClient().create(Quotes.class);
        this.quoteRepository = quoteRepository;
        this.tagRepository = tagRepository;
    }

    public void syncQuotes() {
        try {
            Response<List<QuoteDTO>> response = apiService.getRandomQuotes(20).execute();
            if (response.isSuccessful()) {
                List<QuoteDTO> quotes = Optional.ofNullable(response.body()).orElse(Collections.emptyList());

                // Collect unique author names
                Set<String> authorNames = quotes.stream()
                        .map(QuoteDTO::getAuthor)
                        .collect(Collectors.toSet());

                Set<String> tagNamesForSearch = quotes.stream()  // Stream over List<QuoteDTO>
                        .map(QuoteDTO::getTags)  // Converts each QuoteDTO to List<Tag>
                        .filter(Objects::nonNull)  // Ensure no null list is processed
                        .flatMap(List::stream)  // Flatten the List<Tag> into Stream<Tag>
                        .collect(Collectors.toSet());

                // Fetch all authors in one go and map them by name
                Map<String, Author> authorMap = authorRepository.findAllByNameIn(authorNames)
                        .stream()
                        .collect(Collectors.toMap(Author::getName, author -> author));

                // Fetch all tags in one go and map them by name
                Map<String, Tag> tagMap = tagRepository.findAllByNameIn(tagNamesForSearch)
                        .stream()
                        .collect(Collectors.toMap(
                                Tag::getName,
                                Function.identity(),
                                (existing, replacement) -> existing
                        ));

                Set<String> existingTexts = quoteRepository.findTextsByContent(
                        quotes.stream().map(QuoteDTO::getContent).collect(Collectors.toList()));

                List<Quote> newQuotes = quotes.stream()
                        .filter(q -> !existingTexts.contains(q.getContent()))
                        .map(dto -> {
                            Quote quote = new Quote();
                            quote.setText(dto.getContent());
                            quote.setAuthor(authorMap.get(dto.getAuthor()));
                            if (dto.getTags() != null) {
                                Set<Tag> tags = dto.getTags().stream()
                                        .map(tagMap::get)  // Fetch each tag from the map
                                        .filter(Objects::nonNull) // Ensure no null tags are added
                                        .collect(Collectors.toSet());
                                quote.setTags(tags);
                                return quote;
                            }
                            return quote;
                        }).collect(Collectors.toList());

                quoteRepository.saveAll(newQuotes);
            } else {
                log.error("Failed to fetch quotes. Status: {}, Message: {}", response.code(), response.message());
            }
        } catch (IOException e) {
            log.error("Error fetching quotes: {}", e.getMessage(), e);
        }
    }

    public Quote findQuoteById(Long id) {
        return quoteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Quote not found"));
    }

    public List<com.marin.quotesdashboardbackend.dtos.QuoteDTO> getQuotesByTags(List<String> tagNames) {
        return quoteRepository.findByTagNames(tagNames).stream().map(DTOMappings::fromQuoteToQuoteDTO).collect(Collectors.toList());
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")  // Runs every day at midnight
    public com.marin.quotesdashboardbackend.dtos.QuoteDTO selectQuoteOfTheDay() {
        List<Quote> quotes = quoteRepository.findAll();
        if (!quotes.isEmpty()) {
            Random random = new Random();
            quoteOfTheDay = quotes.get(random.nextInt(quotes.size()));
        }

        return DTOMappings.fromQuoteToQuoteDTO(quoteOfTheDay);
    }

    public List<com.marin.quotesdashboardbackend.dtos.QuoteDTO> searchQuotes(String text, String author, List<String> tagNames) {
        List<Object[]> quotes = quoteRepository.findQuotes(text, author, tagNames);
        return quotes.stream()
                .map(this::mapResultToQuote).map(DTOMappings::fromQuoteToQuoteDTO)
                .toList();
    }

    private Quote mapResultToQuote(Object[] result) {
        Quote quote = new Quote();
        quote.setId((Long) result[0]);
        quote.setText((String) result[1]);

        Author authorDTO = new Author();
        authorDTO.setId((Long) result[2]);
        authorDTO.setName((String) result[3]);

        quote.setAuthor(authorDTO);

        Tag tag = new Tag();
        tag.setId((Long) result[4]);
        tag.setName((String) result[5]);

        quote.getTags().add(tag);

        return quote;
    }
}
