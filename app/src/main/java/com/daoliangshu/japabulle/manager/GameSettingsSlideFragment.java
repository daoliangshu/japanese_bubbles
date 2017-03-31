package com.daoliangshu.japabulle.manager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.daoliangshu.japabulle.R;
import com.daoliangshu.japabulle.lettrabulle.LB_Config;

/**
 * Created by daoliangshu on 2/5/17.
 */

public class GameSettingsSlideFragment extends Fragment {

    private Activity parentActivity;
    private TextView mWordView;
    private TextView mTransView;
    private TextView mTime;
    private TextView mScore;
    private ViewGroup rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.game_settings_fragment, container, false);
        parentActivity = getActivity();
        mWordView = (TextView) rootView.findViewById(R.id.bulle_word_to_guess);
        mTransView = (TextView) rootView.findViewById(R.id.bulle_trans_of_word);
        mTime = (TextView) rootView.findViewById(R.id.bulle_time);
        mScore = (TextView) rootView.findViewById(R.id.bulle_score);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        /*--------_Speed Settings -------------*/
        RadioGroup radioGroupSpeed = (RadioGroup) rootView.findViewById(R.id.radioGroupSpeed);
        int currSpeed = LB_Config.getSpeedCode();
        int selectedRadio = 0;
        switch (currSpeed) {
            case 0:
                selectedRadio = R.id.radio_slow;
                break;
            case 2:
                selectedRadio = R.id.radio_fast;
                break;
            default:
                selectedRadio = R.id.radio_medium;
                break;
        }
        RadioButton selectedRdb = (RadioButton) rootView.findViewById(selectedRadio);
        radioGroupSpeed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_slow:
                        LB_Config.currentSpeed = LB_Config.SPEED_SLOW;
                        break;
                    case R.id.radio_medium:
                        LB_Config.currentSpeed = LB_Config.SPEED_MEDIUM;
                        break;
                    case R.id.radio_fast:
                        LB_Config.currentSpeed = LB_Config.SPEED_FAST;
                        break;
                    default:
                        LB_Config.currentSpeed = LB_Config.SPEED_MEDIUM;
                }
            }
        });


        return rootView;
    }
}
