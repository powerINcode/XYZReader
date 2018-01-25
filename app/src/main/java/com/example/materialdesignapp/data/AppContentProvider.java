
package com.example.materialdesignapp.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class AppContentProvider extends ContentProvider {
    private SQLiteOpenHelper mOpenHelper;

    public static final String CONTENT_AUTHORITY = "com.example.materialdesignapp";
    public static final Uri BASE_URI = Uri.parse("content://" + AppContentProvider.CONTENT_AUTHORITY);

    interface Tables {
        String ITEMS = "items";
        String TALE_PROGRESS = TaleProgressContract.TaleProgressEntry.TABLE_NAME;
    }

    private static final int ITEMS = 0;
    private static final int ITEMS__ID = 1;

    private static final int TALE_PROGRESS_WITH_ID = 2;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, "items", ITEMS);
        matcher.addURI(CONTENT_AUTHORITY, "items/#", ITEMS__ID);
        matcher.addURI(CONTENT_AUTHORITY, TaleProgressContract.PATH_TALE_PROGRESS + "/#", TALE_PROGRESS_WITH_ID);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AppDb(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ItemsContract.Items.CONTENT_TYPE;
            case ITEMS__ID:
                return ItemsContract.Items.CONTENT_ITEM_TYPE;
            case TALE_PROGRESS_WITH_ID:
                return TaleProgressContract.TaleProgressEntry.CONTENT_URI.buildUpon().appendPath("#").build().toString();
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case ITEMS:
            case ITEMS__ID:
                final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
                final SelectionBuilder builder = buildSelection(uri);
                cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
                if (cursor != null) {
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                }
                return cursor;

            case TALE_PROGRESS_WITH_ID:
                long id = ContentUris.parseId(uri);
                SQLiteDatabase readableDatabase = mOpenHelper.getReadableDatabase();
                cursor = readableDatabase.query(Tables.TALE_PROGRESS, projection, TaleProgressContract.TaleProgressEntry._ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
                return cursor;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS: {
                final long _id = db.insertOrThrow(Tables.ITEMS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ItemsContract.Items.buildItemUri(_id);
            }
            case TALE_PROGRESS_WITH_ID:
                SQLiteDatabase writableDatabase = mOpenHelper.getWritableDatabase();
                long id = writableDatabase.insert(Tables.TALE_PROGRESS, null, values);

                if (id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);

                    return ContentUris.withAppendedId(TaleProgressContract.TaleProgressEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.sqlite.SQLiteException("Failed to insert tale progress: " + uri);
                }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
            case ITEMS__ID:
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                final SelectionBuilder builder = buildSelection(uri);
                getContext().getContentResolver().notifyChange(uri, null);
                return builder.where(selection, selectionArgs).update(db, values);
            case TALE_PROGRESS_WITH_ID:
                long id = ContentUris.parseId(uri);
                SQLiteDatabase writableDatabase = mOpenHelper.getWritableDatabase();
                return writableDatabase.update(Tables.TALE_PROGRESS, values,
                        TaleProgressContract.TaleProgressEntry.TALE_ID + "=?", new String[] { String.valueOf(id) });

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return builder.where(selection, selectionArgs).delete(db);
    }

    private SelectionBuilder buildSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        return buildSelection(uri, match, builder);
    }

    private SelectionBuilder buildSelection(Uri uri, int match, SelectionBuilder builder) {
        final List<String> paths = uri.getPathSegments();
        switch (match) {
            case ITEMS: {
                return builder.table(Tables.ITEMS);
            }
            case ITEMS__ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.ITEMS).where(ItemsContract.Items._ID + "=?", _id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }
}
