package com.example.xyzreader.ui.screens.taleDetail.fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.utils.TalePager;

/**
 * Created by powerman23rus on 23.01.2018.
 * Enjoy ;)
 */

public class FragmentTalePage extends Fragment {
    private final static String BUNDLE_PAGE_PROPERTIES = "BUNDLE_PAGE_PROPERTIES";
    private final static String BUNDLE_PAGE_INFO = "BUNDLE_PAGE_INFO";
    private PageInfo mPageInfo;
    private TalePager.PageProperties mPageProperties;
    private TextView mPageTextView;

    public static FragmentTalePage getFragment(PageInfo pageInfo, TalePager.PageProperties pageProp) {
        FragmentTalePage fragment = new FragmentTalePage();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_PAGE_PROPERTIES, pageProp);
        bundle.putParcelable(BUNDLE_PAGE_INFO, pageInfo);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tale_page, container, false);
        mPageTextView = view.findViewById(R.id.tv_page);

        if (getArguments() != null) {
            mPageInfo = getArguments().getParcelable(BUNDLE_PAGE_INFO);
            mPageProperties = getArguments().getParcelable(BUNDLE_PAGE_PROPERTIES);
            mPageTextView.setText(mPageInfo.getText());
            mPageTextView.setTextSize(mPageProperties.getTextSize());
            mPageTextView.setPadding(mPageProperties.getPadding().left, mPageProperties.getPadding().top,
                    mPageProperties.getPadding().right, mPageProperties.getPadding().bottom);
        }
        return view;
    }

    public void setText(CharSequence text) {
        if (mPageTextView != null) {
            mPageTextView.setText(text);
        }
    }


    public static class PageInfo implements Parcelable {
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

}
