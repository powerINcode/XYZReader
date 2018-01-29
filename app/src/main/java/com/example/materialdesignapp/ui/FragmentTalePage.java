package com.example.materialdesignapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.materialdesignapp.R;
import com.example.materialdesignapp.data.PageInfo;
import com.example.materialdesignapp.data.TalePager;

/**
 * Created by powerman23rus on 23.01.2018.
 * Enjoy ;)
 */

public class FragmentTalePage extends Fragment {
    public final static String BUNDLE_PAGE_PROPERTIES = "BUNDLE_PAGE_PROPERTIES";
    public final static String BUNDLE_PAGE_INFO = "BUNDLE_PAGE_INFO";
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

}
