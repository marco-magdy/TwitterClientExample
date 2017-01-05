package com.example.simpletwitterclient.commons;


import com.example.simpletwitterclient.models.Followers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * FollowersCustomService
 * Custom Interface for getting users followers
 */

public interface FollowersCustomService {
    @GET("/1.1/followers/list.json")
    Call<Followers> show(
            @Query("user_id") Long userId,
            @Query("screen_name") String var,
            @Query("cursor") long cursor,
            @Query("skip_status") Boolean var1,
            @Query("include_user_entities") Boolean var2,
            @Query("count") Integer var3);
}

