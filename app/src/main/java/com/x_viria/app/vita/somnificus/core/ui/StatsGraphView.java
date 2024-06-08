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
import com.x_viria.app.vita.somnificus.util.BitmapUtils;
import com.x_viria.app.vita.somnificus.util.Unit;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class StatsGraphView extends View {

    private Context CONTEXT;

    private int GRAPH_AREA_LEFT_LIMIT = 100;
    private int GRAPH_AREA_RIGHT_LIMIT = 0;
    private int GRAPH_AREA_X = 0;
    private int GRAPH_WIDTH = 24;
    private int SC_LIMIT_LINE_TOP = 100;
    private int SC_LIMIT_LINE_BOTTOM = -100;

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

        BiFunction<Float, Float, Integer> isGraphClicked = (x, y) -> {
            float x1 = _get_graph_bar_x_pos(1) + _get_graph_bar_left_padding();
            float x2 = _get_graph_bar_x_pos(2) + _get_graph_bar_left_padding();
            float x3 = _get_graph_bar_x_pos(3) + _get_graph_bar_left_padding();
            float x4 = _get_graph_bar_x_pos(4) + _get_graph_bar_left_padding();
            float x5 = _get_graph_bar_x_pos(5) + _get_graph_bar_left_padding();
            float x6 = _get_graph_bar_x_pos(6) + _get_graph_bar_left_padding();
            float x7 = _get_graph_bar_x_pos(7) + _get_graph_bar_left_padding();
            int halfWidth = GRAPH_WIDTH / 2;

            if (SC_LIMIT_LINE_TOP <= y && y <= (getHeight() + SC_LIMIT_LINE_BOTTOM)) {
                if (x1 - halfWidth < x && x < x1 + halfWidth) {
                    return 1;
                } if (x2 - halfWidth < x && x < x2 + halfWidth) {
                    return 2;
                } if (x3 - halfWidth < x && x < x3 + halfWidth) {
                    return 3;
                } if (x4 - halfWidth < x && x < x4 + halfWidth) {
                    return 4;
                } if (x5 - halfWidth < x && x < x5 + halfWidth) {
                    return 5;
                } if (x6 - halfWidth < x && x < x6 + halfWidth) {
                    return 6;
                } if (x7 - halfWidth < x && x < x7 + halfWidth) {
                    return 7;
                }
            }
            return -1;
        };

        Log.d("StatsGraphView", "graph ID -> " + isGraphClicked.apply(event.getX(), event.getY()));

        return false;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        init(canvas);
    }

    private float _get_graph_bar_x_pos(int graph_n) {
        return (GRAPH_AREA_X / 7.0f * (graph_n - 1)) + GRAPH_AREA_LEFT_LIMIT;
    }

    private float _get_graph_bar_left_padding() {
        return (_get_graph_bar_x_pos(2) - _get_graph_bar_x_pos(1)) / 2.0f;
    }

    private void drawDofW(Canvas canvas, int graph_n, @ColorRes int color) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, graph_n - 8);
        float x = _get_graph_bar_x_pos(graph_n);
        Paint paint1 = new Paint();
        paint1.setColor(CONTEXT.getColor(color));
        canvas.drawRect(
                x - 1,
                getHeight() + SC_LIMIT_LINE_BOTTOM,
                x + 1,
                getHeight() + SC_LIMIT_LINE_BOTTOM + 8,
                paint1
        );
        String dofw = DOFW_LIST[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        Rect rect = new Rect();
        Paint paint2 = new Paint();
        paint2.setColor(CONTEXT.getColor(color));
        paint2.setTextSize(Unit.dp2px(CONTEXT, 12));
        paint2.getTextBounds(dofw, 0, dofw.length(), rect);
        canvas.drawText(
                dofw,
                x - (rect.width() / 2.0f),
                rect.height() + getHeight() + SC_LIMIT_LINE_BOTTOM + 16,
                paint2
        );
    }

    private void drawTLBar(Canvas canvas, int graph_n, int top, int bottom, @ColorRes int color) {
        float x = _get_graph_bar_x_pos(graph_n) + _get_graph_bar_left_padding();
        Paint paint = new Paint();
        paint.setColor(CONTEXT.getColor(color));
        canvas.drawRect(
                x - (GRAPH_WIDTH / 2),
                SC_LIMIT_LINE_TOP + top,
                x + (GRAPH_WIDTH / 2),
                getHeight() + SC_LIMIT_LINE_BOTTOM - bottom,
                paint
        );
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
        GRAPH_AREA_RIGHT_LIMIT = getWidth() - 48;
        GRAPH_AREA_X = GRAPH_AREA_RIGHT_LIMIT - GRAPH_AREA_LEFT_LIMIT;
        for (int n = 1; n < 8; n++) {
            drawDofW(canvas, n, R.color.primaryInactiveColor);
            drawTLBar(canvas, n, 0, 0, R.color.tertiaryBackgroundColor);
        }
        drawDofW(canvas, 8, R.color.primaryInactiveColor);
        int iconSize = Unit.dp2px(CONTEXT, 24);
        BitmapUtils bitmapUtils = new BitmapUtils(CONTEXT);
        Paint pColorFLine = new Paint();
        pColorFLine.setColor(CONTEXT.getColor(R.color.primaryInactiveColor));
        pColorFLine.setStrokeWidth(2.0f);
        Bitmap bitmapAlarmIC = bitmapUtils.getBitmapFromXML(R.drawable.ic_menu_alarm, iconSize, iconSize);
        bitmapAlarmIC = bitmapUtils.setColor(bitmapAlarmIC, R.color.primaryInactiveColor);
        Bitmap bitmapBedIC = bitmapUtils.getBitmapFromXML(R.drawable.ic_menu_bed, iconSize, iconSize);
        bitmapBedIC = bitmapUtils.setColor(bitmapBedIC, R.color.primaryInactiveColor);
        canvas.drawLine(
                GRAPH_AREA_LEFT_LIMIT,
                SC_LIMIT_LINE_TOP,
                GRAPH_AREA_RIGHT_LIMIT,
                SC_LIMIT_LINE_TOP,
                pColorFLine
        );
        canvas.drawLine(
                GRAPH_AREA_LEFT_LIMIT,
                getHeight() + SC_LIMIT_LINE_BOTTOM,
                GRAPH_AREA_RIGHT_LIMIT,
                getHeight() + SC_LIMIT_LINE_BOTTOM,
                pColorFLine
        );
        canvas.drawBitmap(
                bitmapBedIC,
                (GRAPH_AREA_LEFT_LIMIT / 2) - (bitmapBedIC.getWidth() / 2),
                SC_LIMIT_LINE_TOP - (bitmapBedIC.getHeight() / 2),
                pColorFLine
        );
        canvas.drawBitmap(
                bitmapAlarmIC,
                (GRAPH_AREA_LEFT_LIMIT / 2) - (bitmapAlarmIC.getWidth() / 2),
                getHeight() + SC_LIMIT_LINE_BOTTOM - (bitmapAlarmIC.getHeight() / 2),
                pColorFLine
        );
        invalidate();
    }

}
