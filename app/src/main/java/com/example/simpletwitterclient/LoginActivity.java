package com.example.simpletwitterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.simpletwitterclient.commons.App;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

/**
 * Created by thema on 1/3/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIfUserLoggedIn();
        setContentView(R.layout.activity_login);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                TwitterSession session = result.data;

                App.debug("User Id:   " + session.getUserId());
                App.debug("User Name: " + session.getUserName());

                Twitter.getApiClient(session).getAccountService()
                        .verifyCredentials(true, false).enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> userResult) {
                        User user = userResult.data;
                        System.out.println(user.profileImageUrl + " " + user.email + "" + user.followersCount);
                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }
                });
                App.twitterSession = session;
                App.saveUserData(session);
                checkIfUserLoggedIn();
            }

            @Override
            public void failure(TwitterException exception) {
                App.debug("TwitterKit," + "Login with Twitter failure : " + exception);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    public void checkIfUserLoggedIn() {
        App.debug("checkIfUserLoggedIn" + App.twitterSession);
        if (App.twitterSession != null) {
            App.debug("User ID:" + App.twitterSession.getUserId() + "\n" + "User Name:" + App.twitterSession.getUserName());
            startActivity(new Intent(this, FollowersListActivity.class));
            finish();
        }
    }
}
