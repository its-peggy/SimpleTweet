package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    // pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // for each row, inflate the layout for a tweet
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // get the data at position
        Tweet tweet = tweets.get(position);
        // bind the tweet at the position with viewholder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvRelativeTimestamp;
        ImageView ivTweetImage;
        TextView tvName;
        ImageView ivVerified;

        public ViewHolder(@NonNull @NotNull View itemView) { // itemView = 1 row in the RV
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvDetailBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvRelativeTimestamp = itemView.findViewById(R.id.tvRelativeTimestamp);
            ivTweetImage = itemView.findViewById(R.id.ivTweetImage);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivVerified = itemView.findViewById(R.id.ivDetailVerified);
            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) { // makes the onBindViewHolder method in TweetsAdapter cleaner
            tvBody.setText(tweet.body);
            String screenNameWithAt = "@"+tweet.user.screenName;
            tvScreenName.setText(screenNameWithAt);
            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .transform(new CircleCrop())
                    .into(ivProfileImage);
            String relTimestamp = context.getString(R.string.dot_separator) + " " + getRelativeTimeAgo(tweet.createdAt);
            tvRelativeTimestamp.setText(relTimestamp);
            if (tweet.mediaURL.isEmpty()) {
                ivTweetImage.setVisibility(View.GONE);
            } else {
                // Log.d("asdfjkl", tweet.mediaURL);
                int radius = 25; // corner radius, higher value = more rounded
                int margin = 0; // crop margin, set to 0 for corners with no crop
                Glide.with(context)
                        .load(tweet.mediaURL)
                        // .transform(new RoundedCornersTransformation(radius, margin))
                        .transform(new MultiTransformation(new CenterCrop(), new RoundedCornersTransformation(radius, margin)))
                        .into(ivTweetImage); // load INTO the ivPoster view
            }
            tvName.setText(tweet.user.name);
            if (!tweet.user.verified) {
                ivVerified.setVisibility(View.GONE);
            }

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            // if position exists in the view
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                // intent for new activity
                Intent intent = new Intent(context, TweetDetailActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                // show the new activity
                context.startActivity(intent);
            }
        }

        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);
            try {
                long time = sf.parse(rawJsonDate).getTime();
                long now = System.currentTimeMillis();

                final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    return "just now";
                } else if (diff < 2 * MINUTE_MILLIS) {
                    return "a minute ago";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + "m";
                } else if (diff < 90 * MINUTE_MILLIS) {
                    return "an hour ago";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + "h";
                } else if (diff < 48 * HOUR_MILLIS) {
                    return "yesterday";
                } else {
                    return diff / DAY_MILLIS + "d";
                }
            } catch (ParseException e) {
                Log.i("TweetsAdapter", "getRelativeTimeAgo failed");
                e.printStackTrace();
            }
            return "";
        }
    }

}
