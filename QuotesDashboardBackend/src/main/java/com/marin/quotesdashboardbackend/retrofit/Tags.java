package com.marin.quotesdashboardbackend.retrofit;

import com.marin.quotesdashboardbackend.dtos.TagDTO;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.Call;

import java.util.List;

public interface Tags {

    @GET("/api/tags")
    Call<List<TagDTO>> listTags(
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder
    );
}
