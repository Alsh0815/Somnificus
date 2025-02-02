package com.x_viria.app.vita.somnificus.util;

import android.content.Context;
import android.util.TypedValue;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

public class Theme {

    public static void apply(Context context) {
        try {
            int style_id = new SPStorage(context).getInt(Config.KEY__SETTINGS_APP_THEME, R.style.Theme_Somnificus);
            context.setTheme(style_id);
        } catch (Exception e) {
            context.setTheme(R.style.Theme_Somnificus);
        }
    }

    public static int getColor(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

}
