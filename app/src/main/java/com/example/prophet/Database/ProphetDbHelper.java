package com.example.prophet.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ProphetDbHelper extends SQLiteOpenHelper {
    public static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "prophet.db";
    public static final String CREATE_FRIENDS_TABLE = "CREATE TABLE " + ProphetContract.Friends.TABLE_NAME +
            "( " + ProphetContract.Friends.COLUMN_EMAIL + "TEXT PRIMARY KEY" +
            ", " + ProphetContract.Friends.COLUMN_NAME + "TEXT NOT NULL" +
            ", " + ProphetContract.Friends.COLUMN_IMAGE + "BLOB );";

    public static final String CREATE_MESSAGES_TABLE = "CREATE TABLE " + ProphetContract.Messages.TABLE_NAME +
            "( " + ProphetContract.Messages.COLUMN_EMAIL + "TEXT NOT NULL " +
            ", " + ProphetContract.Messages.COLUMN_MSG + "TEXT NOT NULL " +
            ", FOREIGN KEY (" + ProphetContract.Messages.COLUMN_EMAIL + ") REFERENCES " + ProphetContract.Friends.TABLE_NAME + "(" +
            ProphetContract.Friends.COLUMN_EMAIL + "));";

    public static final String DROP_TABLE_FRIENDS = "DROP TABLE IF EXISTS " + ProphetContract.Friends.TABLE_NAME;
    public static final String DROP_TABLES_MESSAGES = "DROP TABLE IF EXISTS " + ProphetContract.Messages.TABLE_NAME;

    public ProphetDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FRIENDS_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_FRIENDS);
        db.execSQL(DROP_TABLES_MESSAGES);
        ++DATABASE_VERSION;
        onCreate(db);
    }
}
