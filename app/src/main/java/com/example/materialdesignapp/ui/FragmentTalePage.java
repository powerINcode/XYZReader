package com.example.materialdesignapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.materialdesignapp.R;
import com.example.materialdesignapp.data.Tale;
import com.example.materialdesignapp.data.TalePager;

/**
 * Created by powerman23rus on 23.01.2018.
 * Enjoy ;)
 */

public class FragmentTalePage extends Fragment {
    public final static String BUNDLE_PAGE_PROPERTIES = "BUNDLE_PAGE_PROPERTIES";
    public final static String BUNDLE_PAGE_TEXT = "BUNDLE_PAGE_TEXT";
    private CharSequence mText;
    private TalePager.PageProperties mPageProperties;
    private TextView mPageTextView;

    public static FragmentTalePage getFragment(CharSequence text, TalePager.PageProperties pageProp) {
        FragmentTalePage fragment = new FragmentTalePage();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_PAGE_PROPERTIES, pageProp);
        bundle.putCharSequence(BUNDLE_PAGE_TEXT, text);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tale_page, container, false);
        mPageTextView = view.findViewById(R.id.tv_page);

        if (getArguments() != null) {
            mText = getArguments().getCharSequence(BUNDLE_PAGE_TEXT);
            mPageProperties = getArguments().getParcelable(BUNDLE_PAGE_PROPERTIES);
            mPageTextView.setText(mText);
            mPageTextView.setTextSize(mPageProperties.getTextSize());
            mPageTextView.setPadding(mPageProperties.getPadding().left, mPageProperties.getPadding().top,
                    mPageProperties.getPadding().right, mPageProperties.getPadding().bottom);
        }
        return view;
    }

}
