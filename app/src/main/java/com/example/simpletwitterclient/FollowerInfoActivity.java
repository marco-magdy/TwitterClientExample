package com.example.simpletwitterclient;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.simpletwitterclient.adapters.TweetsAdapter;
import com.example.simpletwitterclient.commons.App;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import retrofit2.Call;


/**
 * This class displays profile image and background image
 * of a previously selected follower"from the followers list"
 * it also displays the last 10 tweets of that user
 */
public class FollowerInfoActivity extends AppCompatActivity {

    private RelativeLayout relativeSticky;
    private ImageView ivBanner;
    private ImageView ivUserImage;

    private TextView stickyView;
    private View stickyViewSpacer;

    private ListView listView;
    private TweetsAdapter adapter;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_info);


        listView = (ListView) findViewById(R.id.listView);
        relativeSticky = (RelativeLayout) findViewById(R.id.relativeSticky);
        stickyView = (TextView) findViewById(R.id.stickyView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ivBanner = (ImageView) findViewById(R.id.ivBanner);
        ivUserImage = (ImageView) findViewById(R.id.ivUserImage);


        App.debug("Banner Url: " + App.getFollowerData().profileBannerUrl);
        if (App.getFollowerData().profileBannerUrl != null) {
            App.imageLoader.displayImage(App.getFollowerData().profileBannerUrl, ivBanner, new SimpleImageLoadingListener());
        } else {
            ivBanner.setBackground(ContextCompat.getDrawable(this, R.drawable.default_bg));
        }
        App.imageLoader.displayImage(App.getFollowerData().profileImageUrl, ivUserImage, new SimpleImageLoadingListener());
        /* Inflate list header layout */
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listHeader = inflater.inflate(R.layout.activity_follower_info_header, null);
        stickyViewSpacer = listHeader.findViewById(R.id.stickyViewPlaceholder);

        stickyView.setText(App.getFollowerData().name);
        stickyView.setTextColor(Color.parseColor("#" + App.getFollowerData().profileTextColor));
        stickyView.setBackgroundColor(Color.parseColor("#" + App.getFollowerData().profileBackgroundColor));

        adapter = new TweetsAdapter(FollowerInfoActivity.this, 0);

        /* Add list view header */
        listView.addHeaderView(listHeader);


          /* Handle list View scroll events */
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                /* Check if the first item is already reached to top.*/
                if (listView.getFirstVisiblePosition() == 0) {
                    View firstChild = listView.getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();
                    }

                    int heroTopY = stickyViewSpacer.getTop();
                    stickyView.setY(Math.max(0, heroTopY + topY));

                    /* Set the image to scroll half of the amount that of ListView */
                    relativeSticky.setY(topY * 0.5f);
                }
            }
        });
        getLastTweets();
    }

    private void getLastTweets() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(App.twitterSession);
        Call<List<Tweet>> call = twitterApiClient.getStatusesService().userTimeline(App.twitterSession.getUserId()
                , App.getFollowerData().screenName, 10, null, null, false, false, false, true);

        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> listResult) {
                progressBar.setVisibility(View.GONE);
                for (Tweet tweet : listResult.data) {
                    // here you will get list
                    App.debug(tweet.text);
                    adapter.add(tweet);
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void failure(TwitterException e) {
                progressBar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        });
    }

}