package com.example.materialdesignapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by powerman23rus on 27.01.2018.
 * Enjoy ;)
 */

public class PageInfo implements Parcelable {
    public int mStartIndex = 0;
    public int mEndIndexIndex = 0;
    public CharSequence mText = "";

    public int getStartIndex() {
        return mStartIndex;
    }

    public int getEndIndex() {
        return mEndIndexIndex;
    }

    public CharSequence getText() {
        return mText;
    }

    public PageInfo(int startIndex, int endIndexIndex, CharSequence text) {
        mStartIndex = startIndex;
        mEndIndexIndex = endIndexIndex;
        mText = text;
    }

    protected PageInfo(Parcel in) {
        mStartIndex = in.readInt();
        mEndIndexIndex = in.readInt();
        mText = (CharSequence) in.readValue(CharSequence.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mStartIndex);
        dest.writeInt(mEndIndexIndex);
        dest.writeValue(mText);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PageInfo> CREATOR = new Parcelable.Creator<PageInfo>() {
        @Override
        public PageInfo createFromParcel(Parcel in) {
            return new PageInfo(in);
        }

        @Override
        public PageInfo[] newArray(int size) {
            return new PageInfo[size];
        }
    };
}