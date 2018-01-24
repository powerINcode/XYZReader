package com.example.materialdesignapp.ui;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.materialdesignapp.R;
import com.example.materialdesignapp.data.ArticleLoader;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment3 extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment3";

    public static final String ARG_ITEM_ID = "item_id";

    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private int mMutedColor = 0xFF333333;
    private ColorDrawable mStatusBarColorDrawable;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);
    private CollapsingToolbarLayout mToolbar;
    private boolean mIsLoaded;
    private ArrayList<String> mBodyParts;
    private int mCurrentPart;
    private int mParts;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment3() {
    }

    public static ArticleDetailFragment3 newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment3 fragment = new ArticleDetailFragment3();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        setHasOptionsMenu(true);
    }

    public ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail3, container, false);
        mToolbar = mRootView.findViewById(R.id.toolbar);

        mStatusBarColorDrawable = new ColorDrawable(0);

//        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
//                        .setType("text/plain")
//                        .setText("Some sample text")
//                        .getIntent(), getString(R.string.action_share)));
//            }
//        });

        bindViews();
        return mRootView;
    }

    static float constrain(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void bindViews() {
        if (mRootView == null) {
            return;
        }


        final ImageLoader imageView = mRootView.findViewById(R.id.iv_photo);
        final TextView taleDescriptionView = (TextView) mRootView.findViewById(R.id.tv_tale_description);
        final TextView bodyView = (TextView) mRootView.findViewById(R.id.tv_body);

        String date;


        if (mCursor != null && !mIsLoaded) {

            String body = mCursor.getString(ArticleLoader.Query.BODY);
            int partSize = 1024;
            mParts = body.length() / partSize;
            int rest = body.length() % partSize;

            if (rest != 0) {
                mParts++;
            }

            mBodyParts = new ArrayList<String>();
            for (int i = 0; i < mParts; i ++) {
                int offset = i * partSize;

                if (i == mParts - 1) {
                    mBodyParts.add(body.substring(offset, body.length() - 1));
                } else {
                    mBodyParts.add(body.substring(offset, offset + partSize));
                }
            }



            String string = mCursor.getString(ArticleLoader.Query.TITLE);
            mToolbar.setTitle(string);

//            mRootView.setAlpha(0);
//            mRootView.setVisibility(View.VISIBLE);
//            mRootView.animate().alpha(1);
            Date publishedDate = parsePublishedDate();

            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
                date = DateUtils.getRelativeTimeSpanString(
                        publishedDate.getTime(), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL).toString();

            } else {
                // If date is before 1902, just show the string
                date = outputFormat.format(publishedDate);

            }

//            taleDescriptionView.setText(getResources().getString(R.string.tale_description, mCursor.getString(ArticleLoader.Query.AUTHOR), date));


            final NestedScrollView sv = mRootView.findViewById(R.id.tmp);
            sv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
                    View view = (View) sv.getChildAt(sv.getChildCount()-1);

                    double offset = (view.getBottom() * 0.9) - (sv.getHeight() + sv.getScrollY());

                    if (offset < 0) {
                        mCurrentPart++;
                        if (mCurrentPart < mParts) {
                            bodyView.append(mBodyParts.get(mCurrentPart));
                        }
                    }

//                    // Calculate the scrolldiff
//                    int diff = (view.getBottom()-(sv.getHeight()+ sv.getScrollY()));
//
//                    // if diff is zero, then the bottom has been reached
//                    if( diff == 0 )
//                    {
//                        // notify that we have reached the bottom
//                        Log.d("hahaha", "MyScrollView: Bottom has been reached" );
//                    }
                }
            });
            bodyView.setText(mBodyParts.get(mCurrentPart));
            imageView.initialize(mCursor.getString(ArticleLoader.Query.PHOTO_URL));
//            Picasso.with(getActivity()).load(mCursor.getString(ArticleLoader.Query.PHOTO_URL)).into(imageView);

        } else {
//            mRootView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }
}
