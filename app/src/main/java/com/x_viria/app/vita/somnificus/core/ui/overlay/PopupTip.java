package com.x_viria.app.vita.somnificus.core.ui.overlay;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.x_viria.app.vita.somnificus.R;

public class PopupTip {

    private final Context CONTEXT;

    public PopupTip(Context context) {
        this.CONTEXT = context;
    }

    public void show(View v, @StringRes int resId) {
        show(v, CONTEXT.getString(resId));
    }

    public void show(View v, String text) {
        LayoutInflater inflater = (LayoutInflater) CONTEXT.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_tips, null);

        ((TextView) popupView.findViewById(R.id.popup_text)).setText(text);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setAnimationStyle(R.style.PopupWindowTipsAnimation);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        LinearLayout closeButton = popupView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v1 -> popupWindow.dismiss());
    }

}
