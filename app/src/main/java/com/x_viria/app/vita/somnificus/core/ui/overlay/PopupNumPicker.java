package com.x_viria.app.vita.somnificus.core.ui.overlay;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;

public class PopupNumPicker {

    private final Context CONTEXT;
    private OnClickListener N_LISTENER = null;
    private OnClickListener P_LISTENER = null;
    private int MIN = 0;
    private int MAX = 120;

    public PopupNumPicker(Context context) {
        this.CONTEXT = context;
    }

    public void setNegativeButton(OnClickListener listener) {
        this.N_LISTENER = listener;
    }

    public void setPositiveButton(OnClickListener listener) {
        this.P_LISTENER = listener;
    }

    public void setMinimum(int value) {
        this.MIN = value;
    }

    public void setMaximum(int value) {
        this.MAX = value;
    }

    public void show(View v) {
        LayoutInflater inflater = (LayoutInflater) CONTEXT.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_num_picker, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setAnimationStyle(R.style.PopupWindowTipsAnimation);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        NumberPicker numberPicker = popupView.findViewById(R.id.popup_numpicker);
        numberPicker.setMinValue(MIN);
        numberPicker.setMaxValue(MAX);

        TextView closeButton = popupView.findViewById(R.id.popup_close);
        closeButton.setOnClickListener(v1 -> {
            if (N_LISTENER != null) N_LISTENER.onClick(-1);
            popupWindow.dismiss();
        });

        TextView okButton = popupView.findViewById(R.id.popup_ok);
        okButton.setOnClickListener(v1 -> {
            if (P_LISTENER != null) P_LISTENER.onClick(numberPicker.getValue());
            popupWindow.dismiss();
        });
    }

    public static class OnClickListener {
        interface Listener {
            public void onClick(int n);
        }

        public void onClick(int n) {}
    }

}
