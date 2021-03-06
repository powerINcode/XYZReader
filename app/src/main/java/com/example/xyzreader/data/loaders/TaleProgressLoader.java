package com.example.xyzreader.data.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.example.xyzreader.data.providers.TaleProgressContract;

/**
 * Helper for loading a list of articles or a single article.
 */
public class TaleProgressLoader extends CursorLoader {
    public static TaleProgressLoader newInstanceForTaleId(Context context, long taleId) {
        return new TaleProgressLoader(context, TaleProgressContract.TaleProgressEntry.buildTaleProgressUri(taleId));
    }

    private TaleProgressLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, null);
    }

    public interface Query {
        String[] PROJECTION = {
                TaleProgressContract.TaleProgressEntry._ID,
                TaleProgressContract.TaleProgressEntry.TALE_ID,
                TaleProgressContract.TaleProgressEntry.PAUSE_INDEX
        };
    }
}
