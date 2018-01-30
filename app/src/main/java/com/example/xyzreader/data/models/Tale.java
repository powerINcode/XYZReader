package com.example.xyzreader.data.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.xyzreader.data.loaders.ArticleLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by powerman23rus on 23.01.2018.
 * Enjoy ;)
 */

public class Tale implements Parcelable {
    private static final String TAG = Tale.class.getSimpleName();

    private String mTitle;
    private String mPhoto;
    private String mAuthor;
    private String mDate;
    private String mBody;

    public String getTitle() {
        return mTitle;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDate() {
        return mDate;
    }

    public String getBody() {
        return mBody;
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    public Tale(Cursor cursor) {
        mPhoto = cursor.getString(ArticleLoader.Query.PHOTO_URL);
        mTitle = cursor.getString(ArticleLoader.Query.TITLE);
        mAuthor = cursor.getString(ArticleLoader.Query.AUTHOR);
        mBody = cursor.getString(ArticleLoader.Query.BODY);

        Date publishedDate = parsePublishedDate(cursor);
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            mDate = DateUtils.getRelativeTimeSpanString(
                    publishedDate.getTime(), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString();

        } else {
            // If date is before 1902, just show the string
            mDate = outputFormat.format(publishedDate);

        }
    }

    public Tale(String title, String photo, String author, String date, String body) {
        mTitle = title;
        mPhoto = photo;
        mAuthor = author;
        mDate = date;
        mBody = body;
    }

    private Date parsePublishedDate(Cursor cursor) {
        try {
            String date = cursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    protected Tale(Parcel in) {
        mTitle = in.readString();
        mPhoto = in.readString();
        mAuthor = in.readString();
        mDate = in.readString();
        mBody = in.readString();
        dateFormat = (SimpleDateFormat) in.readValue(SimpleDateFormat.class.getClassLoader());
        outputFormat = (SimpleDateFormat) in.readValue(SimpleDateFormat.class.getClassLoader());
        START_OF_EPOCH = (GregorianCalendar) in.readValue(GregorianCalendar.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPhoto);
        dest.writeString(mAuthor);
        dest.writeString(mDate);
        dest.writeString(mBody);
        dest.writeValue(dateFormat);
        dest.writeValue(outputFormat);
        dest.writeValue(START_OF_EPOCH);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Tale> CREATOR = new Parcelable.Creator<Tale>() {
        @Override
        public Tale createFromParcel(Parcel in) {
            return new Tale(in);
        }

        @Override
        public Tale[] newArray(int size) {
            return new Tale[size];
        }
    };
}