package com.x_viria.app.vita.somnificus.util;

import android.content.Context;

public class Unit {

    public static int dp2px(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

}
