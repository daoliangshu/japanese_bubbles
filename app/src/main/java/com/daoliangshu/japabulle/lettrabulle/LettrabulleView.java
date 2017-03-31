package com.daoliangshu.japabulle.lettrabulle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;


import com.daoliangshu.japabulle.DBHelper;
import com.daoliangshu.japabulle.vocab.VocabularyUnit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by daoliangshu on 2016/11/8.
 * Game View of the LettrABulle game : Vocabulary are aligned and going down.
 * User should choose a correct letter and try to fill the blank in the coming down words,
 * before that the word reach the bottom.
 */

public class LettrabulleView extends SurfaceView implements SurfaceHolder.Callback {

    /*-----------STATICs----------------*/
    private DBHelper myDBHelper;
    private boolean stopThread = false;

    /*-----Drawable Components----------------*/
    private LettrabulleBackground lettrabulleBackground;
    private Avatar avatar;
    private Canon canon;
    public LetterChooser letterChooser;
    public ArrayList<WordLine> wordLines;


    /*------State------------------------------*/
    private int mode = LB_Config.mode;
    private int currentState = 1;
    private int scrW;
    private int scrH;
    private int canonOriginX = -1;
    private int canonOriginY = -1;
    private float moveUnitX = 1.0f;
    private float moveUnitY = 1.0f;
    private int canonRadius = 60;
    private Rect boardRect;
    public Rect rectLetterChooser;
    private float moveX = 0f;
    private float moveY = 0f;
    private ArrayList<VocabularyUnit> vocList;
    private ArrayList<Integer[]> tops;
    private boolean isPushing = false;
    private int pushingCount = 18;
    private int pushingLimit = 18;
    private HashMap<String, String> wordToTransMap;
    private boolean needPauseGame = false;

    /*References*/
    private LettrabulleActivity rootActivity;
    public LettrabulleView self;
    private LettrabulleThread thread;
    public Context myContext;
    public Random rand;


    /*------------------------------------*/
    /*--------CONSTRUCTION : VIEW---------*/
    /*------------------------------------*/
    public LettrabulleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try{
            myDBHelper = new DBHelper(context);
        }catch(SQLException ioe){
            ioe.printStackTrace();
        }

        rand = new Random();
        wordToTransMap = new HashMap<>();
        self = this;
        this.myContext = context;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new LettrabulleThread(holder, context,
                new Handler() {
                    @Override
                    public void handleMessage(Message m) {
                    }
                });
        setFocusable(true);
    }

    /*
    * Thread
    */
    class LettrabulleThread extends Thread {
        private static final int FRAME_DELAY = 30;
        private long mLastTime;
        private SurfaceHolder mySurfaceHolder;
        private Canvas canvas;
        private boolean running = true;
        private boolean thread_running = true;

        /*---------------------------------*/
        /*----THREAD_CONSTRUCTOR-----------*/
        /*---------------------------------*/
        public LettrabulleThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
            mySurfaceHolder = surfaceHolder;
            myContext = context;
        }

        /*----------------------------*/
        /*-----THREAD_RUN-------------*/
        /*----------------------------*/
        @Override
        public void run() {
            while (thread_running) {
                //Delay control
                long now = System.currentTimeMillis();
                long delay = FRAME_DELAY + mLastTime - now;
                if (delay > 0) try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mLastTime = now;
                canvas = null;
                try {
                    if (mySurfaceHolder.getSurface().isValid() && running) {
                        canvas = mySurfaceHolder.lockCanvas(null);
                        if (canvas != null && running) {
                            synchronized (mySurfaceHolder) {
                                if (running) {
                                    draw(canvas);
                                    update();
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (canvas != null) {
                        try {
                            mySurfaceHolder.unlockCanvasAndPost(canvas);
                        } catch (IllegalStateException ise) {
                            Log.e("ERR", " failed to unlock canvas");
                        }
                    }
                }
                if (needPauseGame) {
                    running = false;
                    needPauseGame = false;
                    rootActivity.pauseGame();
                }
            }
        }

        /*----------------------------*/
        /*-----THREAD_EVENTS----------*/
        /*----------------------------*/
        public void leaveThread() {
            thread_running = false;
            stopThread = true;
            thread.interrupt();
        }


        /*------------------------------*/
        /*-----THREAD_DRAW & UPDATEs----*/
        /*------------------------------*/
        private void draw(Canvas canvas) {
            /***
             * Draw the different components
             * THis is the main method from which are called components onDraw methods.
             */
            lettrabulleBackground.onDraw(canvas);
            letterChooser.onDraw(canvas);
            lettrabulle_service.drawLines(canvas, wordLines);
            avatar.onDraw(canvas);
            canon.onDraw(canvas);
        }

        /*----------------------------*/
        /*-----THREAD_SETTERS---------*/
        /*----------------------------*/
        public void setSurfaceSize(int width, int height) {
            /***
             * Initilization of graphic components should be done here
             */
            synchronized (mySurfaceHolder) {
                scrH = height;
                scrW = width;
                int bubbleDiameter = scrW / LB_Config.BUBBLE_PER_LINE;
                LB_Config.initCommonResources(getContext(), bubbleDiameter);
                lettrabulleBackground = new LettrabulleBackground(myContext,
                        0, 0, width, height);
                rectLetterChooser = new Rect(width - width / 2,
                        height - width / 2,
                        width,
                        height);

                avatar = new Avatar(myContext, new Rect(0,
                        (int) (scrH * LB_Config.GAME_BOARD_RATIO),
                        scrW / 2,
                        scrH));

                boardRect = new Rect(0, 0, scrW, (int) (LB_Config.GAME_BOARD_RATIO * scrH));
                canonOriginX = scrW / 2;
                canonOriginY = (int) (LB_Config.GAME_BOARD_RATIO * scrH +
                        (1 - LB_Config.GAME_BOARD_RATIO) * scrH / 2);
                moveUnitX = width / 480;
                moveUnitY = height / 640;
                canon = new Canon(myContext,
                        canonOriginX,
                        canonOriginY,
                        scrW / (2 * LB_Config.BUBBLE_PER_LINE),
                        height);
                canon.setMoveUnits(moveUnitX, moveUnitY);

                canon.setBoard(boardRect);
                initLines(); // Should be before initialization of letterChooser,
                // as needed to retrieve the FiveLenghtLetterSet()
                letterChooser = new LetterChooser(myContext,
                        rectLetterChooser.left,
                        rectLetterChooser.top,
                        width / 2,
                        width / 2,
                        canon,
                        getFiveLengthLetterSet());

            }
            thread.setRunning(true);
            if (thread.getState() == Thread.State.NEW) {
                thread.start();
            }
        }

        public void stopThread() {
            running = false;
        }

        public void setRunning(boolean b) {
            if (!b) needPauseGame = true;
            else {
                needPauseGame = false;
                running = b;
                setNewFiveLengthLetterSet();
            }
        }
    } // END OF THREAD CLASS

    /*--------------------------------*/
    /*---------DRAW & UPDATES---------*/
    /*--------------------------------*/
    public void update() {
        new Thread() {
            @Override
            public void run() {
                letterChooser.update();
                int updateIndex;
                if (tops.get(0)[0] >= wordLines.size()) return;
                if (wordLines.get(tops.get(0)[0]).getTop() < 0) {
                    // Boosts speed when next line is not totally displayed
                    updateIndex = lettrabulle_service.moveLines(LB_Config.POWERUP_FALLING_SPEED, wordLines);
                } else {
                    updateIndex = lettrabulle_service.moveLines(LB_Config.currentSpeed, wordLines);
                }

                if (updateIndex != -1) {
                    rootActivity.pauseGame();
                    initLines();
                    setRunning(false);
                    updateTopBottomIndexLineIndex();
                    updateNeededLine(updateIndex);
                }

                if (canon.getIsEnableProjectile()) {
                    canon.move();
                    RectF proj = canon.getProjectileRectfF();
                    for (int i = wordLines.size() - 1; i >= 0; i--) {
                        if (wordLines.get(i).hasCollide((int) proj.centerX(), (int) proj.centerY(), canon.getLetter())) {
                            if (wordLines.get(i).getIsFinished()) {
                                /*-------ADD SCORE ----------*/
                                rootActivity.computeAndAddScore(LB_Config.SCORE_UNIT,
                                        LB_Config.CURRENT_SCORE_WEIGHT *
                                                (1 + Math.abs(1 - wordLines.get(i).getBottom() / boardRect.height())));
                                int prev = tops.get(0)[0];
                                int colorIndex = getRandColorIndex(tops.get(0)[0]);
                                wordLines.get(i).setWord(getRandomWord(), colorIndex);
                                updateTopBottomIndexLineIndex();
                                updateNeededLine(i);
                            }
                            canon.setActiveProjectile(false);
                            letterChooser.setCanon();
                            break;
                        }
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                if (isPushing == true) {
                    --pushingCount;
                    if (pushingCount <= 0) {
                        avatar.setIsPushing(false);
                        isPushing = false;
                        pushingCount = pushingLimit;
                    }
                }
            }
        }.start();


    }

    /***
     * Call when a line has reached the bottom or has collide and should be update
     *
     * @param index index of the line
     */
    public void updateNeededLine(int index) {
        int offset = 0;
        int prev = tops.get(0)[0];
        int colorIndex = getRandColorIndex(prev);
        this.wordLines.get(index).setCellColorIndex(colorIndex);
        if (this.tops.get(this.tops.size() - 1)[0] == index) {
            offset = 1;
        }
        if (this.tops.get(this.tops.size() - 1 - offset)[1] > 0) {
            this.wordLines.get(index).setPosFromBottom(-1);
        } else {
            this.wordLines.get(index).setPosFromBottom(this.tops.get(this.tops.size() - 1 - offset)[1] - 1);
        }
        updateTopBottomIndexLineIndex();
    }


    /**
     * Store/update in tops variable the order of display of the lines
     * Note: the top value in tops.get(i)[1] is not update until an event has happened requiring
     * to update. Thus, if you need the top of the line for a specific order, you should use
     * this.wordLines.get(tops.get(0 for lowest)[0]).getTop()
     */
    public void updateTopBottomIndexLineIndex() {
        tops = new ArrayList<>();
        for (int i = 0; i < this.wordLines.size(); i++) {
            Integer[] values = {i, this.wordLines.get(i).getTop()};
            tops.add(values);
        }
        try {
            Collections.sort(tops, new CustomIntegerComparator());
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* Update current word */

            /* Update infoPanel information when order has changed */
        String word = wordLines.get(tops.get(0)[0]).getWord();

        if (wordToTransMap.containsKey(word) && wordToTransMap.get(word) != null) {
            String str = wordToTransMap.get(word);
            this.rootActivity.updateInfoWord(word, str);
        }
    }

    /*--------------------------------*/
    /*---------EVENTS-----------------*/
    /*--------------------------------*/
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            moveX = event.getX(i);
            moveY = event.getY(i);
            int motionAction = event.getAction();
            switch (motionAction & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:
                    if (moveX >= rectLetterChooser.left && moveX <= rectLetterChooser.right &&
                            moveY >= rectLetterChooser.top && moveY <= rectLetterChooser.bottom) {
                        letterChooser.touchEvent(moveX, moveY);
                    } else if (moveX >= boardRect.left && moveX <= boardRect.right &&
                            moveY >= boardRect.top && moveY <= boardRect.bottom) {
                        canon.setAngle(getCanonAngle(moveX, moveY));
                        if (!canon.getIsEnableProjectile()) {
                            canon.setActiveProjectile(true);
                            isPushing = true;
                            avatar.setIsPushing(true);
                            setNewFiveLengthLetterSet();
                            letterChooser.setCanon();
                        }

                    }
                default:
            }
        }
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (myContext == null) {
            Log.e("ERR", "myContext is null in view ...");
        }
        getThread().mySurfaceHolder = holder;
        if (thread != null)
            thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false);
        stopThread = true;
        thread.interrupt();

    }


    public void leaveThread() {
        thread.stopThread();
        thread.interrupt();
    }

    public void stopThread() {
        thread.stopThread();
    }


    /*---------------------------------*/
    /*--------INITIALIZATION------- ---*/
    /*---------------------------------*/

    /**
     * Initialize the words to play with, as well as the position of the lines
     */
    public void initLines() {
        /* First initUnit the rectGameBoard class attribute */
        wordLines = new ArrayList<>();
        WordLine.rectGameBoard = new Rect(0, 0, scrW, (int) (LB_Config.GAME_BOARD_RATIO * scrH));
        Random rand = new Random();

        int numWordLine = 6;
        int prevCellColorIndex = -1;
        for (int i = 0; i < numWordLine; i++) {
            prevCellColorIndex = getRandColorIndex(prevCellColorIndex);
            wordLines.add(new WordLine(myContext));
            wordLines.get(i).setWord(getRandomWord(), prevCellColorIndex);
            if (i == 0) wordLines.get(i).setPos(0);
            else {
                wordLines.get(i).setPosFromBottom(wordLines.get(i - 1).getTop() - 1);
            }
        }
        updateTopBottomIndexLineIndex();
    }

    public void initInformationPanel(TextView wordInfo,
                                     TextView transInfo,
                                     TextView timeInfo,
                                     Button scoreInfo) {
    }

    /**
     * Chooses a random color bubble index for the line, which is not the same as
     * the previous one
     *
     * @param prevCellColorIndex
     * @return
     */
    public int getRandColorIndex(int prevCellColorIndex) {
        int cellColorIndex = Math.abs(rand.nextInt()) % LB_Config.CELLS_COUNT;
        if (cellColorIndex == prevCellColorIndex) {
            cellColorIndex = (cellColorIndex + 1 +
                    Math.abs(rand.nextInt()) % (LB_Config.CELLS_COUNT - 2)) % LB_Config.CELLS_COUNT;
        }
        return cellColorIndex;
    }

    public void setActivity(LettrabulleActivity parent) {
        this.rootActivity = parent;
    }

    /*--------------------------------*/
    /*------------SETTERS-------------*/
    /*--------------------------------*/
    public void setNewFiveLengthLetterSet() {
        if (letterChooser == null || tops == null) return;
        letterChooser.setNewFiveLengthLetterSet(getFiveLengthLetterSet());
        letterChooser.setCanon();
    }

    public void setRunning(boolean state) {
        thread.setRunning(state);
        //this.thread.setRunning(state);
    }

    /*--------------------------------*/
    /*------------GETTERS-------------*/
    /*--------------------------------*/

    public float getCanonAngle(float moveX, float moveY) {
        /**
         * Retrieve the angle in degrees of the canon, according the the given position
         * moveX and moveY should be inside rectBoard
         */
        float dx = canon.getCanonCenterX() - moveX;
        float dy = canon.getCanonCenterY() - moveY;
        float tmpAngle = (float) Math.toDegrees(Math.abs(Math.atan(dx / dy)));
        if (moveX < canon.getCanonCenterX()) tmpAngle *= -1;
        return tmpAngle;
    }

    public String getFiveLengthLetterSet() {
        String tmpLetterSet = "";
        for (int i = 0; i < tops.size(); i++) {
            tmpLetterSet += this.wordLines.get(this.tops.get(i)[0]).getToGuessLetters();
        }
        String letterSet = "";
        int count = 0;
        for (int i = 0; i < tmpLetterSet.length(); i++) {
            if (!letterSet.contains(tmpLetterSet.substring(i, i + 1))) {
                letterSet += tmpLetterSet.charAt(i);
            }
            if (letterSet.length() >= 5) break;
        }
        while (letterSet.length() < 5) letterSet += '~';
        return letterSet;
    }

    public LettrabulleThread getThread() {
        return this.thread;
    }

    private static int getIndex(MotionEvent event) {
        int idx = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        return idx;
    }

    public String getRandomWord() {
        String res;
        switch (LB_Config.mode) {
            case LB_Config.MODE_RANDOM_DATABASE:
                res = getRandFromDB(0, 100, Math.abs(rand.nextInt() % 3));
                break;
            case LB_Config.MODE_VOC_LIST_DATABASE:
                res = getRandFromVocList();
                break;
            default:
                return "erreur fatale";
        }
        return res;
    }

    private String getRandFromDB(int startRange, int endRange, int db_table_code) {
        int iteCount = 0;
        HashMap<String, String> res;
        do {
            res = myDBHelper.getEntryById(startRange + Math.abs(rand.nextInt() % (endRange - startRange)));
            iteCount += 1;
            if (iteCount > 15) break;
        } while (res == null || res.get(DBHelper.COL_TRANS).length() > WordLine.MAX_LETTER);
        if (res == null) return "erreur fatale";
        updateWordToTransMap(res.get(DBHelper.COL_TRANS));
        return res.get(DBHelper.COL_TRANS);
    }

    private String getRandFromVocList() {
        if (LB_Config.vocList.size() <= 0) return null;
        int randIndex = Math.abs(rand.nextInt() % LB_Config.vocList.size());
        String str = LB_Config.vocList.get(randIndex).getWord();
        updateWordToTransMap(str);
        if (str == null) return "fatale erreur";
        return str;
    }

    /**
     * The index is used differently according to the gaming mode:
     * (1)If the words are taken from a vocabulary list, then the index indicates the
     * place in the vocList from which to get the tids(trans ids) from.
     * (2)If it is a random game, randomly taking words from database, then the index
     * indicate the database table from which to search a match for.
     *
     * @param newJapaneseWord
     */
    public void updateWordToTransMap(String newJapaneseWord) {
        if (!wordToTransMap.containsKey(newJapaneseWord)) {
            switch (LB_Config.mode) {
                case LB_Config.MODE_VOC_LIST_DATABASE: //(1)
                    wordToTransMap.put(newJapaneseWord, myDBHelper.getWordTrans(newJapaneseWord));
                    break;
                case LB_Config.MODE_RANDOM_DATABASE: //(2)
                    String res =
                            myDBHelper.getSourceWord(newJapaneseWord);
                    if (res != null) {
                        try {
                            /* Get the translation found in the first meaning of
                                the first returned result.
                                split the String to obtain a  String[] to map
                             */
                            wordToTransMap.put(newJapaneseWord,
                                    res);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }
}


class CustomIntegerComparator implements Comparator<Integer[]> {
    @Override
    public int compare(final Integer[] o1, final Integer[] o2) {
        return o2[1].compareTo(o1[1]);
    }
}
