package com.example.materialdesignapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.materialdesignapp.R;
import com.example.materialdesignapp.data.Tale;
import com.example.materialdesignapp.utils.ViewUtil;

import java.io.File;
import java.io.IOException;

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
    private ViewGroup mDescriptionContainer;

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

        mDescriptionContainer = view.findViewById(R.id.cl_tale_content);
        mShareFloatingButton = view.findViewById(R.id.fb_share);

        mShareFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File shareFile = new File(Environment.getExternalStorageDirectory().toString() + "/" + mTale.getTitle() + ".txt");
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + shareFile.getAbsolutePath()));
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(sharingIntent, "share file with"));

                if (sharingIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(sharingIntent);
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

    @Override
    public void onResume() {
        super.onResume();

        ViewUtil.onLoad(mDescriptionContainer, new Runnable() {
            @Override
            public void run() {
                setInitialState();
                showDescription();
            }
        });
    }

    public void showDescription() {
        animateDescriptionContainer(0, 1);
    }

    public void hideDescription() {
        animateDescriptionContainer(mDescriptionContainer.getHeight() / 2, 0);
    }

    private void animateDescriptionContainer(float transitionY, float scale) {
        if (getContext() != null && getContext().getResources() != null) {
            int delayDuration = 300;
            int animationDuration = 200;

            mDescriptionContainer.animate()
                    .translationY(transitionY)
                    .setStartDelay(delayDuration)
                    .setDuration(animationDuration)
                    .setInterpolator(ViewUtil.getAccelerateInterpolator(getContext()))
                    .start();

            mShareFloatingButton.animate()
                    .setStartDelay(delayDuration + animationDuration)
                    .alpha(scale)
                    .scaleX(scale)
                    .scaleY(scale)
                    .setInterpolator(ViewUtil.getAccelerateInterpolator(getContext()))
                    .start();
        }
    }

    private void setInitialState() {
        mDescriptionContainer.setTranslationY(mDescriptionContainer.getHeight());
        mShareFloatingButton.setScaleX(0);
        mShareFloatingButton.setScaleY(0);
    }
}
