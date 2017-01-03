package com.example.simpletwitterclient.commons;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;

import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;

import io.fabric.sdk.android.Fabric;

import static com.example.simpletwitterclient.commons.Constants.TWITTER_KEY;
import static com.example.simpletwitterclient.commons.Constants.TWITTER_SECRET;

/**
 * Created by thema on 1/3/2017.
 */

public class App extends Application {
    public static App app;
    public static TwitterSession twitterSession;
    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        app = this;
        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        App.twitterSession = getUserData();
    }

    public static void debug(String message) {
        Log.d("MY TAG", message);
    }


    public static void saveUserData(TwitterSession session) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(session);
        App.debug("Session json :" + json);
        editor.putString("UserData", json);
        editor.apply();
    }

    private static TwitterSession getUserData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserData", "");
        App.debug("Session json :" + json);
        TwitterSession session = gson.fromJson(json, TwitterSession.class);
        return session;
    }

}
