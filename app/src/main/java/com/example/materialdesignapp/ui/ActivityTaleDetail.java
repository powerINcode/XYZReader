package com.example.materialdesignapp.ui;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.materialdesignapp.R;
import com.example.materialdesignapp.data.ArticleLoader;
import com.example.materialdesignapp.data.Tale;
import com.example.materialdesignapp.data.TalePager;
import com.example.materialdesignapp.data.TaleProgress;
import com.example.materialdesignapp.data.TaleProgressContract.TaleProgressEntry;
import com.example.materialdesignapp.data.TaleProgressLoader;
import com.example.materialdesignapp.utils.ViewUtil;

public class ActivityTaleDetail extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final int TALE_START_INDEX = -1;
    public static final int TALE_COVER_OFFSET = 1;

    public final static int LOADER_TALE = 1000;
    public final static int LOADER_TALE_PROGRESS = 1001;

    public final static String EXTRA_TALE_ID = "EXTRA_TALE_ID";
    public final static String BUNDLE_TALE = "BUNDLE_TALE";
    public final static String BUNDLE_TALE_PROGRESS = "BUNDLE_TALE_PROGRESS";

    private long mTaleId;
    private Tale mTale;
    private TaleProgress mTaleProgress;
    private boolean mIsTaleRewind;
    private ViewPager mViewPager;
    private TextView mPageTextView;
    private PageAdapter mAdapter;
    private TalePager mTalePager;
    private Toolbar mToolbar;
    private FragmentTaleCover mTaleCoverFragment;
    private FragmentTalePage mPageFragment;

    public Tale getTale() {
        return mTale;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tale_detail);

        if (getIntent() != null) {
            mTaleId = getIntent().getLongExtra(EXTRA_TALE_ID, 0);
        }

        if (savedInstanceState != null) {
            mTale = savedInstanceState.getParcelable(BUNDLE_TALE);
            mTaleProgress = savedInstanceState.getParcelable(BUNDLE_TALE_PROGRESS);
        }

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.GONE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPageTextView = findViewById(R.id.tv_page);
        mViewPager = findViewById(R.id.pager);
        mViewPager.setVisibility(View.INVISIBLE);

        mViewPager.addOnPageChangeListener(this);
        mAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);


        ViewUtil.onLoad(mPageTextView, new Runnable() {
            @Override
            public void run() {
                if (mTale == null) {
                    startTaleLoader();
                } else {
                    onTaleLoaded();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(BUNDLE_TALE, mTale);
        outState.putParcelable(BUNDLE_TALE_PROGRESS, mTaleProgress);
    }

    //region PagerView events
    @Override
    public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            mToolbar.setVisibility(View.GONE);

            if (mTaleCoverFragment != null) {
                mTaleCoverFragment.showDescription();
            }

            updatePageStartIndex(TALE_START_INDEX);

        } else {
            mToolbar.setVisibility(View.VISIBLE);

            if (mTaleCoverFragment != null) {
                mTaleCoverFragment.hideDescription();
            }

            int talePageIndex = position - TALE_COVER_OFFSET;
            mPageFragment = (FragmentTalePage) mAdapter.getItem(mViewPager.getCurrentItem());
            updatePageStartIndex(mTalePager.getPage(talePageIndex).getStartIndex());
        }


    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void updatePageStartIndex(int pageStartIndex) {
        if (mIsTaleRewind) {
            mTaleProgress.setStartIndex(pageStartIndex);
            TaleProgressEntry.updatePage(getContentResolver(), mTaleId, mTaleProgress.getStartIndex());
        }
    }
    //endregion

    //region TaleLoader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_TALE) {
            return ArticleLoader.newInstanceForItemId(this, mTaleId);
        } else {
            return TaleProgressLoader.newInstanceForTaleId(this, mTaleId);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null) {
            return;
        }

        cursor.moveToFirst();

        switch (loader.getId()) {
            case LOADER_TALE:
                    mTale = new Tale(cursor);
                    onTaleLoaded();
                    break;
            case LOADER_TALE_PROGRESS:
                if (cursor.getCount() == 0) {
                    long id = TaleProgressEntry.create(getContentResolver(), mTaleId);
                    mTaleProgress = new TaleProgress(id, mTaleId, TALE_START_INDEX);
                } else {
                    mTaleProgress = new TaleProgress(cursor);
                }
                onTaleProgressLoaded();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //endregion

    private void startTaleLoader() {
        if (getLoaderManager().getLoader(LOADER_TALE) != null) {
            getLoaderManager().initLoader(LOADER_TALE, null, ActivityTaleDetail.this);
        } else {
            getLoaderManager().restartLoader(LOADER_TALE, null, ActivityTaleDetail.this);
        }
    }

    private void startTaleProgressLoader() {
        if (getLoaderManager().getLoader(LOADER_TALE_PROGRESS) != null) {
            getLoaderManager().initLoader(LOADER_TALE_PROGRESS, null, ActivityTaleDetail.this);
        } else {
            getLoaderManager().restartLoader(LOADER_TALE_PROGRESS, null, ActivityTaleDetail.this);
        }
    }

    private void onTaleProgressLoaded() {
        if (mTaleProgress.getStartIndex() == TALE_START_INDEX) {
            mViewPager.setCurrentItem(0, false);
        } else {
            int page = 0;
            for (int i = 0; i < mTalePager.getPageCount(); i++) {
                int startIndex = mTalePager.getPage(i).getStartIndex();
                int endIndexIndex = mTalePager.getPage(i).getEndIndex();
                if (mTaleProgress.getStartIndex() >= startIndex  && mTaleProgress.getStartIndex() <= endIndexIndex) {
                    page = i;
                    break;
                }
            }

            mViewPager.setCurrentItem(page + TALE_COVER_OFFSET, false);
            mAdapter.notifyDataSetChanged();
        }


        mViewPager.setVisibility(View.VISIBLE);
        mIsTaleRewind = true;
    }

    @SuppressLint("StaticFieldLeak")
    private void onTaleLoaded() {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;

        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        mTalePager = new TalePager(mPageTextView, mTale.getBody(), actionBarHeight);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mTalePager.processText();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();

                if (mTaleProgress == null) {
                    startTaleProgressLoader();
                } else {
                    onTaleProgressLoaded();
                }
            }
        }.execute();
    }

    class PageAdapter extends FragmentStatePagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mTalePager == null ? 0 : mTalePager.getPageCount() + TALE_COVER_OFFSET;
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                mTaleCoverFragment = FragmentTaleCover.getFragment(mTale);
                return mTaleCoverFragment;
            } else {
                return FragmentTalePage.getFragment(mTalePager.getPage(position - TALE_COVER_OFFSET), mTalePager.getPageProperties());
            }
        }
    }
}
