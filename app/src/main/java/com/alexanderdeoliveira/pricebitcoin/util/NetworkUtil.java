package com.alexanderdeoliveira.pricebitcoin.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkUtil {

    public static String OFFLINE = "OFFLINE";
    public static String MOBILE = "MOBILE";
    public static String WIFI = "WIFI";

    /**
     * Returns the type of connection being used by the device. If no connection exists 'OFFLINE' is returned.
     *
     * @return Connection Type
     */
    public static String connectionType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return "WIFI";
            } else {
                return "MOBILE";
            }
        } else {
            return "OFFLINE";
        }
    }
}
