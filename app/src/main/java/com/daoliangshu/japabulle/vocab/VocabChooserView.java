package com.daoliangshu.japabulle.vocab;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;


import com.daoliangshu.japabulle.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by daoliangshu on 11/27/16.
 * This view provides a convenient panel for user to choose a vocabulary set,
 * amongst the set available.
 */

public class VocabChooserView extends LinearLayout {
    private static ArrayList<VocabularyListUnit> vocLists;
    private VocabularyActivity parentActivity = null;
    private Context myContext = null;
    private String[] sortWay;
    private int currentSortWay = 0;
    private boolean notLoad = true;

    public VocabChooserView(final Context context, VocabularyActivity activity) {
        super(context);
        parentActivity = activity;
        setOrientation(LinearLayout.VERTICAL);
        String[] list = null;
        InputStream is = null;
        myContext = context;

        try {
            File fileDir = context.getExternalFilesDir("librefra_lessons");
            if (fileDir != null) list = fileDir.list();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list == null) return;
        String vocFileNames = "";
        int count = 0;
        for (String fn : list) {
            if (count != 0) vocFileNames += ",";
            count++;
            vocFileNames += "librefra_lessons/" + fn;
        }

        try {
            //File fileDir = context.getExternalFilesDir("librefra_vocab");
            //File fileDir = context.getDir("vocab", Context.MODE_PRIVATE);
            File fileDir = new File(context.getFilesDir(), "vocab");
            list = fileDir.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) return;
        for (String fn : list) {
            if (count != 0) vocFileNames += ",";
            count++;
            vocFileNames += "vocab/" + fn;
        }
        sortWay = getResources().getStringArray(R.array.lesson_search_way);
        final Spinner spinSearch = (Spinner) parentActivity.findViewById(R.id.
                voc_choose_sort_spinner);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        context,
                        R.array.lesson_search_way,
                        R.layout.spinner_item_type1);
        adapter.setDropDownViewResource(R.layout.spinner_item_type1);
        spinSearch.setAdapter(adapter);
        init(vocFileNames);

        spinSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = spinSearch.getSelectedItem().toString();
                for (int i = 0; i < sortWay.length; i++) {
                    if (str.equals(sortWay[i])) {
                        final Spinner subSearch = (Spinner) parentActivity.findViewById(R.id.voc_choose_sub_sort_spinner);
                        switch (i) {
                            case 0:
                                parentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        subSearch.setEmptyView(null);
                                        subSearch.setEnabled(false);
                                        subSearch.setVisibility(INVISIBLE);

                                        ArrayList<VocabularyListUnit> res = new ArrayList<>();
                                        for (int i = 0; i < vocLists.size(); i++) {
                                            res.add(vocLists.get(i));
                                        }
                                        ScrollView courseSelectionView2 =
                                                (ScrollView) parentActivity.findViewById(R.id.vocab_chooser_scroll);
                                        if (res.size() <= 0) {
                                            courseSelectionView2.removeAllViews();
                                        } else {
                                            set(res);
                                        }
                                    }
                                });
                                break;
                            case 1:
                                        /*----------Sort by Level-------------*/
                                subSearch.setVisibility(VISIBLE);
                                subSearch.setEnabled(true);
                                ArrayAdapter<String> gameKindArray =
                                        new ArrayAdapter<String>(context,
                                                R.layout.spinner_item_type1,
                                                getResources().getStringArray(R.array.level_list));
                                gameKindArray.setDropDownViewResource(R.layout.spinner_item_type1);
                                subSearch.setAdapter(gameKindArray);
                                subSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        parentActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ArrayList<VocabularyListUnit> res = new ArrayList<>();
                                                subSearch.setEnabled(true);
                                                String selectedLevel = subSearch.
                                                        getSelectedItem().toString();
                                                for (int i = 0; i < vocLists.size(); i++) {
                                                    String[] formated = vocLists.get(i).getLessonChooserFormatedData().split(",");
                                                    if (formated[3].equals(selectedLevel)) {
                                                        res.add(vocLists.get(i));
                                                    }
                                                }
                                                ScrollView courseSelectionView2 =
                                                        (ScrollView) parentActivity.findViewById(R.id.vocab_chooser_scroll);
                                                if (res.size() <= 0) {
                                                    courseSelectionView2.removeAllViews();
                                                } else {
                                                    set(res);
                                                }

                                            }
                                        });


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        return;
                                    }
                                });
                                break;
                            case 2:
                                        /*----------Sort by Custom Thematic-------------*/

                                /*ArrayAdapter<String> gameKindArray2=
                                        new ArrayAdapter<String>(context(),
                                                R.layout.spinner_item_type1,
                                                listThematics.toArray(
                                                        new String [listThematics.size()]));
                                gameKindArray2.setDropDownViewResource(R.layout.spinner_item_type1);
                                subSearch.setAdapter(gameKindArray2);
                                subSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ArrayList<String> res = new ArrayList<String>();
                                                subSearch.setEnabled(true);
                                                String selectedThematic = subSearch.
                                                        getSelectedItem().toString();
                                                for(int i=0; i<lessonList.size(); i++){
                                                    String[] formated = lessonList.get(i).split(",");
                                                    if(formated[4].equals(selectedThematic)){
                                                        res.add(lessonList.get(i));
                                                    }
                                                }
                                                ListView courseSelectionView2 = (ListView)findViewById(R.id.listView);
                                                if(res.size() <= 0){
                                                    courseSelectionView2.setEmptyView(null);
                                                }
                                                LessonItemAdapter lia = new LessonItemAdapter(getBaseContext(),
                                                        res.toArray(new String[res.size()]));
                                                courseSelectionView2.setAdapter(lia);
                                            }
                                        });


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        return;
                                    }
                                });
                                break;*/

                        }
                        currentSortWay = i;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentSortWay = 0;
                return;
            }
        });

    }

    private void init(String params) {
        String fn[] = params.split(",");
        ArrayList<VocabularyListUnit> res = new ArrayList<>();
        if (vocLists != null) {
            res.addAll(vocLists);
            set(res);
            notLoad = false;
            return;
        }
        vocLists = new ArrayList<>();
        for (int i = 0; i < fn.length; i++) {
            String filenameSeparated[];
            VocabularyListSet vls = null;
            try {
                if (fn[i].contains("/")) {
                    filenameSeparated = fn[i].trim().split("/", 2);
                    File fileDir = new File(myContext.getFilesDir(), filenameSeparated[0]);
                    vls = new VocabularyListSet(myContext,
                            new FileInputStream(new File(fileDir, filenameSeparated[1])), fn[i].trim());
                } else {
                    vls = new VocabularyListSet(myContext,
                            new FileInputStream(new File(myContext.getExternalFilesDir(null),
                                    fn[i])), fn[i].trim());
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            if (vls != null && vls.getVocabularyListCount() > 0) {
                vocLists.addAll(vls.getVocbularyLists());
                res.addAll(vls.getVocbularyLists());
            }
        }
        set(res);
        notLoad = false;
    }

    private void set(ArrayList<VocabularyListUnit> lists) {
        if (lists == null) return;
        LinearLayout layout = null;
        int count = 0;
        LayoutParams param = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 1.0f);
        LayoutParams param2 = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        removeAllViews();
        ScrollView courseSelectionView2 = (ScrollView) parentActivity.findViewById(R.id.vocab_chooser_scroll);
        for (VocabularyListUnit vl : lists) {
            VocabListDisplayedEntry vocDisplay =
                    new VocabListDisplayedEntry(getContext(), parentActivity);
            vocDisplay.set(vl);

            if (count % 2 == 0) {
                layout = new LinearLayout(myContext);
                layout.setWeightSum(2.0f);
                layout.addView(vocDisplay, param);
                if (count == lists.size() - 1) {
                    addView(layout, param2);
                }
            } else {
                if (layout != null) layout.addView(vocDisplay, param);
                addView(layout, param2);
            }
            count++;

        }
        courseSelectionView2.removeAllViews();
        courseSelectionView2.addView(this);
    }

    public void setParentActivity(VocabularyActivity vocActivity) {
        this.parentActivity = vocActivity;
    }
}
