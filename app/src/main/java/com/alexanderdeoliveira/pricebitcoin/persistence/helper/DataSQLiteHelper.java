package com.alexanderdeoliveira.pricebitcoin.persistence.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_DATA = "datas";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TIME = "time";
    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_DATA + "("
            + COLUMN_TIME + " integer primary key, "
            + COLUMN_PRICE + " integer);";


    public DataSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        onCreate(db);
    }
}
