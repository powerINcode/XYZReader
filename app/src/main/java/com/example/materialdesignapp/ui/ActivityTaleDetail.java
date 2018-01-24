package com.example.materialdesignapp.ui;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.materialdesignapp.R;
import com.example.materialdesignapp.data.Tale;
import com.example.materialdesignapp.data.TalePager;
import android.support.v4.app.Fragment;

public class ActivityTaleDetail extends AppCompatActivity {

    public final static String BUNDLE_TALE = "BUNDLE_TALE";
    private Tale mTale;
    private ViewPager mViewPager;
    private TextView mPageTextView;
    private PageAdapter mAdapter;
    private TalePager mTalePager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tale_detail);

        if (getIntent() != null) {
            mTale = getIntent().getParcelableExtra(BUNDLE_TALE);
        }

        mPageTextView = findViewById(R.id.tv_page);
        mViewPager = findViewById(R.id.pager);
        mAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);


        mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onGlobalLayout() {
                mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mTalePager = new TalePager(mPageTextView, mTale.getTale(), (getResources().getDimension(R.dimen.tale_text_size) / getResources().getDisplayMetrics().density));
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
        });
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
