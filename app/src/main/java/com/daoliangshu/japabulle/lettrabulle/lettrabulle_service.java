package com.daoliangshu.japabulle.lettrabulle;

import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by daoliangshu on 2016/11/9.
 */

public class lettrabulle_service {

    /***
     * @param dy
     * @param lines WordLines
     * @return the index that has reach the bottom if any, or -1
     */
    public static int moveLines(float dy, ArrayList<WordLine> lines) {
        int needUpdateIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).move(dy)) needUpdateIndex = i;
        }
        return needUpdateIndex;
    }

    public static void drawLines(Canvas canvas, ArrayList<WordLine> lines) {
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).onDraw(canvas);
        }
    }

}
