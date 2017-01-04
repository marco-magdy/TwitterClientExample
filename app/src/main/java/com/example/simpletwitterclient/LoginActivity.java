package com.example.simpletwitterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.simpletwitterclient.commons.App;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;

/**
 * Login Screen:
 * Contains a login button, when the user click this button
 * , a web page is opened and ask the user to grant authentication for the application,
 */
public class LoginActivity extends AppCompatActivity {
    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIfUserLoggedIn();
        setContentView(R.layout.activity_login);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!App.isOnline()) {
                    App.toast(getString(R.string.connection_error));
                }
            }
        });
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                TwitterSession session = result.data;

                App.debug("User Id:   " + session.getUserId());
                App.debug("User Name: " + session.getUserName());
                App.twitterSession = session;
                App.saveUserData(session);

                saveAccountInMultiUserAccounts();
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

    /***
     * Multiple User Accounts:
     * this method saves the login account and
     * adding this account to the list of accounts
     * so that the user can switch between his accounts later
     */
    private void saveAccountInMultiUserAccounts() {
        //1. get all saved accounts
        ArrayList<TwitterSession> accounts = App.getAccounts();
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        App.debug("Number of saved accounts : " + accounts.size());
        //2.check if the user already saved in the shared preferences "Settings"
        if (!userExists(App.twitterSession.getUserName(), accounts)) {
            //2.1 add the new account
            accounts.add(App.twitterSession);
            //2.2 save accounts
            App.saveAccounts(accounts);
        } else {
            App.toast(getString(R.string.user_already_saved));
        }
    }


    /**
     * this method checks if the user already saved in the shared preferences
     * @param userName
     * @param accounts
     * */
    private boolean userExists(String userName, ArrayList<TwitterSession> accounts) {
        for (TwitterSession account : accounts) {
            if (account.getUserName().contains(userName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks if the user already logged in
     * if true(if credentials already saved in the device settings),
     * the login activity dismissed automatically and "FollowerListActivity" is opened
     */
    private void checkIfUserLoggedIn() {
        App.debug("checkIfUserLoggedIn" + App.twitterSession);
        if (App.twitterSession != null) {
            App.debug("User ID:" + App.twitterSession.getUserId() + "\n" + "User Name:" + App.twitterSession.getUserName());
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }
    }
}
