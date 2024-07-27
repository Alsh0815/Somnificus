package com.x_viria.app.vita.somnificus.core.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.util.ArrayUtils;
import com.x_viria.app.vita.somnificus.util.BitmapUtils;
import com.x_viria.app.vita.somnificus.util.Unit;

import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class StatsGraphView extends View {

    private Context CONTEXT;

    private String[] DOFW_LIST;

    public StatsGraphView(Context context) {
        super(context);
        this.CONTEXT = context;
    }

    public StatsGraphView(Context context, AttributeSet attr) {
        super(context, attr);
        this.CONTEXT = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        return false;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        init(canvas);
    }

    private void init(Canvas canvas) {
        DOFW_LIST = new String[] {
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
            DOFW_LIST = ArrayUtils.shiftForward(DOFW_LIST);
        }

        int GRAPH_BAR_WIDTH = (canvas.getWidth() / 7) - Unit.dp2px(CONTEXT, 4);
        Paint paint = new Paint();
        paint.setColor(CONTEXT.getColor(R.color.primaryTextColor));
        paint.setTextSize(Unit.dp2px(CONTEXT, 12));
        for (int i = 0; i < DOFW_LIST.length; i++) {
            int bar_center = (canvas.getWidth() / 7) * i + (canvas.getWidth() / 14);
            canvas.drawText(DOFW_LIST[i], bar_center, canvas.getHeight() - 50, paint);
        }

        invalidate();
    }

}
