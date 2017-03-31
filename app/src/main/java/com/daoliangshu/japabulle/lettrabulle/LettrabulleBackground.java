package com.daoliangshu.japabulle.lettrabulle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.daoliangshu.japabulle.R;

/**
 * Created by daoliangshu on 2016/11/8.
 * Background of the LettraBulle game
 */

public class LettrabulleBackground {
    private int x = 0;
    private int y = 0;
    private int w = 0;
    private int h = 0;
    private Bitmap background;

    public LettrabulleBackground(Context context, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.fond_sombre2);
        background = Bitmap.createScaledBitmap(background, w, h, false);

    }

    public void onDraw(Canvas c) {
        c.drawBitmap(background, x, y, null);
    }
}
