package com.example.prophet.Database;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProphetContract {
    public static final String AUTHORITIES = "com.example.prophet";
    public static final String FRIENDS_PATH = "friends";
    public static final String MESSAGES_PATH = "messages";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITIES);

    public static final class Friends implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, FRIENDS_PATH);
        public static final String TABLE_NAME = "friends";
        public static final String COLUMN_EMAIL = "_email";
        public static final String COLUMN_IMAGE = "img";
        public static final String COLUMN_NAME = "name";
    }

    public static final class Messages implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, MESSAGES_PATH);
        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_EMAIL = "_email";
        public static final String COLUMN_MSG = "message";
    }
}
