package com.example.simpletwitterclient.commons;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.simpletwitterclient.models.Followers;
import com.google.gson.reflect.TypeToken;

import com.example.simpletwitterclient.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

import static com.example.simpletwitterclient.commons.Constants.TWITTER_KEY;
import static com.example.simpletwitterclient.commons.Constants.TWITTER_SECRET;

/**
 * Created by thema on 1/3/2017.
 */

public class App extends Application {
    public static App app;
    public static TwitterSession twitterSession;
    public static ArrayList<TwitterSession> usersAccounts;
    private static SharedPreferences sharedPreferences;
    public static ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        app = this;
        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        App.twitterSession = getUserData();

        setUpTheImageLoader();
    }

    public static void debug(String message) {
        Log.d("MY TAG", message);
    }

    public static void toast(String message) {
        Toast.makeText(app, message, Toast.LENGTH_SHORT).show();
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

    public static void saveAccounts(ArrayList<TwitterSession> sessions) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(sessions);
        App.debug("Session json :" + json);
        editor.putString("UserAccounts", json);
        editor.apply();
    }

    public static ArrayList<TwitterSession> getAccounts() {
        ArrayList<TwitterSession> sessions = null;
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserAccounts", "");
        App.debug("Session json :" + json);
        Type type = new TypeToken<ArrayList<TwitterSession>>() {
        }.getType();
        sessions = gson.fromJson(json, type);
        return sessions;
    }

    public static void saveFollowers(ArrayList<User> followers) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(followers);
        App.debug("Session json :" + json);
        editor.putString("Followers", json);
        editor.apply();
    }

    public static ArrayList<User> getFollowers() {
        ArrayList<User> followers = null;
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Followers", "");
        App.debug("Session json :" + json);
        Type type = new TypeToken<ArrayList<User>>() {
        }.getType();
        followers = gson.fromJson(json, type);
        return followers;
    }

    private void setUpTheImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(20)) //rounded corner bitmap
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.grey_loading_image)
                .build();
        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(this).diskCacheExtraOptions(480, 800, null)
                        .diskCacheSize(30 * 1024 * 1024)
                        .diskCacheFileCount(1000)
                        .defaultDisplayImageOptions(options)
                        .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    public static void logOut() {
        // sharedPreferences.edit().clear().apply();
        App.saveUserData(null);
        App.twitterSession = null;

    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
