package com.x_viria.app.vita.somnificus.core.ui.sdview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.icu.util.Calendar;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.util.ArrayUtils;
import com.x_viria.app.vita.somnificus.util.Unit;

public class SDL7DGraphView extends View {

    private final AttributeSet ATTRS;
    private final Context CONTEXT;
    private final int DEF_SA;

    private Canvas CANVAS;

    private String[] X_AXIS_LABEL;

    public SDL7DGraphView(Context context) {
        this(context, null);
    }

    public SDL7DGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SDL7DGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ATTRS = attrs;
        this.CONTEXT = context;
        this.DEF_SA = defStyleAttr;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        this.CANVAS = canvas;
        init();
    }

    private void init() {
        X_AXIS_LABEL = new String[] {
                CONTEXT.getString(R.string.activity_set_alarm__checkbox_text_sun),
                CONTEXT.getString(R.string.activity_set_alarm__checkbox_text_mon),
                CONTEXT.getString(R.string.activity_set_alarm__checkbox_text_tue),
                CONTEXT.getString(R.string.activity_set_alarm__checkbox_text_wed),
                CONTEXT.getString(R.string.activity_set_alarm__checkbox_text_thu),
                CONTEXT.getString(R.string.activity_set_alarm__checkbox_text_fri),
                CONTEXT.getString(R.string.activity_set_alarm__checkbox_text_sat)
        };
        Calendar calendar = Calendar.getInstance();
        int dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < dayOfWeekIndex; i++) {
            X_AXIS_LABEL = ArrayUtils.shiftForward(X_AXIS_LABEL);
        }

        Paint paint = new Paint();
        paint.setColor(CONTEXT.getColor(R.color.secondaryTextColor));
        paint.setTextSize(Unit.dp2px(CONTEXT, 12));
        Paint paint_ac = new Paint();
        paint_ac.setColor(CONTEXT.getColor(R.color.secondaryActiveColor));
        paint_ac.setTextSize(Unit.dp2px(CONTEXT, 12));
        for (int i = 0; i < X_AXIS_LABEL.length; i++) {
            int bar_center = (CANVAS.getWidth() / 7) * i + (CANVAS.getWidth() / 14);
            if (i < X_AXIS_LABEL.length - 1) {
                CANVAS.drawText(X_AXIS_LABEL[i], bar_center, CANVAS.getHeight() - 50, paint);
            } else {
                CANVAS.drawText(X_AXIS_LABEL[i], bar_center, CANVAS.getHeight() - 50, paint_ac);
            }
        }

        invalidate();
    }
}
