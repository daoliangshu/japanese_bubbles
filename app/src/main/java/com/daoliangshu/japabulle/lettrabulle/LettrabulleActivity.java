package com.daoliangshu.japabulle.lettrabulle;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.daoliangshu.japabulle.DBHelper;
import com.daoliangshu.japabulle.R;
import com.daoliangshu.japabulle.manager.GameSettingsSlideFragment;
import com.daoliangshu.japabulle.manager.GameStatusSlideFragment;
import com.daoliangshu.japabulle.manager.ZoomOutPageTransformer;
import com.daoliangshu.japabulle.vocab.VocabularyUnit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by daoliangshu on 2016/11/8.
 */

public class LettrabulleActivity extends AppCompatActivity {

    private com.daoliangshu.japabulle.lettrabulle.LettrabulleView gameView;
    private DBHelper dbHelper;
    private ViewPager mGameMenuPager;

    private GameSettingsSlideFragment gameSettings;
    private GameStatusSlideFragment gameStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.lettrabulle_layout);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            Parcelable[] ps = extra.getParcelableArray("vocUnits");
            VocabularyUnit[] vocUnits = new VocabularyUnit[ps.length];
            System.arraycopy(ps, 0, vocUnits, 0, ps.length);
            LB_Config.vocList = new ArrayList<>(Arrays.asList(vocUnits));
            LB_Config.mode = LB_Config.MODE_VOC_LIST_DATABASE;
        } else {
            LB_Config.mode = LB_Config.MODE_RANDOM_DATABASE;
        }
        try {
            dbHelper = new DBHelper(getApplicationContext());
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
            gameView = (LettrabulleView) findViewById(R.id.lettrabulle_view);
        gameView.setActivity(this);



        /*---- Flip View ---*/
        Button retryButton = (Button) findViewById(R.id.lettrabule_retry_btn);
        Button quitButton = (Button) findViewById(R.id.lettrabule_quit_btn);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onDestroy();
                    }
                });
            }
        });


        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Go back to game */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("CHANGE_VIEW", "VIEW HAS CHANGED");
                        gameStatus.restartTimer();
                        View v1 = findViewById(R.id.game_layout);
                        View v = findViewById(R.id.lettrabulle_game_over_view);
                        v.setVisibility(View.GONE);
                        gameView.setRunning(true);
                        v1.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


        mGameMenuPager = (ViewPager) findViewById(R.id.game_menu_pager);
        PagerAdapter mPagerAdapterMain = new SlidePagerAdapter(getSupportFragmentManager()
        );
        mGameMenuPager.setAdapter(mPagerAdapterMain);
        mGameMenuPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }


    String str;
    String strZh;

    Thread thread = new Thread(new Runnable() {
        public void run() {
            if (gameStatus != null) {
                gameStatus.setWord(strZh);
                gameStatus.setTrans(strZh);
            }
        }
    });

    public void updateInfoWord(String str, String strZh) {
        this.str = str;
        this.strZh = strZh;
        runOnUiThread(thread);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.setRunning(true);
        //gameView.getThread();
    }


    @Override
    protected void onPause() {
        super.onPause();
        gameView.setRunning(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.leaveThread();
        gameView.getThread().leaveThread();
        this.finish();
    }

    public void pauseGame() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("CHANGE_VIEW", "VIEW HAS CHANGED");

                if (gameStatus != null) gameStatus.pauseTimer();

                View v = findViewById(R.id.lettrabulle_game_over_view);
                if (v.getVisibility() == View.VISIBLE) return;
                View v1 = findViewById(R.id.game_layout);
                v1.setVisibility(View.GONE);

                v.setVisibility(View.VISIBLE);


                TextView tvScore = (TextView) findViewById(R.id.lettrabulle_score_gameover);
                if (gameStatus != null)
                    tvScore.setText(String.valueOf(String.valueOf(gameStatus.getScore())));
                if (gameStatus != null) gameStatus.setScore(0);
            }
        });
    }

    public void computeAndAddScore(final int baseInt, final float weight) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int scoreToAdd = (int) (baseInt * weight);
                gameStatus.incrementScore(scoreToAdd);

            }
        });
    }


    private class SlidePagerAdapter extends FragmentStatePagerAdapter {
        private Fragment mCurrentFragment;

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    GameStatusSlideFragment res = new GameStatusSlideFragment();
                    gameStatus = res;
                    return res;
                case 1:
                    GameSettingsSlideFragment res2 = new GameSettingsSlideFragment();
                    gameSettings = res2;
                    return res2;
            }
            return null;
        }


        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}