package com.example.simpletwitterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.simpletwitterclient.adapters.FollowersAdapter;
import com.example.simpletwitterclient.commons.App;
import com.example.simpletwitterclient.commons.MyTwitterApiClient;
import com.example.simpletwitterclient.models.Followers;
import com.example.simpletwitterclient.views.SpacesItemDecoration;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * FollowerList Activity ..
 * contains a list of user followers ,pull to refresh
 * and endless scroll view for loading more users
 */

public class FollowersListActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FollowersAdapter adapter;

    private ArrayList<User> userList = new ArrayList<>();

    /**
     * cursor Causes the results to be broken into pages.
     * -1 is the first page
     */
    private long cursor = -1;

    //users count per page
    private int pageLimit = 10;

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_list);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(App.twitterSession.getUserName() + getString(R.string.followers));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SpacesItemDecoration(FollowersListActivity.this));
        //onScrollListener for loading more items on scroll down
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                    /*
                    * if cursor==0 that means that you already fetched all data
                    * reference: https://dev.twitter.com/overview/api/cursoring
                    * */
                    if (cursor != 0) {
                        if (loading) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = false;
                                App.debug("Last item reached!");
                                //Do pagination.. i.e. fetch new data
                                getFollowersListOnline();
                            }
                        }
                    }
                }
            }
        });
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cursor = -1;
                getFollowersListOnline();
            }
        });


        getFollowersListOnline();
    }


    /**
     * in this method we call twitter API "/1.1/followers/list.json" to get the followers list using Retrofit 2.0
     * <p>
     * Retrofit is a REST Client for Android and Java by Square.
     * It makes it relatively easy to retrieve and upload JSON (or other structured data)
     * via a REST based webservice.
     * <p>
     * Note: Twitter Kit uses Retrofit to convert interfaces into authenticated representations of our endpoints.
     * Any additional endpoints added through the extensible example need to adhere to the format required by Retrofit.
     * Reference: https://docs.fabric.io/android/twitter/access-rest-api.html
     * <p>
     * *
     */
    public void getFollowersListOnline() {

        Call<Followers> client
                = new MyTwitterApiClient(App.twitterSession)
                .getFollowersCustomService()
                .show(App.twitterSession.getUserId(), null, cursor, true, true, pageLimit);

        client.enqueue(new Callback<Followers>() {
            @Override
            public void success(Result<Followers> result) {
                progressBar.setVisibility(View.GONE);
                swipeContainer.setRefreshing(false);


                App.debug("Next Cursor :" + result.data.nextCursor);
                App.debug("Followers Count : " + result.data.users.size());

                if (cursor == -1) {//first time "userList is empty"
                    userList = new ArrayList<>(result.data.users);
                    adapter = new FollowersAdapter(FollowersListActivity.this, userList);
                    recyclerView.setAdapter(adapter);
                    clearOfflineUsers();
                } else {//add the new items to already existing ones
                    adapter.addItems(new ArrayList<>(result.data.users));
                }
                //set nextCursor value to cursor for the next page request
                cursor = result.data.nextCursor;
                saveOfflineUsers(result.data.users);

                //assign true to loading to enable scrolling down
                loading = true;
            }

            @Override
            public void failure(TwitterException exception) {
                progressBar.setVisibility(View.GONE);
                swipeContainer.setRefreshing(false);

                App.debug("FollowersLostActivity:exception : " + exception);

                if (App.isOnline()) {
                    App.toast(getString(R.string.too_many_requests));
                } else {
                    App.toast(getString(R.string.connection_error));
                    getFollowersListOffline();
                }
            }
        });
    }


    /**
     * save the list of followers for offline use
     * <p>
     * Note: we can also save the followers list using
     * light weight Database Management System such like:
     * (SQLite, OrmLite, SugarORM, etc...) with DAO design pattern or something
     * but "in this example" shared preferences is much more simple, straight forward
     * and doesn't need a lot of code
     *
     * @param users a list of users
     */
    private void saveOfflineUsers(List<User> users) {
        List<User> mUsers = App.getFollowersList();
        if (mUsers == null) {
            mUsers = new ArrayList<>();
        }
        mUsers.addAll(users);
        App.saveFollowersList(new ArrayList<>(mUsers));
    }

    /**
     * clear offline users data when there is a valid connection
     */
    private void clearOfflineUsers() {
        App.saveFollowersList(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        * get saved Accounts "for multiple users" from shared preferences
        * */
        ArrayList<TwitterSession> accounts = App.getAccounts();
        int accountsSize = accounts.size();
        App.debug("Number of accounts: " + accountsSize);

        //Add accounts to the overflow menu
        for (int i = 0; i < accountsSize; i++) {
            menu.add(0, i, i, "@" + accounts.get(i).getUserName());
        }

        //the last item of the menu would be "Log Out"
        menu.add(1, accountsSize + 1, accountsSize + 1, R.string.txt_log_out);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * get all user accounts from shared preferences
         * */
        ArrayList<TwitterSession> accounts = App.getAccounts();

        /**
         * get selected user account and save it as the "current account"
         * to use it further in authentication operations ,etc...
         *
         * we have to assign -1 to cursor,cursor is a param in "user followers list" API
         * a value of -1 indicates requesting the first page.
         * reference: https://dev.twitter.com/overview/api/cursoring
         *
         * we have also to assign null to the adapter to clear its data
         *
         * finally we are ready to call the followers list API
         * */
        if (item.getItemId() < accounts.size()) {
            TwitterSession twitterSession = accounts.get(item.getItemId());
            App.toast(twitterSession.getUserName() + getString(R.string.selected));
            App.saveUserData(twitterSession);
            App.twitterSession = twitterSession;
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(App.twitterSession.getUserName() + getString(R.string.followers));
            cursor = -1;
            adapter = null;
            progressBar.setVisibility(View.VISIBLE);
            getFollowersListOnline();
        } else {
            App.toast(getString(R.string.log_out));
            App.logOut();
            Intent intent = new Intent(FollowersListActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * this method gets all followers that already saved in the shared preferences
     * and display their data when the network is unavailable
     */
    private void getFollowersListOffline() {

        ArrayList<User> userArrayList = App.getFollowersList();
        App.debug("getFollowersListOffline : " + userArrayList);
        userList = new ArrayList<>(userArrayList);
        adapter = new FollowersAdapter(FollowersListActivity.this, userList);
        recyclerView.setAdapter(adapter);
    }


}