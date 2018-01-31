package com.example.xyzreader.ui.screens.taleDetail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xyzreader.R;
import com.example.xyzreader.data.loaders.ArticleLoader;
import com.example.xyzreader.data.models.Tale;
import com.example.xyzreader.data.providers.FileContract;
import com.example.xyzreader.ui.screens.taleDetail.fragments.FragmentTaleCover;
import com.example.xyzreader.ui.screens.taleDetail.fragments.FragmentTalePage;
import com.example.xyzreader.utils.TalePager;
import com.example.xyzreader.data.models.TaleProgress;
import com.example.xyzreader.data.providers.TaleProgressContract.TaleProgressEntry;
import com.example.xyzreader.data.loaders.TaleProgressLoader;
import com.example.xyzreader.utils.ViewUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ActivityTaleDetail extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        FragmentTaleCover.FragmentCoverEvent,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int TALE_START_INDEX = -1;
    private static final int TALE_COVER_OFFSET = 1;

    private final static int LOADER_TALE = 1000;
    private final static int LOADER_TALE_PROGRESS = 1001;

    public final static String EXTRA_TALE_ID = "EXTRA_TALE_ID";
    private final static String BUNDLE_TALE_PROGRESS = "BUNDLE_TALE_PROGRESS";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tale_detail);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.GONE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            mTaleId = getIntent().getLongExtra(EXTRA_TALE_ID, 0);
        }

        if (savedInstanceState != null) {
            mTaleProgress = savedInstanceState.getParcelable(BUNDLE_TALE_PROGRESS);
        }

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
            mTaleProgress.setPauseIndex(pageStartIndex);
            TaleProgressEntry.updatePage(getContentResolver(), mTaleId, mTaleProgress.getPauseIndex());
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

        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //endregion

    private void startTaleLoader() {
        Loader<Object> loader = getSupportLoaderManager().getLoader(LOADER_TALE);
        if (loader != null && !loader.isReset()) {
            getSupportLoaderManager().restartLoader(LOADER_TALE, null, ActivityTaleDetail.this);
        } else {
            getSupportLoaderManager().initLoader(LOADER_TALE, null, ActivityTaleDetail.this);
        }
    }

    private void startTaleProgressLoader() {
        if (getSupportLoaderManager().getLoader(LOADER_TALE_PROGRESS) != null) {
            getSupportLoaderManager().restartLoader(LOADER_TALE_PROGRESS, null, ActivityTaleDetail.this);
        } else {
            getSupportLoaderManager().initLoader(LOADER_TALE_PROGRESS, null, ActivityTaleDetail.this);
        }
    }

    private void onTaleProgressLoaded() {
        if (mTaleProgress.getPauseIndex() == TALE_START_INDEX) {
            mViewPager.setCurrentItem(0, false);
        } else {
            int page = 0;
            for (int i = 0; i < mTalePager.getPageCount(); i++) {
                int startIndex = mTalePager.getPage(i).getStartIndex();
                int endIndexIndex = mTalePager.getPage(i).getEndIndex();
                if (mTaleProgress.getPauseIndex() >= startIndex && mTaleProgress.getPauseIndex() < endIndexIndex) {
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
        getSupportActionBar().setTitle(mTale.getTitle());
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

    @Override
    public void onShareTap(FragmentTaleCover sender) {
        try {
            File tale = generateTaleFile(mTale.getTitle(), mTale.getBody());

            Uri taleUri = FileProvider.getUriForFile(this, FileContract.AUTHORITY, tale);

            Intent sharingIntent = new Intent(Intent.ACTION_VIEW);
            sharingIntent.setDataAndType(taleUri, getContentResolver().getType(taleUri));
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (sharingIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(sharingIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.share_error, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private File generateTaleFile(String taleName, String text) throws IOException {
        File cacheTalesDir = new File(getCacheDir() + "/" + FileContract.CacheFiles.PATH_TALES);

        if (!cacheTalesDir.exists()) {
            cacheTalesDir.mkdir();
        }

        File tale = new File(cacheTalesDir.getAbsolutePath() + "/" + taleName + ".txt");
        if (!tale.exists()) {
            tale.createNewFile();
            tale.deleteOnExit();

            FileOutputStream fOut = new FileOutputStream(tale);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(text);
            myOutWriter.close();
        }

        return tale;
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
            return mTalePager == null || !mTalePager.isTextProcessed() ? 0 : mTalePager.getPageCount() + TALE_COVER_OFFSET;
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                mTaleCoverFragment = FragmentTaleCover.getFragment(mTale);
                mTaleCoverFragment.setEventListener(ActivityTaleDetail.this);
                return mTaleCoverFragment;
            } else {
                return FragmentTalePage.getFragment(mTalePager.getPage(position - TALE_COVER_OFFSET), mTalePager.getPageProperties());
            }
        }
    }
}
