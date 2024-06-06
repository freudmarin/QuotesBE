package com.marin.socialnetwork.retrofit;

import com.marin.socialnetwork.dtos.api.AuthorPageDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Authors {
    @GET("/api/authors")
    Call<AuthorPageDTO> listAuthors(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("order") String order,
            @Query("sortBy") String sortBy,
            @Query("slug") String slug
    );
}
