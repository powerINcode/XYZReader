package com.example.materialdesignapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.materialdesignapp.R;
import com.example.materialdesignapp.data.Tale;

/**
 * Created by powerman23rus on 23.01.2018.
 * Enjoy ;)
 */

public class FragmentTaleCover extends Fragment {
    public final static String BUNDLE_TALE = "BUNDLE_TALE";
    private Tale mTale;
    private TextView mTitleTextView;
    private TextView mTaleDescriptionTextView;
    private TextView mDateTextView;
    private ImageLoader mImageLoader;
    private FloatingActionButton mShareFloatingButton;

    public static FragmentTaleCover getFragment(Tale tale) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_TALE, tale);
        FragmentTaleCover fragment = new FragmentTaleCover();
        fragment.setArguments(bundle);

        return fragment;
    }

    public String getTale() {
        return ((ActivityTaleDetail) getActivity()).getTale().getBody();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tale_cover, container, false);

        mShareFloatingButton = view.findViewById(R.id.fb_share);
        mShareFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                        .setChooserTitle(R.string.share_intent_title)
                        .setType("text/plain")
                        .setText(getTale())
                        .getIntent();

                if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(shareIntent);
                }
            }
        });

        if (getArguments() != null) {
            mTale = getArguments().getParcelable(BUNDLE_TALE);
            mImageLoader = view.findViewById(R.id.iv_photo);
            mTitleTextView = view.findViewById(R.id.tv_title);
            mTaleDescriptionTextView = view.findViewById(R.id.tv_tale_description);
            mDateTextView = view.findViewById(R.id.tv_date);

            mTitleTextView.setText(mTale.getTitle());
            mTaleDescriptionTextView.setText(getResources().getString(R.string.tale_description, mTale.getAuthor(), mTale.getDate()));


            mImageLoader.initialize(mTale.getPhoto());
        }

        return view;
    }
}
