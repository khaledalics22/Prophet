package com.example.prophet.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProphetProvider extends ContentProvider {
    private ProphetDbHelper mProphetDbHelper;
    private static final int FRIENDS_TABLE = 200;
    private static final int FRIENDS_ROW = 201;
    private static final int MESSAGES_TABLE = 202;
    private static final int MESSAGES_ROW = 203;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProphetContract.AUTHORITIES, ProphetContract.FRIENDS_PATH, FRIENDS_TABLE);
        sUriMatcher.addURI(ProphetContract.AUTHORITIES, ProphetContract.FRIENDS_PATH + "/#", FRIENDS_ROW);
        sUriMatcher.addURI(ProphetContract.AUTHORITIES, ProphetContract.MESSAGES_PATH, MESSAGES_TABLE);
        sUriMatcher.addURI(ProphetContract.AUTHORITIES, ProphetContract.MESSAGES_PATH + "/#", FRIENDS_ROW);
    }

    @Override
    public boolean onCreate() {
        mProphetDbHelper = new ProphetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mProphetDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case FRIENDS_TABLE:
                cursor = db.query(ProphetContract.Friends.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FRIENDS_ROW:
                selection = ProphetContract.Friends.COLUMN_EMAIL + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProphetContract.Friends.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MESSAGES_TABLE:
                cursor = db.query(ProphetContract.Messages.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MESSAGES_ROW:
                selection = ProphetContract.Messages.COLUMN_EMAIL + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProphetContract.Messages.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Query: invalid argument for uri" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mProphetDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long id;
        switch (match) {
            case FRIENDS_TABLE:
                id = db.insert(ProphetContract.Friends.TABLE_NAME, null, values);
                break;
            case MESSAGES_TABLE:
                id = db.insert(ProphetContract.Messages.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Insert: invalid argument for uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mProphetDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rows;
        switch (match) {
            case FRIENDS_TABLE:
                rows = db.delete(ProphetContract.Friends.TABLE_NAME, selection, selectionArgs);
                break;
            case FRIENDS_ROW:
                selection = ProphetContract.Friends.COLUMN_EMAIL + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db.delete(ProphetContract.Friends.TABLE_NAME, selection, selectionArgs);
                break;
            case MESSAGES_TABLE:
                rows = db.delete(ProphetContract.Messages.TABLE_NAME, selection, selectionArgs);
                break;
            case MESSAGES_ROW:
                selection = ProphetContract.Messages.COLUMN_EMAIL + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db.delete(ProphetContract.Messages.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete: invalid argument for uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mProphetDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rows;
        switch (match) {
            case FRIENDS_TABLE:
                rows = db.update(ProphetContract.Friends.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FRIENDS_ROW:
                selection = ProphetContract.Friends.COLUMN_EMAIL + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db.update(ProphetContract.Friends.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MESSAGES_TABLE:
                rows = db.update(ProphetContract.Messages.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MESSAGES_ROW:
                selection = ProphetContract.Messages.COLUMN_EMAIL + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db.update(ProphetContract.Messages.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update: invalid argument for uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }
}
