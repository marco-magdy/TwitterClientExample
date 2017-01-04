package com.example.simpletwitterclient.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.simpletwitterclient.R;
import com.example.simpletwitterclient.commons.App;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;

/**
 * Created by thema on 1/3/2017.
 */

public class TweetsAdapter extends ArrayAdapter<Tweet> {

    private Context context;
    private ArrayList<Tweet> items;


    public TweetsAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder v;
        if (convertView == null) {
            v = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tweet, null);
            v.ivUserImg = (ImageView) convertView.findViewById(R.id.ivUserImg);
            v.tvFullName = (TextView) convertView.findViewById(R.id.tvFullName);
            v.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            v.tvTweet = (TextView) convertView.findViewById(R.id.tvTweet);
            v.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            convertView.setTag(v);
        } else {
            v = (ViewHolder) convertView.getTag();
        }
        final Tweet tweet = getItem(position);


        App.imageLoader.displayImage(tweet.user.profileImageUrl, v.ivUserImg, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
                v.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                v.progressBar.setVisibility(View.GONE);
            }
        });

        v.tvFullName.setText(tweet.user.name);
        String screenName = "@" + tweet.user.screenName;
        v.tvScreenName.setText(screenName);
        v.tvTweet.setText(tweet.text);
        return convertView;
    }

    private static class ViewHolder {
        ImageView ivUserImg;
        TextView tvFullName, tvScreenName, tvTweet;
        ProgressBar progressBar;
    }
}
