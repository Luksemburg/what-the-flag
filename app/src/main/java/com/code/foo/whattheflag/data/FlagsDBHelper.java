package com.code.foo.whattheflag.data;

import android.content.Context;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.code.foo.whattheflag.data.FlagsArraysContract.AppendFlag;

import static com.code.foo.whattheflag.data.FlagsArraysContract.AppendFlag.TABLE_NAME;

public class FlagsDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = FlagsDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "flags.db";
    private static final int DATABASE_VERSION = 1;

    public FlagsDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        String SQL_CREATE_FLAGS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + AppendFlag._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AppendFlag.COLUMN_COUNTRY + " TEXT NOT NULL, "
                + AppendFlag.CODE + " TEXT NOT NULL, "
                + AppendFlag.COLUMN_ARRAY + " TEXT NOT NULL); ";

        db.execSQL(SQL_CREATE_FLAGS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " до версии " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

}
