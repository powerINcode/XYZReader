package com.example.xyzreader.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by powerman23rus on 25.01.2018.
 * Enjoy ;)
 */

public class TaleProgressContract {
    public final static String PATH_TALE_PROGRESS = "TALE_PROGRESS";

    public static class TaleProgressEntry implements BaseColumns {
        public final static Uri CONTENT_URI = AppContentProvider.BASE_URI.buildUpon().appendPath(PATH_TALE_PROGRESS).build();
        public final static String TABLE_NAME = "tale_progress";

        /** Matches: /tale_progress/[tale_id]/ */
        public static Uri buildTaleProgressUri(long taleId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(taleId)).build();
        }

        public final static int COLUMN_ID_INDEX = 0;
        public final static int COLUMN_TALE_ID_INDEX = 1;
        public final static int COLUMN_PAUSE_INDEX = 2;

        public final static String TALE_ID = "tale_id";
        public final static String PAUSE_INDEX = "pause_index";

        public static long create(ContentResolver contentResolver, long taleId) {
            ContentValues cv = new ContentValues();
            cv.put(TALE_ID, taleId);
            Uri insertUri = contentResolver.insert(buildTaleProgressUri(taleId), cv);

            return ContentUris.parseId(insertUri);
        }

        public static boolean updatePage(ContentResolver cr, long taleId, int page) {
            ContentValues cv = new ContentValues();
            cv.put(TaleProgressEntry.PAUSE_INDEX, page);

            int count = cr.update(buildTaleProgressUri(taleId), cv, null, null);
            return count > 0;
        }
    }
}
