package com.marin.socialnetwork.retrofit;

import com.marin.socialnetwork.dtos.api.QuoteDTO;
import com.marin.socialnetwork.dtos.api.SearchQuoteResponseDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface Quotes {

    @GET("/api/quotes/random")
    Call<List<QuoteDTO>> getRandomQuotes(@Query("limit") int limit);

    @GET("/api/quotes")
    Call<List<QuoteDTO>> getQuotes(@Query("maxLength") Integer maxLength,
                                   @Query("minLength") Integer minLength,
                                   @Query("tags") String tags,
                                   @Query("author") String author,
                                   @Query("sortBy") String sortBy,
                                   @Query("order") String order,
                                   @Query("limit") int limit,
                                   @Query("page") int page);

    @GET("/api/quotes/search")
    Call<SearchQuoteResponseDTO> searchQuotes(@Query("query") String query,
                                              @Query("fields") String fields,
                                              @Query("limit") int limit,
                                              @Query("skip") int skip);
}
