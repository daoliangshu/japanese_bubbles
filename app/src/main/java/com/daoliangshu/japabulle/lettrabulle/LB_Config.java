package com.daoliangshu.japabulle.lettrabulle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.daoliangshu.japabulle.R;
import com.daoliangshu.japabulle.vocab.VocabularyUnit;

import java.util.ArrayList;

/**
 * Created by daoliangshu on 2016/11/11.
 * Settings for LettraBulle game
 */

public class LB_Config {

    /*------Contants--------*/
    public static final int MODE_RANDOM_DATABASE = 0; // Fetch random words from database
    public static final int MODE_VOC_LIST_DATABASE = 1; // from voc list which uses database
    public static final int MODE_VOC_LIST_INDEPENDANT = 2; //from voc list ,which


    /*-----In game config--------*/
    public static int mode = LB_Config.MODE_RANDOM_DATABASE;
    public static final float GAME_BOARD_RATIO = 0.7f;
    public static final int BUBBLE_PER_LINE = 7;
    public static final float STANDARD_FALLING_SPEED = 0.5f;

    /*---- Speed control ---------------*/
    public static final float SPEED_SLOW = 0.3f;
    public static final float SPEED_MEDIUM = 0.9f;
    public static final float SPEED_FAST = 1.5f;
    public static float currentSpeed = SPEED_SLOW;

    public static float POWERUP_FALLING_SPEED = 1.5f;
    public static float PROJECTILE_SPEED = 13f;
    public static int LINE_PER_BOARD = 6;
    public static ArrayList<VocabularyUnit> vocList;
    public final static int SCORE_UNIT = 10;
    public static float CURRENT_SCORE_WEIGHT = 1.0f;


    /*Loaded common resources*/
    public static int bubbleDiameter;
    public static Bitmap cell1;
    public static Bitmap cell1Front;
    public static Bitmap cell1_red;
    public static Bitmap[] cells;
    public static final int CELLS_COUNT = 5;

    public static void initCommonResources(Context c, int bubbleDiameter) {
        if (cell1 != null && cell1.getWidth() != bubbleDiameter) return;
        LB_Config.bubbleDiameter = bubbleDiameter;
        LB_Config.cell1 = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.cell1);
        LB_Config.cell1Front = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.cell1_transparent);
        LB_Config.cell1_red = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.cell1_red);
        LB_Config.cell1 = Bitmap.createScaledBitmap(LB_Config.cell1,
                bubbleDiameter,
                bubbleDiameter,
                false);
        LB_Config.cell1Front = Bitmap.createScaledBitmap(LB_Config.cell1Front,
                bubbleDiameter,
                bubbleDiameter,
                false);
        LB_Config.cell1_red = Bitmap.createScaledBitmap(LB_Config.cell1_red,
                bubbleDiameter,
                bubbleDiameter,
                false);
        LB_Config.cells = new Bitmap[5];
        Bitmap bubbles = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.cells_64x64_1x5);
        bubbles = Bitmap.createScaledBitmap(bubbles,
                bubbleDiameter * LB_Config.CELLS_COUNT,
                bubbleDiameter,
                false);
        for (int i = 0; i < LB_Config.CELLS_COUNT; i++) {
            LB_Config.cells[i] = Bitmap.createBitmap(bubbles,
                    i * bubbleDiameter,
                    0,
                    bubbleDiameter,
                    bubbleDiameter);
        }
    }

    public static void setSpeed(int speed_code) {
        switch (speed_code) {
            case 0:
                currentSpeed = SPEED_SLOW;
                break;
            case 1:
                currentSpeed = SPEED_MEDIUM;
                break;
            case 2:
                currentSpeed = SPEED_FAST;
                break;
            default:
                currentSpeed = SPEED_MEDIUM;
        }
    }

    public static int getSpeedCode() {
        if (currentSpeed == SPEED_SLOW) return 0;
        else if (currentSpeed == SPEED_FAST) return 2;
        else return 1;
    }
}
