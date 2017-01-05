package com.example.simpletwitterclient.commons;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;


/**
 * MyTwitterApiClient
 * Custom TwitterApiClient for getting users followers
 */

public class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }

    public FollowersCustomService getFollowersCustomService() {
        return getService(FollowersCustomService.class);
    }


}