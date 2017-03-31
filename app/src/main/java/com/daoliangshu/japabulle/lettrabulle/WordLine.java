package com.daoliangshu.japabulle.lettrabulle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.daoliangshu.japabulle.lettrabulle.LB_Config;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by daoliangshu on 2016/11/9.
 * THis class represents a line in the lettre à bulle game
 */

public class WordLine {
    /*----Class variable -----*/
    public final static int MAX_LETTER = 50; // need change will crash if > MAX_LETTER
    public static Rect rectGameBoard; /* Need first to initialize the rectGameBoard */

    /*----Bitmaps ----*/
    public Bitmap currentBubbleLine;
    public Canvas currentCanvas;

    /*-----State the line-----*/
    private int cellColorIndex = 0; // LB_Config
    private int letterCount = 0;
    private int lineCount = 0;
    private int bucketOffset = 0;
    private RectF rectfLine;
    private String word;
    private String formatedWord;
    private ArrayList<Integer> guessLetterIndexes;
    private int offsetTextX;
    private int offsetTextY;
    private Paint textPaint;

    /*-----------------------------------*/
    /*----------CONSTRUCTOR--------------*/
    /*-----------------------------------*/
    public WordLine(Context c) {
        if (rectGameBoard == null) {
            throw new ExceptionInInitializerError("<rectGameBoard> class attribute should be first initialized");
        }
        if (LB_Config.bubbleDiameter <= 0) {
            throw new ExceptionInInitializerError("<LB_Config> initCommonResources should be first called");
        }
    }

    /*-----------------------------------*/
    /*-------DRAW & UPDATES--------------*/
    /*-----------------------------------*/
    public void onDraw(Canvas canvas) {
        if (letterCount > 0 && canvas != null && rectfLine != null) {
            canvas.drawBitmap(currentBubbleLine, rectfLine.left, rectfLine.top, null);
        }
    }

    /***
     * @param dy
     * @return true when the line has reached the bottom and should be reassigned
     */
    public boolean move(float dy) {
        if (rectfLine == null) return false;
        rectfLine.offset(0, dy);
        if (rectfLine.top > rectGameBoard.bottom) {
            return true;
        }
        return false;
    }

    /*-----------------------------------*/
    /*------------SETTERS----------------*/
    /*-----------------------------------*/
    public void setWord(String word, int cellColorIndex) {
        if (cellColorIndex < 0 || cellColorIndex > LB_Config.CELLS_COUNT) {
            this.cellColorIndex = 0;
        } else {
            this.cellColorIndex = cellColorIndex;
        }
        setWord(word);
    }

    public void setWord(String word) {
        if (word.length() > MAX_LETTER || word.length() <= 0) {
            letterCount = 0;
            return;
        }
        this.word = word;
        this.letterCount = word.length();
        this.lineCount = 0;
        int tmp = word.length() - LB_Config.BUBBLE_PER_LINE;
        while (tmp > 0) {
            tmp -= LB_Config.BUBBLE_PER_LINE;
            ++lineCount;
            tmp += 1; // add a char for a '-' add the end of a line
        }

        this.guessLetterIndexes = WordLine.setRandomizedGuessIndexes(word);

        rectfLine = new RectF(0, 0, rectGameBoard.width(), LB_Config.bubbleDiameter * (lineCount + 1));
        currentBubbleLine = Bitmap.createBitmap((int) rectfLine.width(),
                (int) rectfLine.height(),
                Bitmap.Config.ARGB_4444);
        currentCanvas = new Canvas(currentBubbleLine);
        textPaint = new Paint();
        textPaint.setTextSize(3 * LB_Config.bubbleDiameter / 4);
        Rect textBounds = new Rect();
        textPaint.getTextBounds(word, 0, 1, textBounds);
        offsetTextX = LB_Config.bubbleDiameter / 2 - (textBounds.width() / 2);
        offsetTextY = (int) (currentCanvas.getHeight() / (2 * (1 + lineCount)) -
                ((textPaint.descent() + textPaint.ascent()) / 2));

        for (int i = 0; i < word.length(); i++) {
            setLetter(i);
        }
    }

    public void setLetter(int index) {

        Paint circlePaint = new Paint();
        circlePaint.setStrokeWidth(2);
        circlePaint.setColor(Color.BLACK);
        int curLine = index / LB_Config.BUBBLE_PER_LINE;
        currentCanvas.drawCircle(
                (index % LB_Config.BUBBLE_PER_LINE) * LB_Config.bubbleDiameter +
                        LB_Config.bubbleDiameter / 2,
                curLine * LB_Config.bubbleDiameter + LB_Config.bubbleDiameter / 2,
                LB_Config.bubbleDiameter / 2 + 2,
                circlePaint);
        if (this.guessLetterIndexes.contains(index)) {
                /* Draw in red letters to guess */
            currentCanvas.drawBitmap(LB_Config.cell1_red,
                    (index % LB_Config.BUBBLE_PER_LINE) * LB_Config.bubbleDiameter,
                    curLine * LB_Config.bubbleDiameter,
                    null);
                /* Hides the letter */
            currentCanvas.drawText("?",
                    ((index % LB_Config.BUBBLE_PER_LINE)) * LB_Config.bubbleDiameter + offsetTextX,
                    curLine * LB_Config.bubbleDiameter + offsetTextY,
                    textPaint);
        } else {
                /* Draw in original color other letters */
            currentCanvas.drawBitmap(LB_Config.cells[this.cellColorIndex],
                    (index % LB_Config.BUBBLE_PER_LINE) * LB_Config.bubbleDiameter,
                    curLine * LB_Config.bubbleDiameter,
                    null);
                /* Shows the other letters */
            currentCanvas.drawText(word.substring(index, index + 1),
                    ((index % LB_Config.BUBBLE_PER_LINE)) * LB_Config.bubbleDiameter + offsetTextX,
                    curLine * LB_Config.bubbleDiameter + offsetTextY,
                    textPaint);
        }

        Paint p2 = new Paint();
        p2.setAlpha(100);
        currentCanvas.drawBitmap(LB_Config.cell1Front,
                (index % LB_Config.BUBBLE_PER_LINE) * LB_Config.bubbleDiameter,
                curLine * LB_Config.bubbleDiameter,
                p2);
    }

    public static ArrayList<Integer> setRandomizedGuessIndexes(String word) {
        Random rand = new Random();
        int numberOfGuess = 1 + Math.abs(rand.nextInt() % word.length() / 6);
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < numberOfGuess; i++) {
            int newIndex;
            do {
                newIndex = Math.abs(rand.nextInt() % word.length());
            } while (res.contains(newIndex) || word.charAt(newIndex) == ' ');
            res.add(newIndex);
        }
        return res;
    }


    /**
     * Assign a new color to the line
     *
     * @param colorIndex
     */
    public void setCellColorIndex(int colorIndex) {
        if (colorIndex >= 0 && colorIndex < LB_Config.CELLS_COUNT) {
            this.cellColorIndex = colorIndex;
        } else {
            this.cellColorIndex = 0;
        }
    }

    public void setPos(int y) {
        rectfLine.offsetTo(0, y);
    }

    public void setPosFromBottom(int newBottomY) {
        this.rectfLine.offsetTo(this.rectfLine.left, newBottomY - rectfLine.height());
    }


    /*-----------------------------------------------*/
    /*-------------GETTERS---------------------------*/
    /*-----------------------------------------------*/
    public int getBottom() {
        return (int) rectfLine.bottom;
    }

    public int getTop() {
        if (rectfLine != null)
            return (int) rectfLine.top;
        else return -1;
    }

    public int getColorIndex() {
        return this.cellColorIndex;
    }

    public int getLineCount() {
        return this.lineCount;
    }

    public boolean hasCollide(int colX, int colY, char collidingLetter) {
        if (colY > this.rectfLine.top && colY < this.rectfLine.bottom) {
            int i = (int) (colX - this.rectfLine.left) / LB_Config.bubbleDiameter;
            i += ((int) (colY - this.rectfLine.top) / LB_Config.bubbleDiameter) * LB_Config.BUBBLE_PER_LINE;

            if (i >= 0 && i < this.word.length() && this.guessLetterIndexes.contains(i)) {
                char cc = this.word.charAt(i);
                if (collidingLetter == cc) {
                    this.guessLetterIndexes.remove(this.guessLetterIndexes.indexOf(i));
                    this.setLetter(i);
                    return true;
                }
            }
        }
        return false;
    }

    public String getToGuessLetters() {
        String res = "";
        for (int i = 0; i < this.guessLetterIndexes.size(); i++) {
            res += this.word.charAt(this.guessLetterIndexes.get(i));
        }
        return res;
    }

    public String getWord() {
        return this.word;
    }

    public static boolean getIsVoyel(char c) {
        switch (c) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
            case 'y':
                return true;
            default:
                return false;
        }
    }

    public boolean getIsFinished() {
        return this.guessLetterIndexes.size() <= 0;
    }
}
