package com.example.xyzreader.ui.screens.taleDetail.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xyzreader.R;
import com.example.xyzreader.data.providers.FileContract;
import com.example.xyzreader.data.models.Tale;
import com.example.xyzreader.ui.customViews.ImageLoader;
import com.example.xyzreader.utils.ViewUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by powerman23rus on 23.01.2018.
 * Enjoy ;)
 */

public class FragmentTaleCover extends Fragment {
    private final static String BUNDLE_TALE = "BUNDLE_TALE";
    private Tale mTale;
    private TextView mTitleTextView;
    private TextView mTaleDescriptionTextView;
    private TextView mDateTextView;
    private ImageLoader mImageLoader;
    private FloatingActionButton mShareFloatingButton;
    private ViewGroup mDescriptionContainer;

    public static FragmentTaleCover getFragment(Tale tale) {
        Bundle bundle = new Bundle();
        Tale taleCopy = new Tale(tale.getTitle(), tale.getPhoto(), tale.getAuthor(), tale.getDate(), null);
        bundle.putParcelable(BUNDLE_TALE, taleCopy);
        FragmentTaleCover fragment = new FragmentTaleCover();
        fragment.setArguments(bundle);

        return fragment;
    }

    public void setTale(Tale tale) {
        mTale = tale;

        if (isAdded()) {
            updateView();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tale_cover, container, false);

        mDescriptionContainer = view.findViewById(R.id.cl_tale_content);
        mShareFloatingButton = view.findViewById(R.id.fb_share);
        mImageLoader = view.findViewById(R.id.iv_photo);
        mTitleTextView = view.findViewById(R.id.tv_title);
        mTaleDescriptionTextView = view.findViewById(R.id.tv_tale_description);
        mDateTextView = view.findViewById(R.id.tv_date);


        mShareFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File tale = generateTaleFile(mTale.getTitle());

                    Uri taleUri = FileProvider.getUriForFile(getContext(), FileContract.AUTHORITY, tale);

                    Intent sharingIntent = new Intent(Intent.ACTION_VIEW);
                    sharingIntent.setDataAndType(taleUri, getContext().getContentResolver().getType(taleUri));
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    if (sharingIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(sharingIntent);
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), R.string.share_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        if (getArguments() != null) {
            mTale = getArguments().getParcelable(BUNDLE_TALE);
            updateView();
        }

        return view;
    }

    private void updateView() {
        if (mTale != null) {
            mTitleTextView.setText(mTale.getTitle());
            mTaleDescriptionTextView.setText(getResources().getString(R.string.tale_description, mTale.getAuthor(), mTale.getDate()));

            mImageLoader.initialize(mTale.getPhoto());
        }
    }

    private File generateTaleFile(String taleName) throws IOException {
        File cacheTalesDir = new File(getContext().getCacheDir() + "/" + FileContract.CacheFiles.PATH_TALES);

        if (!cacheTalesDir.exists()) {
            cacheTalesDir.mkdir();
        }

        File tale = new File(cacheTalesDir.getAbsolutePath() + "/" + taleName + ".txt");
        if (!tale.exists()) {
            tale.createNewFile();
            tale.deleteOnExit();

            FileOutputStream fOut = new FileOutputStream(tale);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(mTale.getBody());
            myOutWriter.close();
        }

        return tale;
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
