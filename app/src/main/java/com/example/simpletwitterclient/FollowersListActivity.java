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
 * Created by thema on 1/3/2017.
 */

public class FollowersListActivity extends AppCompatActivity {

    private ArrayList<User> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private ProgressBar progressBar;
    private FollowersAdapter adapter;


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

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SpacesItemDecoration(FollowersListActivity.this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                    if (cursor != 0) {//if cursor==0 that means that you already fetched all data
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
                    clearOfflineUsers();
                    recyclerView.setAdapter(adapter);
                } else {//add the new items to already existing ones
                    adapter.addItems(new ArrayList<>(result.data.users));

                }
                cursor = result.data.nextCursor;
                saveOfflineUsers(result.data.users);

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
                    App.toast("ghgf");
                    getFollowersListOffline();
                }
            }
        });
    }


    private void saveOfflineUsers(List<User> users) {
        List<User> mUsers = App.getFollowersList();
        if (mUsers == null) {
            mUsers = new ArrayList<>();
        }
        mUsers.addAll(users);
        App.saveFollowersList(new ArrayList<>(mUsers));
    }

    private void clearOfflineUsers() {
        App.saveFollowersList(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        * get saved Accounts
        * */
        ArrayList<TwitterSession> accounts = App.getAccounts();
        int accountsSize = accounts.size();
        App.debug("Number of accounts: " + accountsSize);
        for (int i = 0; i < accountsSize; i++) {
            menu.add(0, i, i, "@" + accounts.get(i).getUserName());
        }
        menu.add(1, accountsSize + 1, accountsSize + 1, "Log out");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<TwitterSession> accounts = App.getAccounts();

        if (item.getItemId() < accounts.size()) {
            TwitterSession twitterSession = accounts.get(item.getItemId());
            App.toast(twitterSession.getUserName() + getString(R.string.selected));
            App.saveUserData(twitterSession);
            App.twitterSession = twitterSession;
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

    private void getFollowersListOffline() {

        ArrayList<User> userArrayList = App.getFollowersList();
        App.debug("getFollowersListOffline : " + userArrayList);
        userList = new ArrayList<>(userArrayList);
        adapter = new FollowersAdapter(FollowersListActivity.this, userList);
        recyclerView.setAdapter(adapter);
    }


}