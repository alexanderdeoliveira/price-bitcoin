package com.alexanderdeoliveira.pricebitcoin.persistence.dao;

import android.content.Context;

import com.alexanderdeoliveira.pricebitcoin.model.Data;
import com.alexanderdeoliveira.pricebitcoin.persistence.datasource.DataDataSource;

import java.util.List;

public class DataDAO {

    public static List<Data> getAllData(Context context) {
        DataDataSource dataDataSource = new DataDataSource(context);
        dataDataSource.open();
        List<Data> dataList = dataDataSource.getAllData();
        dataDataSource.close();
        return dataList;
    }

    public static void saveDatas(Context context, List<Data> datas) {
        if (datas != null && datas.size() > 0) {
            DataDataSource dataDataSource = new DataDataSource(context);
            dataDataSource.open();
            for (Data data : datas)
                dataDataSource.saveData(data);

            dataDataSource.close();
        }
    }

    public static void deleteData(Context context) {
        DataDataSource dataDataSource = new DataDataSource(context);
        dataDataSource.deleteAll();
    }
}
