package com.alexanderdeoliveira.pricebitcoin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    @JsonProperty("x")
    long time;

    @JsonProperty("y")
    double price;

    public Data(JSONObject json) {
        try {
            this.time = json.getLong("x");
            DecimalFormat formato = new DecimalFormat("#.##");
            this.price = Double.valueOf(formato.format(json.getDouble("y")).replace(",", "."));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Data() {}

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
