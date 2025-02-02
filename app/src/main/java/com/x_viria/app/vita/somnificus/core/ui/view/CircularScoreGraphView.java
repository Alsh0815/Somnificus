package com.x_viria.app.vita.somnificus.core.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.x_viria.app.vita.somnificus.R;

public class CircularScoreGraphView extends View {

    private Paint BG_PAINT;
    private Paint CIRCLE_PAINT;
    private Paint TEXT_PAINT;
    private int SCORE = 60;
    private int FG_COLOR = Color.rgb(0, 0, 255);
    private boolean FLAG_GRADIENT = false;

    public CircularScoreGraphView(Context context, AttributeSet attr) {
        super(context, attr);
        init(attr);
    }

    public CircularScoreGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircularScoreGraphView);
        int textColor = a.getColor(R.styleable.CircularScoreGraphView_textColor, Color.BLACK);
        FG_COLOR = a.getColor(R.styleable.CircularScoreGraphView_color, FG_COLOR);
        SCORE = a.getInteger(R.styleable.CircularScoreGraphView_score, SCORE);
        FLAG_GRADIENT = a.getBoolean(R.styleable.CircularScoreGraphView_enableGradient, FLAG_GRADIENT);
        a.recycle();

        BG_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
        BG_PAINT.setStyle(Paint.Style.STROKE);
        BG_PAINT.setStrokeWidth(20f);

        CIRCLE_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
        CIRCLE_PAINT.setStyle(Paint.Style.STROKE);
        CIRCLE_PAINT.setStrokeWidth(20f);
        CIRCLE_PAINT.setColor(FG_COLOR);

        TEXT_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
        TEXT_PAINT.setColor(textColor);
        TEXT_PAINT.setTextSize(80f);
        TEXT_PAINT.setTextAlign(Paint.Align.CENTER);

        setScore(SCORE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2f - 20f;
        float cx = width / 2f;
        float cy = height / 2f;

        int r = Color.red(FG_COLOR);
        int g = Color.green(FG_COLOR);
        int b = Color.blue(FG_COLOR);

        if (FLAG_GRADIENT) {
            LinearGradient gradient = new LinearGradient(
                    0, 0, width, height,
                    FG_COLOR,
                    Color.rgb(
                            r > 127 ? r - 96 : r + 96,
                            g > 127 ? g - 96 : g + 96,
                            b > 127 ? b - 96 : b + 96
                    ),
                    Shader.TileMode.CLAMP
            );
            BG_PAINT.setShader(gradient);
            BG_PAINT.setAlpha(64);
            CIRCLE_PAINT.setShader(gradient);
        } else {
            BG_PAINT.setShader(null);
            BG_PAINT.setColor(FG_COLOR);
            BG_PAINT.setAlpha(64);
            CIRCLE_PAINT.setShader(null);
            CIRCLE_PAINT.setColor(FG_COLOR);
        }

        RectF rectF = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);
        canvas.drawArc(rectF, -90, 360f, false, BG_PAINT);
        canvas.drawArc(rectF, -90, (SCORE / 100f) * 360f, false, CIRCLE_PAINT);

        canvas.drawText(String.valueOf(SCORE), cx, cy + (TEXT_PAINT.getTextSize() / 3f), TEXT_PAINT);
    }

    public void setBackgroundColor(int color) {
        this.FG_COLOR = color;
        CIRCLE_PAINT.setColor(color);
        invalidate();
    }

    public void setGradient(boolean gradient) {
        this.FLAG_GRADIENT = gradient;
        invalidate();
    }

    public void setScore(int score) {
        ValueAnimator animator = ValueAnimator.ofInt(SCORE, score);
        animator.setDuration(500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            SCORE = (int) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public void setTextColor(int color) {
        TEXT_PAINT.setColor(color);
        invalidate();
    }

}
