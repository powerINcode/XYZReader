package com.example.xyzreader.ui.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.xyzreader.R;

/**
 * Created by powerman23rus on 27.01.2018.
 * Enjoy ;)
 */

@SuppressLint("AppCompatCustomView")
public class RatioImageView extends ImageView {
    private int mWidthRatio;
    private int mHeightRatio;

    public RatioImageView(Context context) {
        super(context);
        setup(context, null, 0);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr);
    }

    private void setup(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RatioImageView, defStyleAttr, 0);

            try {
                String ratio = ta.getString(R.styleable.RatioImageView_ratio);
                if (ratio == null || TextUtils.isEmpty(ratio)) {
                    return;
                }

                setRatio(ratio);

            } finally {
                ta.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidthRatio < 1 || mHeightRatio < 1) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int newWidth = MeasureSpec.getSize(heightMeasureSpec) * mWidthRatio / mHeightRatio;
        int newWidthSpec = MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY);

        int newHeight = MeasureSpec.getSize(widthMeasureSpec) * mHeightRatio / mWidthRatio;
        int newHeightSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, newHeightSpec);
    }

    public void setRatio(String ratio) {
        if (ratio == null) {
            return;
        }

        String[] splitRatio = ratio.split(":");

        if (splitRatio.length != 2) {
            throw new IllegalArgumentException("Invalid aspect ratio arguments: " + splitRatio.length);
        }

        mWidthRatio = Integer.parseInt(splitRatio[0]);
        mHeightRatio = Integer.parseInt(splitRatio[1]);
    }
}
