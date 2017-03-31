package com.daoliangshu.japabulle.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.daoliangshu.japabulle.DBHelper;
import com.daoliangshu.japabulle.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by daoliangshu on 2/5/17.
 */

public class GameStatusSlideFragment extends Fragment {

    private Activity parentActivity;
    private TextView mWordView;
    private TextView mTransView;
    private TextView mTime;
    private TextView mScore;
    private Timer timer;
    private boolean timeRunning = true;
    private int timerValue;
    private int maxTimerValue;
    private ViewGroup rootView;

    private Context context;
    private int timeValue = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.game_status_fragment, container, false);
        parentActivity = getActivity();
        timerValue = 0;
        maxTimerValue = 9000;
        mWordView = (TextView) rootView.findViewById(R.id.bulle_word_to_guess);
        mTransView = (TextView) rootView.findViewById(R.id.bulle_trans_of_word);
        mTime = (TextView) rootView.findViewById(R.id.bulle_time);
        mScore = (TextView) rootView.findViewById(R.id.bulle_score);


        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final Thread timerUpdateThread = new Thread(new Runnable() {
            public void run() {
                if (timeRunning) {
                    timerValue += 1;
                    if (timerValue >= maxTimerValue) {
                        timerValue = 0;
                        setTimer(timerValue);
                    } else {
                        incrementTimer();
                    }
                }

            }
        });
        timer = new Timer();
        TimerTask timerTaskObj = new TimerTask() {
            public void run() {
                if (getActivity() != null)
                    getActivity().runOnUiThread(timerUpdateThread);
            }
        };
        timer.schedule(timerTaskObj, 0, 1500);

        return rootView;
    }


    public void setWord(String newWord) {
        this.mWordView.setText(newWord);
        this.mWordView.postInvalidate();
    }

    public void setWord(String newWord, boolean fetchTrans, DBHelper dbHelper) {
        if (fetchTrans && newWord.trim().length() > 0) {
            String content;
            String trans;
            trans = dbHelper.getWordTrans(newWord.trim());
            content = "";
            if (trans != null) {
                this.setTrans(trans);
            } else {
                this.setTrans("");
                this.setWord(newWord);
            }
        } else {
            setWord(newWord);
        }
    }

    public void setTrans(String newTrans) {
        this.mTransView.setText(newTrans);
        this.mTransView.postInvalidate();
    }

    public void setTimer(int newValue) {
        this.timeValue = newValue;
        int minutes = (newValue % 3600) / 60;
        int seconds = newValue % 60;
        String timeString =
                String.format(Locale.US, "%02d:%02d", minutes, seconds);
        this.mTime.setText(timeString);
    }

    public void setScore(int score) {
        this.mScore.setText(String.valueOf(score));
    }

    public void incrementScore(int inc) {
        String scoreText = this.mScore.getText().toString();
        int score;
        try {
            score = Integer.parseInt(scoreText);
        } catch (NumberFormatException nfe) {
            score = 0;
        }
        score += inc;
        this.mScore.setText(String.valueOf(score));
    }

    public void incrementTimer() {
        this.timeValue += 1;
        int minutes = (this.timeValue % 3600) / 60;
        int seconds = this.timeValue % 60;
        String timeString =
                String.format(Locale.US, "%02d:%02d", minutes, seconds);
        this.mTime.setText(timeString);
    }


    /*------------------------------------------------*/
    /*-------------------GETTERS----------------------*/
    /*------------------------------------------------*/
    public int getScore() {
        try {
            return Integer.parseInt(this.mScore.getText().toString());
        } catch (Exception ex) {
            return 0;
        }
    }

    public void pauseTimer() {
        timeRunning = false;
        timerValue = 0;
    }

    public void restartTimer() {
        timeRunning = true;
        timerValue = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timeRunning = false;
        timer.purge();
    }
}
