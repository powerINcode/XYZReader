package com.example.xyzreader.utils;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.StaticLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.xyzreader.ui.screens.taleDetail.fragments.FragmentTalePage.*;

/**
 * Created by powerman23rus on 23.01.2018.
 * Enjoy ;)
 */

public class TalePager {
    private final TextView mTextView;
    private final Rect mPadding;
    private final float mTextSize;
    private int mHeight;
    private int mWidth;
    private String mText;
    private int bottomOffsetForLongCharacters = 10;
    private boolean mIsTextProcessed;

    private ArrayList<PageInfo> mPages = new ArrayList<>();

    public TalePager(TextView textView, String text, float actionBarHeight) {
        mTextView = textView;
        mHeight = textView.getHeight();
        mWidth = textView.getWidth();
        mText = text;
        mTextSize = mTextView.getTextSize() / mTextView.getContext().getResources().getDisplayMetrics().density;

        mTextView.setTextSize(mTextSize);

        mPadding = new Rect(mTextView.getPaddingStart(), mTextView.getPaddingTop(),
                mTextView.getPaddingEnd(), mTextView.getPaddingBottom());
        mHeight -= mPadding.top + mPadding.bottom + bottomOffsetForLongCharacters + actionBarHeight;
        mWidth -= mPadding.left + mPadding.right;
    }

    public PageInfo getPage(int page) {
        return mPages.get(page);
    }

    public int getPageCount() {
        return mPages.size();
    }

    public boolean isTextProcessed() {
        return mIsTextProcessed;
    }

    public void processText() {

        final StaticLayout layout = new StaticLayout(mText, mTextView.getPaint(), mWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, true);

        final int lines = layout.getLineCount();
        final CharSequence text = layout.getText();
        int startOffset = 0;
        int height = mHeight;

        for (int i = 0; i < lines; i++) {
            if (height < layout.getLineBottom(i)) {
                // When the layout height has been exceeded
                PageInfo pageInfo = new PageInfo(startOffset, layout.getLineStart(i), text.subSequence(startOffset, layout.getLineStart(i)));
                mPages.add(pageInfo);
                startOffset = layout.getLineStart(i);
                height = layout.getLineTop(i) + mHeight;
            }

            if (i == lines - 1) {
                // Put the rest of the text into the last page
                PageInfo pageInfo = new PageInfo(startOffset, layout.getLineStart(i), text.subSequence(startOffset, layout.getLineStart(i)));
                mPages.add(pageInfo);
                break;
            }
        }

        mIsTextProcessed = true;
    }

    public PageProperties getPageProperties() {
        return new PageProperties(mTextSize, mPadding);
    }

    public static class PageProperties implements Parcelable {
        final float mTextSize;
        final Rect mPadding;

        PageProperties(float textSize, Rect padding) {
            mTextSize = textSize;
            mPadding = padding;
        }

        public float getTextSize() {
            return mTextSize;
        }

        public Rect getPadding() {
            return mPadding;
        }

        PageProperties(Parcel in) {
            mTextSize = in.readInt();
            mPadding = (Rect) in.readValue(Rect.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(mTextSize);
            dest.writeValue(mPadding);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<PageProperties> CREATOR = new Parcelable.Creator<PageProperties>() {
            @Override
            public PageProperties createFromParcel(Parcel in) {
                return new PageProperties(in);
            }

            @Override
            public PageProperties[] newArray(int size) {
                return new PageProperties[size];
            }
        };
    }
}
