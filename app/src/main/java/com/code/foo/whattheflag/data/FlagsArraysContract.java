package com.code.foo.whattheflag.data;

import android.provider.BaseColumns;

public final class FlagsArraysContract {

    private FlagsArraysContract(){

    };


    public static final class AppendFlag implements BaseColumns{

        public final static String TABLE_NAME = "flags";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_COUNTRY = "country";
        public final static String COLUMN_ARRAY = "array";
        public final static String CODE = "code";
    }

}
