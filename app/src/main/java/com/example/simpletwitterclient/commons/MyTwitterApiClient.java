package com.example.simpletwitterclient.commons;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;


/**
 * Created by thema on 1/1/2017.
 */

public class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }

    public FollowersCustomService getFollowersCustomService() {
        return getService(FollowersCustomService.class);
    }


}