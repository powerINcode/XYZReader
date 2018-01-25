package com.example.materialdesignapp.ui;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.example.materialdesignapp.R;
import com.example.materialdesignapp.data.ArticleLoader;
import com.example.materialdesignapp.data.Tale;
import com.example.materialdesignapp.data.TalePager;
import android.support.v4.app.Fragment;

public class ActivityTaleDetail extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    public final static String EXTRA_TALE_ID = "EXTRA_TALE_ID";
    public final static String BUNDLE_TALE = "BUNDLE_TALE";

    private long mTaleId;
    private Tale mTale;
    private ViewPager mViewPager;
    private TextView mPageTextView;
    private PageAdapter mAdapter;
    private TalePager mTalePager;
    private Toolbar mToolbar;

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
        }

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.GONE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPageTextView = findViewById(R.id.tv_page);
        mViewPager = findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(this);
        mAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);


        mPageTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onGlobalLayout() {
                mPageTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mTale == null) {
                    getLoaderManager().initLoader(0, null, ActivityTaleDetail.this);
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
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            mToolbar.setVisibility(View.GONE);
        } else {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(this, mTaleId);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            cursor.moveToFirst();

            mTale = new Tale(cursor);

            onTaleLoaded();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
            }
        }.execute();
    }

    class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mTalePager == null ? 0 : mTalePager.getPageCount();
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return FragmentTaleCover.getFragment(mTale);
            } else {
                return FragmentTalePage.getFragment(mTalePager.getPage(position - 1), mTalePager.getPageProperties());
            }
        }
    }
}
