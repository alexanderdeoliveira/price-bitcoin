package com.alexanderdeoliveira.pricebitcoin.persistence.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.alexanderdeoliveira.pricebitcoin.model.Data;
import com.alexanderdeoliveira.pricebitcoin.persistence.helper.DataSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class DataDataSource {
    private SQLiteDatabase database;
    private DataSQLiteHelper dbHelper;
    private String[] allColumns = {
            DataSQLiteHelper.COLUMN_TIME,
            DataSQLiteHelper.COLUMN_PRICE
    };

    public DataDataSource(Context context) {
        dbHelper = new DataSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public void saveData(Data data) {
        ContentValues values = new ContentValues();
        values.put(DataSQLiteHelper.COLUMN_TIME, data.getTime());
        values.put(DataSQLiteHelper.COLUMN_PRICE, data.getPrice());

        database.insert(DataSQLiteHelper.TABLE_DATA, null, values);
    }

    public void deleteAll(){
        open();
        database.execSQL("delete from " + dbHelper.TABLE_DATA);
        close();
    }

    public List<Data> getAllData() {
        List<Data> dataList = new ArrayList<>();
        Cursor cursor = database.query(DataSQLiteHelper.TABLE_DATA, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Data data = cursorToDta(cursor);
            dataList.add(data);
            cursor.moveToNext();
        }
        cursor.close();
        return dataList;
    }

    private Data cursorToDta(Cursor cursor) {
        Data data = new Data();
        data.setPrice(cursor.getDouble(cursor.getColumnIndex(DataSQLiteHelper.COLUMN_PRICE)));
        data.setTime(cursor.getLong(cursor.getColumnIndex(DataSQLiteHelper.COLUMN_TIME)));

        return data;
    }
}
