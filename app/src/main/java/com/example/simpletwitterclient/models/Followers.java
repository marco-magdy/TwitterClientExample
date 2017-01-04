package com.example.simpletwitterclient.models;

import com.google.gson.annotations.SerializedName;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

/**
 * Created by thema on 1/3/2017.
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

