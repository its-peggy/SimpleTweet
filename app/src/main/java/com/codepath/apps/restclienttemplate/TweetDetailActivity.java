package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity {

    Tweet tweet;
    ImageView ivDetailProfileImage;
    TextView tvDetailName;
    ImageView ivDetailVerified;
    TextView tvDetailScreenName;
    TextView tvDetailBody;
    ImageView ivDetailTweetImage;
    TextView tvDetailTimeAndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        ivDetailProfileImage = (ImageView) findViewById(R.id.ivDetailProfileImage);
        tvDetailName = (TextView) findViewById(R.id.tvDetailName);
        ivDetailVerified = (ImageView) findViewById(R.id.ivDetailVerified);
        tvDetailScreenName = (TextView) findViewById(R.id.tvDetailScreenName);
        tvDetailBody = (TextView) findViewById(R.id.tvDetailBody);
        ivDetailTweetImage = (ImageView) findViewById(R.id.ivDetailTweetImage);
        tvDetailTimeAndDate = (TextView) findViewById(R.id.tvDetailTimeAndDate);

        tvDetailName.setText(tweet.user.name);
        tvDetailBody.setText(tweet.body);
        String screenNameWithAt = "@"+tweet.user.screenName;
        tvDetailScreenName.setText(screenNameWithAt);
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .transform(new CircleCrop())
                .into(ivDetailProfileImage);
        if (tweet.mediaURL.isEmpty()) {
            ivDetailTweetImage.setVisibility(View.GONE);
        } else {
            // Log.d("asdfjkl", tweet.mediaURL);
            int radius = 25; // corner radius, higher value = more rounded
            int margin = 0; // crop margin, set to 0 for corners with no crop
            Glide.with(this)
                    .load(tweet.mediaURL)
                    // .transform(new RoundedCornersTransformation(radius, margin))
                    .transform(new MultiTransformation(new CenterCrop(), new RoundedCornersTransformation(radius, margin)))
                    .into(ivDetailTweetImage); // load INTO the ivPoster view
        }

        tvDetailTimeAndDate.setText(getTimeDateString(tweet.createdAt));

    }

    public String getTimeDateString(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        try {
            Date date = sf.parse(rawJsonDate);
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
            String dotString = " " + this.getString(R.string.dot_separator) + " ";
            String result =  timeFormatter.format(date) + dotString + dateFormatter.format(date);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


}