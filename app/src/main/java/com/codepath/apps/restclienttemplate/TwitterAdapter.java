package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TwitterAdapter extends RecyclerView.Adapter<TwitterAdapter.ViewHolder> {

    List<Tweet> tweets;
    Context context;

    public TwitterAdapter(List<Tweet> tweets, Context context) {
        this.tweets = tweets;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile;
        TextView tvName;
        TextView tvScreenName;
        TextView tvTimestamp;
        TextView tvText;
        TextView tvRetweet;
        TextView tvFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvText = itemView.findViewById(R.id.tvText);
            tvRetweet = itemView.findViewById(R.id.tvRetweet);
            tvFavorite = itemView.findViewById(R.id.tvFavorite);
        }

        public void bind(Tweet tweet) {

            Glide.with(context).load(tweet.user.profileImageUrl).transform(new RoundedCorners(10)).into(ivProfile);
            tvName.setText(tweet.user.name);
            tvScreenName.setText("@" + tweet.user.screenName);
            tvTimestamp.setText(TimeFormatter.getTimeDifference(tweet.createdAt));
            tvText.setText(tweet.text);
            tvFavorite.setText(Integer.toString(tweet.favoritCount));
            tvRetweet.setText(Integer.toString(tweet.retweetCount));
        }
    }
}
