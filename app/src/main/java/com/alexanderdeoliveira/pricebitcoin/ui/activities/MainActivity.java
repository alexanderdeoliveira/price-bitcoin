package com.alexanderdeoliveira.pricebitcoin.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexanderdeoliveira.pricebitcoin.R;
import com.alexanderdeoliveira.pricebitcoin.api.BitcoinApi;
import com.alexanderdeoliveira.pricebitcoin.model.Data;
import com.alexanderdeoliveira.pricebitcoin.persistence.dao.DataDAO;
import com.alexanderdeoliveira.pricebitcoin.util.NetworkUtil;
import com.alexanderdeoliveira.pricebitcoin.util.UiUtil;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Cartesian cartesian;
    private AnyChartView anyChartView;
    private TextView lastPrice;
    private Snackbar errorSnack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorSnack = UiUtil.makeSnack(getResources().getString(R.string.dialog_unknown_error_message), findViewById(R.id.parent_view), Color.WHITE, 0xFFFF0000);

        setChartView();
        setCardView();

        getBitcoinPriceLastYear();
    }

    private void setCardView() {
        lastPrice = findViewById(R.id.price_text);
    }

    /**
     * Set chart view using AnyChart Api. Type - Line Chart
     */
    private void setChartView() {
        anyChartView = findViewById(R.id.chart_view);
        progressBar = findViewById(R.id.progress_bar);
        anyChartView.setProgressBar(progressBar);

        cartesian = AnyChart.line();

        cartesian.animation(true);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.yAxis(0).title("USD");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
    }

    /**
     * Set chart data using data list. Format - x: 09/10 , y: 8682.60
     * @param dataList
     */
    private void setChartData(List<Data> dataList) {
        List<DataEntry> seriesData = new ArrayList<>();

        for (Data data:dataList) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM");
            String dateText = df.format(new Date(new Timestamp(data.getTime()*1000).getTime()));
            seriesData.add(new ValueDataEntry(dateText, Double.valueOf(data.getPrice())));
        }

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name(getResources().getString(R.string.chart_name));
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                getBitcoinPriceLastYear();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Data> responseToDataList(ResponseEntity<String> responseEntity) {
        List<Data> dataList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(responseEntity.getBody());
            if(jsonObject.getString("status").equals("ok")) {
                JSONArray list = jsonObject.getJSONArray("values");
                for(int i=0;i<list.length();i++)
                    dataList.add(new Data((JSONObject) list.get(i)));
            }
        }catch (JSONException err){
            Log.d("Error", err.toString());
        }

        return dataList;
    }

    /**
     * Set card last price
     * @param lastData
     */
    private void setLastPrice(Data lastData) {
        DecimalFormat df = new DecimalFormat(",##0.00");
        String dx = df.format(lastData.getPrice());
        lastPrice.setText("$" + dx);
    }

    /**
     * Load data into chart
     * @param dataList
     */
    private void loadData(List<Data> dataList) {
        if(dataList != null && dataList.size() > 0) {
            setChartData(dataList);
            setLastPrice(dataList.get(dataList.size() - 1));
        } else
            error();
    }


    private void error() {
        progressBar.setVisibility(View.GONE);
        errorSnack.show();
    }

    /**
     * Get price list of last year from blockchain server, if has internet. If not, get from database. And set card last price and chart of last year.
     */
    private void getBitcoinPriceLastYear() {
        if (NetworkUtil.connectionType(getApplicationContext()).equals("OFFLINE")) {
            (errorSnack.setText(getResources().getString(R.string.dialog_connectivity_message))).show();

            List<Data> dataList = DataDAO.getAllData(getApplicationContext());
            loadData(dataList);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        BitcoinApi.getBitcoinPriceLastYear()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            List<Data> dataList;
                            if(response.getStatusCode() == HttpStatus.OK) {
                                dataList = responseToDataList(response);
                                DataDAO.deleteData(getApplicationContext());
                                DataDAO.saveDatas(getApplicationContext(), dataList);
                            } else
                                dataList = DataDAO.getAllData(getApplicationContext());

                            loadData(dataList);

                            },
                        throwable -> {
                            error();
                        },
                        () -> {});
    }
}
