package com.daoliangshu.japabulle.lettrabulle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;


import com.daoliangshu.japabulle.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by daoliangshu on 2016/11/8.
 * This class represents the interface in which the user chooser a letter amongst five choices
 */

public class LetterChooser {
    /*-------Statics------------*/
    public static Canon canon;


    /*--------Bitmaps-----------*/
    private Bitmap background;
    private Bitmap aiguille;
    private ArrayList<Bitmap> letterInBubble;

    /*-------States-------------*/
    private int h = 0;
    private int w = 0;
    private int x = 0;
    private int y = 0;
    private ArrayList<RectF> cellRects;
    private ArrayList<Character> cellContents;
    private float angle = 90;
    private Paint p;
    private Paint circlePaint;
    private Context c;
    private int selectedIndex = 0;
    private static Random rand = new Random();

    /*--------------------------------------*/
    /*-----------CONSTRUCTOR----------------*/
    /*--------------------------------------*/
    public LetterChooser(Context c, int x, int y, int width, int height, Canon canon) {
        this(c, x, y, width, height, canon, null);
    }

    public LetterChooser(Context c,
                         int x,
                         int y,
                         int width,
                         int height,
                         Canon canon,
                         String lengthFiveChars) {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        this.c = c;
        LetterChooser.canon = canon;
        LetterChooser.canon.setLetterChooser(this);
        p = new Paint();
        p.setColor(Color.BLUE);
        aiguille = BitmapFactory.decodeResource(c.getResources(), R.drawable.aiguille);
        aiguille = Bitmap.createScaledBitmap(aiguille, width, width, false);
        circlePaint = new Paint();
        circlePaint.setStrokeWidth(2);
        circlePaint.setColor(Color.BLACK);
        initCells(lengthFiveChars);

        background = BitmapFactory.decodeResource(c.getResources(), R.drawable.chooser_back1);
        background = Bitmap.createScaledBitmap(background, width, height, false);
    }

    /*--------------------------------------*/
    /*----------DRAW & UPDATES--------------*/
    /*--------------------------------------*/
    public void onDraw(Canvas c) {
        c.drawBitmap(background, x, y, p);
        c.save();
        c.rotate(angle, x + w, y + h);
        c.drawBitmap(aiguille, x + w / 2, y + h / 2, null);
        c.restore();

        for (int i = this.cellRects.size() - 1; i >= 0; i--) {
            c.drawCircle(cellRects.get(i).centerX(),
                    cellRects.get(i).centerY(),
                    cellRects.get(i).width() / 2 + 2, circlePaint);

            c.drawBitmap(letterInBubble.get(i), cellRects.get(i).left, cellRects.get(i).top, null);
        }
    }

    public void update() {
        //angle = (angle + 0.9f)%360;
    }


    public void touchEvent(float posX, float posY) {
        float tmpX = x + w - posX;
        float tmpY = y + h - posY;
        if ((tmpX <= 0 || tmpX >= w) || (tmpY <= 0 || tmpY >= h)) return;
        float tmpAngle = (float) Math.atan(tmpY / tmpX);
        tmpAngle = (float) Math.toDegrees(tmpAngle) + 90;
        if (tmpAngle < 90 || tmpAngle > 180) return;
        int tempFixedAngle = 99;
        for (int i = 0; i < 5; i++) {
            if (tmpAngle >= (i) * 18 + 9 + 90) {
                tempFixedAngle = 9 + i * 18 + 90;
                selectedIndex = this.cellContents.size() - i - 1;
            } else {
                break;
            }
        }
        angle = tempFixedAngle;
        Bitmap scaledForCanon = Bitmap.createScaledBitmap(
                this.letterInBubble.get(selectedIndex),
                LB_Config.bubbleDiameter,
                LB_Config.bubbleDiameter,
                false);
        if (canon.getIsEnableProjectile()) {
            canon.setNext(scaledForCanon, this.getSelectedChar());
        } else {
            canon.setBubble(scaledForCanon, this.getSelectedChar());
        }
    }


    /*--------------------------------------*/
    /*-------------INITIALIZATION-----------*/
    /*--------------------------------------*/
    public void initCells(String InitialLetters) {
        this.cellRects = new ArrayList<>();
        this.cellContents = new ArrayList<>();
        float angleCenter = 90f + 9f;
        float distFromBase = this.w - this.w / 4;
        String tmpString = InitialLetters;
        if (tmpString == null || tmpString.length() != 5) {
            tmpString = "";
            for (int i = 0; i < tmpString.length(); i++) {
                char a = 'a';
                a += i;
                tmpString += a;
            }
        }
        for (int i = 0; i < 5; i++) {
            cellContents.add(tmpString.charAt(i));
            cellRects.add(new RectF());
            cellRects.get(i).left = this.x + this.w + distFromBase * (float) Math.cos(Math.toRadians(angleCenter));
            cellRects.get(i).top = this.y + this.w - distFromBase * (float) Math.sin(Math.toRadians(angleCenter));
            angleCenter += 18;
        }

        int L = (int) (18 * Math.PI * distFromBase / 180);
        for (int i = 0; i < cellRects.size(); i++) {
            cellRects.get(i).left -= L / 2;
            cellRects.get(i).right = cellRects.get(i).left + L;
            cellRects.get(i).top -= L / 2;
            cellRects.get(i).bottom = cellRects.get(i).top + L;
        }

        for (int i = 0; i < 5; i++) {
            setBubble(cellContents.get(i), i, false);
        }
        setCanon();
    }

    /*--------------------------------------*/
    /*-------------SETTERS------------------*/
    /*--------------------------------------*/

    /***
     * Set the bubble according to the new char, and at the given index
     *
     * @param myChar      new char to assign to the given index
     * @param index       Index of the cell
     * @param passToCanon If true, marks the index as the selected one and pass it to the canon
     */
    public void setBubble(char myChar, int index, boolean passToCanon) {
        if (index < 0 || index >= 5) {
            return;
        }
        if (this.letterInBubble == null) {
            this.letterInBubble = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                this.letterInBubble.add(null);
            }
        }

        Bitmap bm = LB_Config.cell1.copy(LB_Config.cell1.getConfig(), true);
        bm = Bitmap.createScaledBitmap(bm,
                (int) cellRects.get(0).width(),
                (int) cellRects.get(0).height(),
                false);
        Canvas tmpCan = new Canvas(bm);
        Paint tmpPaint = new Paint();
        Rect bounds = new Rect();
        String str = "" + myChar;

        tmpPaint.setTextSize(3f * tmpCan.getHeight() / 4f);
        tmpPaint.getTextBounds(str, 0, str.length(), bounds);
        tmpPaint.setTextAlign(Paint.Align.CENTER);

        int tmpX = tmpCan.getWidth() / 2;
        int tmpY = (int) ((tmpCan.getHeight() / 2) - ((tmpPaint.descent() + tmpPaint.ascent()) / 2));
        tmpCan.drawText(str, tmpX, tmpY, tmpPaint);
        letterInBubble.set(index, bm);
        this.cellContents.set(index, myChar);
        if (passToCanon) {
            setSelected(index);
        }
    }


    /**
     * Set the letter that the user can choose,
     * They are shuffled before assignement
     *
     * @param fiveLengthString
     */
    public void setNewFiveLengthLetterSet(String fiveLengthString) {
        if (fiveLengthString == null || fiveLengthString.length() < 5) return;
        int[] toShuffle = {0, 1, 2, 3, 4};
        LetterChooser.shuffle(toShuffle);

        for (int i = 0; i < 5; i++) {
            this.setBubble(fiveLengthString.charAt(toShuffle[i]), i, false);
        }
    }

    /**
     * Shortcut for setBubble, directly assigning the new char for the selected index, and give it
     * to the canon
     *
     * @param newLetter
     */
    public void setSelectedBubble(char newLetter) {
        this.setBubble(newLetter, this.selectedIndex, true);
    }

    /**
     * Select a cell by index, and give it to canon
     *
     * @param index
     */
    public void setSelected(int index) {
        selectedIndex = index;
        setCanon();
    }

    public void setCanon() {
        /* Assign the new content to canon */
        Bitmap scaledForCanon = Bitmap.createScaledBitmap(
                this.letterInBubble.get(selectedIndex),
                LB_Config.bubbleDiameter,
                LB_Config.bubbleDiameter,
                false
        );
        if (!canon.getIsEnableProjectile()) {
            canon.setBubble(scaledForCanon,
                    getSelectedChar());
        } else {
            canon.setNext(scaledForCanon,
                    getSelectedChar());
        }
    }

    /*--------------------------------------*/
    /*------------GETTERS-------------------*/
    /*--------------------------------------*/
    public char getSelectedChar() {
        return this.cellContents.get(selectedIndex);
    }

    public int getIndexFromAngle(float angle) {
        int index = 0;
        for (int i = 0; i < 5; i++) {
            if (angle >= 180 - (i + 1) * 18) {
                index = i;
            } else {
                break;
            }
        }
        return index;
    }

    public static <T> void shuffle(int[] arr) {
        if (rand == null) {
            rand = new Random();
        }

        for (int i = arr.length - 1; i > 0; i--) {
            swap(arr, i, rand.nextInt(i + 1));
        }
    }

    public static <T> void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static int[] shuffled(int[] arr) {
        int[] copy = Arrays.copyOf(arr, arr.length);
        shuffle(copy);
        return copy;
    }

}
