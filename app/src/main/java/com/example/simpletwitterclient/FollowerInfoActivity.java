package com.example.simpletwitterclient;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.simpletwitterclient.adapters.TweetsAdapter;
import com.example.simpletwitterclient.commons.App;
import com.example.simpletwitterclient.views.SpacesItemDecoration;
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
 * Created by thema on 1/2/2017.
 */

public class FollowerInfoActivity extends AppCompatActivity {

    private TextView stickyView;
    private ListView listView;
   // private View heroImageView;

    private View stickyViewSpacer;

    private RelativeLayout relativeSticky;

      private ImageView ivBanner;
      private ImageView ivUserImage;
    // private RecyclerView recyclerView;
    private TweetsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_info);

           /* Initialise list view, hero image, and sticky view */
        listView = (ListView) findViewById(R.id.listView);
        relativeSticky = (RelativeLayout) findViewById(R.id.relativeSticky);
        stickyView = (TextView) findViewById(R.id.stickyView);

        ivBanner=(ImageView)  findViewById(R.id.ivBanner);
        ivUserImage=(ImageView)  findViewById(R.id.ivUserImage);

        App.debug("Banner Url: " + App.getFollowerData().profileBannerUrl);
        if(App.getFollowerData().profileBannerUrl!=null) {
            App.imageLoader.displayImage(App.getFollowerData().profileBannerUrl, ivBanner, new SimpleImageLoadingListener());
        }else{
            ivBanner.setBackground(getResources().getDrawable(R.drawable.default_bg));
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

    public void getLastTweets() {

        TwitterApiClient twitterApiClient =
                TwitterCore.getInstance()
                        .getApiClient(App.twitterSession);
        Call call =
                twitterApiClient
                        .getStatusesService()
                        .userTimeline(App.twitterSession.getUserId()
                                , App.getFollowerData().screenName, 10
                                , null, null, false, false, false
                                , true);

        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> listResult) {
                for (Tweet tweet : listResult.data) {
                    // here you will get list
                    App.debug(tweet.text);
                    adapter.add(tweet);

                }

                listView.setAdapter(adapter);
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });
    }

}