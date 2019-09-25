package com.alexanderdeoliveira.pricebitcoin.util;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

public class UiUtil {

    public static Snackbar makeSnack(String text, View view, int textColor, int backgroundColor) {
        Snackbar snack = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        View snackView = snack.getView();
        snackView.setBackgroundColor(backgroundColor);
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(textColor);
        return snack;
    }
}
