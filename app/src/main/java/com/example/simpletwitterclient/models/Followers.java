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

    @SerializedName("previous_cursor")
    public final long previousCursor;

    @SerializedName("previous_cursor_str")
    public final String previousCursorStr;

    @SerializedName("next_cursor")
    public final long nextCursor;

    @SerializedName("next_cursor_str")
    public final String nextCursorStr;


    public Followers(List<User> users, long previousCursor, String previousCursorStr, long nextCursor, String nextCursorStr) {
        this.users = users;
        this.previousCursor = previousCursor;
        this.previousCursorStr = previousCursorStr;
        this.nextCursor = nextCursor;
        this.nextCursorStr = nextCursorStr;
    }

}

