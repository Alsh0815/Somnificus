package com.x_viria.app.vita.somnificus.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.content.res.AppCompatResources;

import com.x_viria.app.vita.somnificus.R;

public class BitmapUtils {

    private Context CONTEXT;

    public BitmapUtils(Context context) {
        this.CONTEXT = context;
    }

    public Bitmap getBitmap(@DrawableRes int drawableRes) {
        return BitmapFactory.decodeResource(CONTEXT.getResources(), drawableRes);
    }

    public Bitmap getBitmapFromXML(@DrawableRes int drawableRes) {
        Canvas canvas = new Canvas();
        Drawable drawable = AppCompatResources.getDrawable(CONTEXT, drawableRes);
        assert drawable != null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap getBitmapFromXML(@DrawableRes int drawableRes, int width, int height) {
        Bitmap bitmap = getBitmapFromXML(drawableRes);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return bitmap;
    }

    public Bitmap setColor(Bitmap bitmap, @ColorRes int color) {
        Bitmap mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap.recycle();
        Canvas canvas = new Canvas(mBitmap);
        int nc = mBitmap.getPixel(0,0);
        ColorFilter filter = new LightingColorFilter(nc, CONTEXT.getColor(color));
        Paint paint = new Paint();
        paint.setColorFilter(filter);
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        return mBitmap;
    }

}
