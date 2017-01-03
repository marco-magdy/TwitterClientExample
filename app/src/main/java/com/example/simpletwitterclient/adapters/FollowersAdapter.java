package com.example.simpletwitterclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.simpletwitterclient.FollowerInfoActivity;
import com.example.simpletwitterclient.R;
import com.example.simpletwitterclient.commons.App;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thema on 1/3/2017.
 */

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.MyViewHolder> {

    private transient Context context;
    private ArrayList<User> userList;


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserImg;
        ProgressBar progressBar;
        TextView tvFullName, tvHandle, tvBio;

        MyViewHolder(View view) {
            super(view);
            ivUserImg = (ImageView) view.findViewById(R.id.ivUserImg);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            tvFullName = (TextView) view.findViewById(R.id.tvFullName);
            tvHandle = (TextView) view.findViewById(R.id.tvHandle);
            tvBio = (TextView) view.findViewById(R.id.tvBio);
        }
    }


    public FollowersAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follower, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final User user = userList.get(position);

        //holder.ivUserImg.setImageBitmap(xx);
        App.imageLoader.displayImage(user.profileImageUrl, holder.ivUserImg, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
                holder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                holder.progressBar.setVisibility(View.GONE);
            }
        });
        holder.tvFullName.setText(user.name);
        String handle = "@" + user.screenName;
        holder.tvHandle.setText(handle);
        holder.tvBio.setText(user.description);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.isOnline()) {
                    Intent i = new Intent(context, FollowerInfoActivity.class);
                    App.saveFollowerData(user);
                    context.startActivity(i);
                } else {
                    App.toast("Network connection problems!");
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addItems(ArrayList<User> newUsers) {
        int startCount = userList.size();
        userList.addAll(newUsers);
        //notifyItemInserted(startCount);

        notifyItemRangeChanged(0, userList.size());


    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}