package com.example.materialdesignapp.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by powerman23rus on 16.01.2018.
 * Enjoy ;)
 */

public class ImageLoader extends FrameLayout {
    public ImageLoader(Context context) {
        super(context);
        setup();
    }

    public ImageLoader(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public ImageLoader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    public void initialize(String url) {
        final CircleImageView circleImageView = new CircleImageView(getContext());
        circleImageView.setVisibility(GONE);
        addView(circleImageView);

        final ProgressBar progressBar = new ProgressBar(getContext());
        addView(progressBar);

        Picasso.with(getContext()).load(url).into(circleImageView, new Callback() {
            @Override
            public void onSuccess() {
                circleImageView.setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
            }

            @Override
            public void onError() {

            }
        });
    }

    private void setup() {

    }
}
