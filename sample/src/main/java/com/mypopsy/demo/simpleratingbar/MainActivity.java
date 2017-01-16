package com.mypopsy.demo.simpleratingbar;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.mypopsy.simpleratingbar.StarIndicator;
import com.mypopsy.simpleratingbar.StarRatingBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final StarIndicator indicator = (StarIndicator) findViewById(R.id.indicator);
        final StarRatingBar bar = (StarRatingBar) findViewById(R.id.bar);
        bar.setOnRatingBarChangeListener(new StarRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(StarRatingBar ratingBar, int rating, boolean fromUser) {
                indicator.setRating(rating);
            }
        });
    }
}
