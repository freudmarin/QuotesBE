package com.marin.quotesdashboardbackend.retrofit;

import com.marin.quotesdashboardbackend.dtos.api.TagDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface Tags {

    @GET("/api/tags")
    Call<List<TagDTO>> listTags(
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder
    );
}
