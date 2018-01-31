package com.example.xyzreader.ui.screens.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.loaders.ArticleLoader;
import com.example.xyzreader.data.models.Tale;
import com.example.xyzreader.data.services.UpdaterService;
import com.example.xyzreader.ui.screens.taleDetail.ActivityTaleDetail;
import com.example.xyzreader.ui.customViews.ImageLoader;
import com.example.xyzreader.utils.ViewUtil;

import net.hockeyapp.android.CrashManager;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleListActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ArticleListActivity.class.toString();
    private static final String BUNDLE_SCROLL_POSITION = "BUNDLE_SCROLL_POSITION";


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private int mScrollPosition = 0;
    private boolean mIsRefreshing = false;
    private boolean mIsAnimationShown = false;

    @Override
    protected void onResume() {
        super.onResume();

        CrashManager.register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setVisibility(View.INVISIBLE);

        getSupportLoaderManager().initLoader(0, null, this);

        if (savedInstanceState != null) {
            mScrollPosition = savedInstanceState.getInt(BUNDLE_SCROLL_POSITION);
        }
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mRecyclerView != null) {
            GridLayoutManager glm = (GridLayoutManager) mRecyclerView.getLayoutManager();

            if (glm == null) {
                return;
            }

            int itemPosition = glm.findFirstCompletelyVisibleItemPosition();
            outState.putInt(BUNDLE_SCROLL_POSITION, itemPosition);
        }
    }

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                String error = intent.getStringExtra(UpdaterService.EXTRA_ERROR);

                if (error != null) {
                    Snackbar.make(ArticleListActivity.this.findViewById(android.R.id.content), error, Snackbar.LENGTH_SHORT).show();
                }

                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Adapter adapter = new Adapter(this, cursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        GridLayoutManager glm =
                new GridLayoutManager(this, getResources().getInteger(R.integer.article_list_column_count));
        mRecyclerView.setLayoutManager(glm);

        if (cursor == null || cursor.getCount() == 0) {
            refresh();
        } else {
            if (!mIsAnimationShown) {
                mIsAnimationShown = true;
                ViewUtil.onLoad(mRecyclerView, new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mRecyclerView.scrollToPosition(mScrollPosition);
                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int offset = metrics.heightPixels / 2;
                        for (int i = 0, cnt = 0; i < mRecyclerView.getChildCount(); i++, cnt++) {
                            View view = mRecyclerView.getChildAt(i);
                            view.setTranslationY(offset + cnt * 100);
                            view.setAlpha(0);

                            view.animate()
                                    .translationY(0)
                                    .alpha(1)
                                    .setDuration(850)
                                    .setInterpolator(ViewUtil.getAccelerateInterpolator(ArticleListActivity.this))
                                    .start();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private final Context mContext;
        private Cursor mCursor;

        public Adapter(Context context, Cursor cursor) {
            mCursor = cursor;
            mContext = context;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ArticleListActivity.this, ActivityTaleDetail.class);
                    if (mCursor.moveToPosition(vh.getAdapterPosition())) {
                        intent.putExtra(ActivityTaleDetail.EXTRA_TALE_ID, mCursor.getLong(ArticleLoader.Query._ID));
                        startActivity(intent);
                    }

                }
            });
            return vh;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            Tale tale = new Tale(mCursor);
            holder.titleView.setText(tale.getTitle());
            holder.subtitleView.setText(tale.getDate() + " " + getString(R.string.by) + " " + tale.getAuthor());
            holder.thumbnailView.initialize(tale.getPhoto());
        }

        @Override
        public int getItemCount() {
            if (mCursor == null) return 0;
            return mCursor.getCount();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageLoader thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = view.findViewById(R.id.thumbnail);
            titleView = view.findViewById(R.id.article_title);
            subtitleView = view.findViewById(R.id.article_subtitle);
        }
    }
}
