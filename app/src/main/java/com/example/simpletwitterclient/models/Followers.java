package com.example.simpletwitterclient.models;

import com.google.gson.annotations.SerializedName;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

/**
 * Followers
 */

public class Followers{
    @SerializedName("users")
    public final List<User> users;


    @SerializedName("next_cursor")
    public final long nextCursor;



    public Followers(List<User> users, long nextCursor) {
        this.users = users;
        this.nextCursor = nextCursor;
    }

}

