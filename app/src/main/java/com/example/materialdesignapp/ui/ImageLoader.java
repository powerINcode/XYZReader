package com.example.materialdesignapp.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.materialdesignapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by powerman23rus on 16.01.2018.
 * Enjoy ;)
 */

public class ImageLoader extends FrameLayout {
    private String mRatio;
    private ProgressBar mProgressBar;
    private RatioImageView mRatioImageView;

    public ImageLoader(Context context) {
        super(context);
        setup(context, null, 0);
    }

    public ImageLoader(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0);
    }

    public ImageLoader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs, defStyle);
    }

    public void initialize(String url) {

        mProgressBar.setVisibility(VISIBLE);
        Picasso.with(getContext()).load(url).into(mRatioImageView, new Callback() {
            @Override
            public void onSuccess() {
                mRatioImageView.setVisibility(VISIBLE);
                mProgressBar.setVisibility(INVISIBLE);
            }

            @Override
            public void onError() {

            }
        });
    }

    private void setup(Context context, AttributeSet attrs, int defStyle) {

        mProgressBar = new ProgressBar(getContext());
        mProgressBar.setLayoutParams(new LayoutParams(200, 200, Gravity.CENTER));
        addView(mProgressBar);



        TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ImageLoader, 0, 0);
        try {
            mRatio = ta.getString(R.styleable.ImageLoader_image_ratio);
        } finally {
            ta.recycle();
        }

        mRatioImageView = new RatioImageView(getContext());
        mRatioImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mRatioImageView.setRatio(mRatio);
        mRatioImageView.setVisibility(INVISIBLE);
        addView(mRatioImageView);
    }
}
