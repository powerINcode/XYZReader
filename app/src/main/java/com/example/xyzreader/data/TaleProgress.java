package com.example.xyzreader.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.xyzreader.data.TaleProgressContract.TaleProgressEntry;

/**
 * Created by powerman23rus on 25.01.2018.
 * Enjoy ;)
 */

public class TaleProgress implements Parcelable {

    private long mId;
    private long mTaleId;
    private int mPauseIndex;

    public long getId() {
        return mId;
    }

    public long getTaleId() {
        return mTaleId;
    }

    public int getPauseIndex() {
        return mPauseIndex;
    }

    public void setId(long id) {
        mId = id;
    }

    public void setTaleId(long taleId) {
        mTaleId = taleId;
    }

    public void setPauseIndex(int pauseIndex) {
        mPauseIndex = pauseIndex;
    }

    public TaleProgress(Cursor cursor) {
        mId = cursor.getLong(TaleProgressEntry.COLUMN_ID_INDEX);
        mTaleId = cursor.getLong(TaleProgressEntry.COLUMN_TALE_ID_INDEX);
        mPauseIndex = cursor.getInt(TaleProgressEntry.COLUMN_PAUSE_INDEX);
    }

    public TaleProgress(long id, long taleId, int page) {
        mId = id;
        mTaleId = taleId;
        mPauseIndex = page;
    }

    protected TaleProgress(Parcel in) {
        mId = in.readLong();
        mTaleId = in.readLong();
        mPauseIndex = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mTaleId);
        dest.writeInt(mPauseIndex);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaleProgress> CREATOR = new Parcelable.Creator<TaleProgress>() {
        @Override
        public TaleProgress createFromParcel(Parcel in) {
            return new TaleProgress(in);
        }

        @Override
        public TaleProgress[] newArray(int size) {
            return new TaleProgress[size];
        }
    };
}