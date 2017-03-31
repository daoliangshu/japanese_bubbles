package com.daoliangshu.japabulle.lettrabulle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.daoliangshu.japabulle.R;


/**
 * Created by daoliangshu on 2016/11/9.
 * Represents the player character
 */

public class Avatar {

    private Rect rectPos;
    private Bitmap avatar;
    private Bitmap avatarPushed;
    private Context context;
    private boolean isPushing = false;

    public Avatar(Context context, Rect pos) {
        this.context = context;
        rectPos = pos;
        init();
    }

    public void init() {
        avatar = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.lettrabulle_avatar1_0,
                null);
        avatar = Bitmap.createScaledBitmap(avatar, rectPos.height(), rectPos.height(), false);
        avatarPushed = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.lettrabulle_avatar1_1,
                null);
        avatarPushed = Bitmap.createScaledBitmap(avatarPushed, rectPos.height(),
                rectPos.height(), false);
    }


     /*-------------------------------------*/
     /*----------DRAW & UPDATEs-------------*/
     /*-------------------------------------*/

    public void onDraw(Canvas canvas) {
        if (!isPushing) {
            canvas.drawBitmap(avatar, rectPos.left, rectPos.top, null);
        } else {
            canvas.drawBitmap(avatarPushed, rectPos.left, rectPos.top, null);
        }
    }

    public void setIsPushing(boolean isPushing) {
        this.isPushing = isPushing;
    }
}
