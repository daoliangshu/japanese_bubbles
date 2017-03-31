package com.daoliangshu.japabulle.lettrabulle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.daoliangshu.japabulle.R;

/**
 * Created by daoliangshu on 2016/11/9.
 * Represents the canon,
 * as well as the projectile
 */

public class Canon {

    /*-------Bitmaps--------*/
    private Bitmap canon;
    private Bitmap projectile;
    private Bitmap canonBasis;

    /*------State-----------*/
    private float angle = 0f;
    private int radius = 0;
    private float activeAngle;
    private Rect pos;
    private RectF rectfProj;
    private Rect rectBoard;
    private float moveUnitX = 1.0f;
    private float moveUnitY = 1.0f;
    private float distance = 0;
    private boolean isDisplayTrajectory = true;
    private boolean isActiveProjectile = false;
    private Bitmap nextBubble;
    private char curLetter, nextLetter;
    private float dx = 0.0f;
    private float dy = 0.0f;
    private Paint linePaint = null;


    /*-----References-------*/
    private Context context;
    private LetterChooser letterChooser;

    /*-----------------------*/
    /*-----CONSTRUCTOR-------*/
    /*-----------------------*/
    public Canon(Context context, int centerX, int centerY, int radius, int bottom) {
        this.context = context;
        this.radius = radius;
        canon = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble_canon1_0);
        canon = Bitmap.createScaledBitmap(canon, radius * 4, radius * 4, false);
        projectile = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.cell1);
        projectile = Bitmap.createScaledBitmap(projectile, radius * 2, radius * 2, false);
        pos = new Rect(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        rectfProj = new RectF(pos);

        canonBasis = BitmapFactory.decodeResource(context.getResources(), R.drawable.canon_base1);
        int canonBasisHeight = radius * 2;
        if (centerY < bottom) {
            canonBasisHeight = bottom - centerY;
        }
        canonBasis = Bitmap.createScaledBitmap(canonBasis, radius * 3, canonBasisHeight, false);

        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setPathEffect(new DashPathEffect(new float[]{30, 30, 30, 30}, 0));
        linePaint.setAlpha(200);
        linePaint.setStrokeWidth(12);
    }

    /*-------------------------------------*/
    /*------------DRAW & UPDATEs ----------*/
    /*-------------------------------------*/
    public void onDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(angle, pos.centerX(), pos.centerY());
        canvas.drawLine(pos.centerX(), pos.centerY(), pos.centerX(), pos.centerY() - 1000, linePaint);
        canvas.restore();
        canvas.drawBitmap(projectile, rectfProj.left, rectfProj.top, null);
        canvas.drawBitmap(canonBasis, pos.left - radius / 2, pos.centerY(), null);
        canvas.save();
        canvas.rotate(angle, pos.centerX(), pos.centerY());
        canvas.drawBitmap(canon, pos.left - radius, pos.top - radius, null);
        canvas.restore();
    }

    public void move() {
        if (dy == 0.0f) {
            dx = moveUnitX * (float) (-LB_Config.PROJECTILE_SPEED * Math.cos(Math.toRadians(this.activeAngle)));
            dy = moveUnitY * (float) (-LB_Config.PROJECTILE_SPEED * Math.sin(Math.toRadians(this.activeAngle)));
        }
        rectfProj.offset(dx, dy);
        distance += -LB_Config.PROJECTILE_SPEED;
        if (rectfProj.bottom < 0) {
            isActiveProjectile = false;
            dy = 0.0f;
            dx = 0.0f;
            distance = 0;
            rectfProj = new RectF(pos);
            this.projectile = this.nextBubble.copy(nextBubble.getConfig(), true);
            this.curLetter = this.nextLetter;
        } else if (rectfProj.left < rectBoard.left) {
            rectfProj.offsetTo(1, rectfProj.top);
            dy = 0.0f;
            this.activeAngle = -(180 + this.activeAngle) % 360;
        } else if (rectfProj.right > rectBoard.right) {
            dy = 0.0f;
            rectfProj.offsetTo(rectBoard.right - rectfProj.width(), rectfProj.top);
            this.activeAngle = (180 - this.activeAngle) % 360;
        }
    }

    /*-------------------------------------*/
    /*------------SETTERS-----------------*/
     /*-------------------------------------*/
    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setDisplayTrajectory(boolean state) {
        this.isDisplayTrajectory = state;
    }

    public void setActiveProjectile(boolean state) {
        this.isActiveProjectile = state;
        if (state) {
            activeAngle = angle + 90;
        } else {
            rectfProj = new RectF(this.pos);
            dy = 0.0f;
        }

    }

    public void setBoard(Rect rectBoard) {
        this.rectBoard = rectBoard;
    }

    public void setBubble(Bitmap cell, char selectedLetter) {
        this.projectile = cell.copy(cell.getConfig(), true);
        this.curLetter = selectedLetter;
    }

    /**
     * Set the next bubble to come after the one currently launched
     *
     * @param nextBubble
     * @param selectedLetter
     */
    public void setNext(Bitmap nextBubble, char selectedLetter) {
        this.nextBubble = nextBubble.copy(nextBubble.getConfig(), true);
        this.nextLetter = selectedLetter;
    }

    public void setLetterChooser(LetterChooser letterChooser) {
        this.letterChooser = letterChooser;
    }

    /*-------------------------------------*/
    /*------------GETTERS------------------*/
     /*-------------------------------------*/
    public int getCanonCenterX() {
        return this.pos.centerX();
    }

    public int getCanonCenterY() {
        return this.pos.centerY();
    }

    public RectF getProjectileRectfF() {
        return this.rectfProj;
    }

    public float getAngle() {
        return this.angle;
    }

    public char getLetter() {
        return this.curLetter;
    }

    public boolean getIsEnableProjectile() {
        return this.isActiveProjectile;
    }

    public void setMoveUnits(float moveUnitX, float moveUnitY) {
        this.moveUnitX = moveUnitX;
        this.moveUnitY = moveUnitY;
    }


}
