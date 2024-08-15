package com.x_viria.app.vita.somnificus.core.ui.sdview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.icu.util.Calendar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.util.ArrayUtils;
import com.x_viria.app.vita.somnificus.util.Unit;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SDL7DGraphView extends View {

    private final int CONST__BAR_WIDTH = 16; // DP

    private final AttributeSet ATTRS;
    private final Context CONTEXT;
    private final int DEF_SA;

    private int CANVAS_WIDTH;
    private int CANVAS_HEIGHT;
    private long[] DATA;

    private String[] X_AXIS_LABEL;
    private long Y_VAL_MAX;

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
        this.CANVAS_WIDTH = canvas.getWidth();
        this.CANVAS_HEIGHT = canvas.getHeight();
        init(canvas);
        drawBar(canvas);
    }

    private int getBarCenterX(int index) {
        return (CANVAS_WIDTH / 7) * index + (CANVAS_WIDTH / 14);
    }

    public void max(long value) {
        this.Y_VAL_MAX = value;
    }

    public void set(long[] data) {
        long _max = Y_VAL_MAX;
        for (long l : data) {
            if (_max < l) _max = l;
        }
        max(_max);

        this.DATA = data;
        invalidate();
    }

    private void drawBar(Canvas canvas) {
        for (int index = 0; index < DATA.length; index++) {
            if (DATA[index] == 0) continue;

            final int bar_left = getBarCenterX(index) - (Unit.dp2px(CONTEXT, CONST__BAR_WIDTH) / 2);
            final int bar_right = getBarCenterX(index) + (Unit.dp2px(CONTEXT, CONST__BAR_WIDTH) / 2);
            final float bar_radius = Unit.dp2px(CONTEXT, 4);
            final float bar_max_height = CANVAS_HEIGHT - 88 - bar_radius - 4;
            final double p = ((double) DATA[index]) / ((double) Y_VAL_MAX);

            Path path = new Path();
            path.moveTo(
                    bar_left,
                    CANVAS_HEIGHT - 88
            );
            if (bar_max_height * p <= bar_radius) {
                path.quadTo(
                        bar_left,
                        CANVAS_HEIGHT - 88 - bar_radius,
                        bar_left + bar_radius,
                        CANVAS_HEIGHT - 88 - bar_radius
                );
                path.lineTo(
                        bar_right - bar_radius,
                        CANVAS_HEIGHT - 88 - bar_radius
                );
                path.quadTo(
                        bar_right,
                        CANVAS_HEIGHT - 88 - bar_radius,
                        bar_right,
                        CANVAS_HEIGHT - 88
                );
                path.lineTo(
                        bar_left,
                        CANVAS_HEIGHT - 88
                );
            } else {
                path.lineTo(
                        bar_left,
                        (float) (CANVAS_HEIGHT - 88 - bar_max_height * p)
                );
                path.quadTo(
                        bar_left,
                        (float) (CANVAS_HEIGHT - 88 - bar_max_height * p - bar_radius),
                        bar_left + bar_radius,
                        (float) (CANVAS_HEIGHT - 88 - bar_max_height * p - bar_radius)
                );
                path.lineTo(
                        bar_right - bar_radius,
                        (float) (CANVAS_HEIGHT - 88 - bar_max_height * p - bar_radius)
                );
                path.quadTo(
                        bar_right,
                        (float) (CANVAS_HEIGHT - 88 - bar_max_height * p - bar_radius),
                        bar_right,
                        (float) (CANVAS_HEIGHT - 88 - bar_max_height * p)
                );
                path.lineTo(
                        bar_right,
                        CANVAS_HEIGHT - 88
                );
                path.lineTo(
                        bar_left,
                        CANVAS_HEIGHT - 88
                );
            }

            Paint paint = new Paint();
            paint.setColor(CONTEXT.getColor(R.color.primaryInactiveColor));
            if (index == 6) paint.setColor(CONTEXT.getColor(R.color.primaryActiveColor));
            paint.setStyle(Paint.Style.FILL);
            invalidate();
            canvas.drawPath(path, paint);
        }
    }

    private void init(Canvas canvas) {
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
            int bar_center = getBarCenterX(i);
            if (i < X_AXIS_LABEL.length - 1) {
                canvas.drawText(X_AXIS_LABEL[i], bar_center - Unit.dp2px(CONTEXT, 6), canvas.getHeight() - 50, paint);
            } else {
                canvas.drawText(X_AXIS_LABEL[i], bar_center - Unit.dp2px(CONTEXT, 6), canvas.getHeight() - 50, paint_ac);
            }
        }
    }
}
