package com.example.simpletwitterclient.commons;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import com.example.simpletwitterclient.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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
 * App is an application class that
 * contains frequently used "complex objects"
 * <p>
 * with "App" class, i can access the same complex object from any point of my application
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

    /**
     * print something on logcat with title  "My Tag"
     *
     * @param message the message body that will appear in the logcat
     */
    public static void debug(String message) {
        Log.d("MY TAG", message);
    }

    /**
     * Show a toast message
     *
     * @param message the message body
     */
    public static void toast(String message) {
        Toast.makeText(app, message, Toast.LENGTH_SHORT).show();
    }

//

    /**
     * save the current session of the user in the shared preferences
     * to use it in credentials stuff
     */
    public static void saveUserData(TwitterSession session) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(session);
        App.debug("Session json :" + json);
        editor.putString("UserData", json);
        editor.apply();
    }

    /**
     * get the current session of the user from the shared preferences
     * to use it in credentials stuff
     */
    private static TwitterSession getUserData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserData", "");
        App.debug("Session json :" + json);
        return gson.fromJson(json, TwitterSession.class);
    }

    /**
     * save (user accounts) in the shared preferences
     * so that the user can switch between multiple accounts
     *
     *@param sessions an array list of Twitter Sessions
     */
    public static void saveAccounts(ArrayList<TwitterSession> sessions) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(sessions);
        App.debug("Session json :" + json);
        editor.putString("UserAccounts", json);
        editor.apply();
    }

    /**
     * get (user accounts) from the shared preferences
     * so that the user can switch between multiple accounts
     */
    public static ArrayList<TwitterSession> getAccounts() {
        ArrayList<TwitterSession> sessions;
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserAccounts", "");
        App.debug("Session json :" + json);
        Type type = new TypeToken<ArrayList<TwitterSession>>() {
        }.getType();
        sessions = gson.fromJson(json, type);
        return sessions;
    }

    /**
     * save the followers of the user in the shared preferences
     * to enable the user view his followers when the connection is unavailable
     *
     * @param followers an array list of Twitter users
     */
    public static void saveFollowersList(ArrayList<User> followers) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(followers);
        App.debug("Session json :" + json);
        editor.putString("FollowersList", json);
        editor.apply();
    }

    /**
     * get the followers of the user
     */
    public static ArrayList<User> getFollowersList() {
        ArrayList<User> followers;
        Gson gson = new Gson();
        String json = sharedPreferences.getString("FollowersList", "");
        App.debug("Session json :" + json);
        Type type = new TypeToken<ArrayList<User>>() {
        }.getType();
        followers = gson.fromJson(json, type);
        return followers;
    }

    /**
     * save follower data for requesting his info from the API
     * @param user Twitter user
     */
    public static void saveFollowerData(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        App.debug("Follower json :" + json);
        editor.putString("FollowerData", json);
        editor.apply();
    }

    /**
     * get follower data for requesting his info from the API
     */
    public static User getFollowerData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("FollowerData", "");
        App.debug("Follower json :" + json);
        return gson.fromJson(json, User.class);
    }

    /**
     * setup universal image loader
     */
    private void setUpTheImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
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
        App.saveUserData(null);
        App.twitterSession = null;
    }

    /**
     * Checks network availability
     */
    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
