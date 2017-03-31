package com.daoliangshu.japabulle.vocab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.daoliangshu.japabulle.R;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by daoliangshu on 12/11/16.
 */


public class VocabListDisplayedEntry extends RelativeLayout {
    private String fn;
    private int vocInListIndex = 0;
    private TextView tvTitleFr;
    private TextView tvTitleZh;

    private VocabularyActivity parentActivity;
    private TextView tvVocCount;
    private TextView tvThematic;
    private TextView tvLevel;
    private TextView tvContentType; // default:Nope, possible choice: Grammar, Vocabulary, Mixed

    public VocabListDisplayedEntry(Context c, final VocabularyActivity parentActivity) {
        super(c);
        this.parentActivity = parentActivity;
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.voclist_entry_element, this, true);
        /*GradientDrawable border = new GradientDrawable();
        border.setColor(ContextCompat.getColor(c, R.color.darkBlue));
        border.setStroke(1, ContextCompat.getColor(c, R.color.lightGreen));
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackgroundDrawable(border);
        } else {
            this.setBackground(border);
        }*/
        tvTitleFr = (TextView) findViewById(R.id.tvTitleFr);
        tvTitleZh = (TextView) findViewById(R.id.tvTitleZh);
        tvVocCount = (TextView) findViewById(R.id.tvWaveCount);
        tvThematic = (TextView) findViewById(R.id.tvThematic);
        //tvThematic.setBackgroundColor(Color.rgb(11,11,111));
        tvLevel = (TextView) findViewById(R.id.tvLevel);
        /*
        GradientDrawable border = new GradientDrawable();
        border.setColor(ContextCompat.getColor(c, R.color.darkBlue));
        border.setStroke(1, ContextCompat.getColor(c, R.color.lightGreen));
        if(Build.VERSION.SDK_INT < 16){
            this.setBackgroundDrawable(border);
        }else{
            this.setBackground(border);
        }*/


        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.loadVocList(getFilename(), getVocInListIndex());
            }
        });
        tvVocCount = (TextView) findViewById(R.id.tvWaveCount);
        tvVocCount.setText("0");
    }


    /*------------------------SETTERS-----------------------------*/
    private void setVocCount(int vocCount) {
        this.tvVocCount.setText(String.format(Locale.ENGLISH, "%d", vocCount));
    }

    public void setTitleFr(String title) {
        this.tvTitleFr.setText(title);
    }

    public void setVocInListIndex(int vocInListIndex) {
        tvVocCount.setText(String.format(Locale.ENGLISH, "%2d", vocInListIndex));
        this.vocInListIndex = vocInListIndex;
    }

    public void setTitleZh(String title) {
        this.tvTitleZh.setText(title);
    }

    public void setFilename(String filename) {
        this.fn = filename;
    }

    public void setLevel(String level) {
        this.tvLevel.setText(level);
    }

    public void setThematic(String theme) {
        this.tvThematic.setText(theme);
    }

    public void set(HashMap<String, String> actInfo) {
        if (actInfo.get("titleFr") == null) setTitleFr("Pas de titre");
        else setTitleFr(actInfo.get("titleFr"));
        if (actInfo.get("titleZh") == null) setTitleZh("Pas de titre2");
        else setTitleZh(actInfo.get("titleZh"));
        setFilename(actInfo.get("filename"));

    }

    public void set(VocabularyListUnit vlu) {
        setTitleZh(vlu.getTitle());
        setTitleFr(vlu.getTitleZh());
        setFilename(vlu.getFilePath());
        setVocInListIndex(vlu.getIndexInSet());
        setThematic(vlu.getThematic());
        setLevel(vlu.getLevel());
        setVocCount(vlu.getSize());

    }

    /*------------------------GETTERS-----------------------------*/
    public String getTitleFr() {
        return this.tvTitleFr.getText().toString();
    }

    public String getTitleZh() {
        return this.tvTitleZh.getText().toString();
    }

    public int getVocInListIndex() {
        return this.vocInListIndex;
    }

    public String getFilename() {
        return this.fn;
    }

}
