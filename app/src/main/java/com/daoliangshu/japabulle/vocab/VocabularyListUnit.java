package com.daoliangshu.japabulle.vocab;


import android.content.Context;


import com.daoliangshu.japabulle.R;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by daoliangshu on 2016/10/9.
 */
public class VocabularyListUnit {
    private ArrayList<VocabularyUnit> vocabularies;
    private String filePath = "";
    private String vocListName = "no_name";
    private String vocThematic = "no";
    private int indexInSet = 0;
    private String level;
    private String thematic;
    private String titleFr;
    private String titleZh;


    /*-----------------------------------------------*/
    /*-----------------CONSTRUCTORS------------------*/
    /*-----------------------------------------------*/
    public VocabularyListUnit(Context context, Element vocListUnit) {
        NodeList elements = vocListUnit.getElementsByTagName("item");
        vocabularies = new ArrayList<>();
        for (int i = 0; i < elements.getLength(); i++) {
            Element vocUnit = (Element) elements.item(i);
            vocabularies.add(new VocabularyUnit(context, vocUnit));
        }
        if (vocListUnit.getElementsByTagName("level") != null &&
                vocListUnit.getElementsByTagName("level").getLength() > 0) {
            int levelInt = Integer.parseInt(vocListUnit.
                    getElementsByTagName("level").item(0).getTextContent());
            switch (levelInt) {
                case 0:
                    this.level = context.getResources().getString(R.string.beginner);
                    break;
                case 1:
                    this.level = context.getResources().getString(R.string.intermediate);
                    break;
                case 2:
                    this.level = context.getResources().getString(R.string.advanced);
                    break;
                default:
                    this.level = context.getResources().getString(R.string.beginner);
            }
        } else {
            this.level = context.getResources().getString(R.string.beginner);
        }
        if (vocListUnit.getElementsByTagName("title_fr") != null &&
                vocListUnit.getElementsByTagName("title_fr").getLength() > 0) {
            this.titleFr = vocListUnit.
                    getElementsByTagName("title_fr").item(0).getTextContent();
        } else {
            this.titleFr = "[pas de titre]";
        }
        if (vocListUnit.getElementsByTagName("title_zh") != null &&
                vocListUnit.getElementsByTagName("title_zh").getLength() > 0) {
            this.titleZh = vocListUnit.
                    getElementsByTagName("title_zh").item(0).getTextContent();
        } else {
            this.titleZh = "[pas de titre]";
        }
        if (vocListUnit.hasAttribute("thematic")) {
            thematic = vocListUnit.getAttribute("thematic");
        } else {
            thematic = context.getResources().getString(R.string.default_lesson_thematic);
        }
    }

    /*-----------------------------------------------*/
    /*-----------------SETTERS-----------------------*/
    /*-----------------------------------------------*/
    public void setFilePath(String path) {
        this.filePath = path;
    }

    public void setVocThematic(String thematic) {
        this.vocThematic = thematic;
    }

    public void setIndexInSet(int index) {
        this.indexInSet = index;
    }

    /*-----------------------------------------------*/
    /*-----------------GETTERS-----------------------*/
    /*-----------------------------------------------*/
    public VocabularyUnit get(int index) {
        return (index < vocabularies.size()) &&
                (index >= 0) ? vocabularies.get(index) : null;
    }

    public int getSize() {
        return vocabularies.size();
    }

    public ArrayList<VocabularyUnit> getAll() {
        return vocabularies;
    }

    public String getTitle() {
        return this.titleFr;
    }

    public String getTitleZh() {
        return this.titleZh;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getThematic() {
        return this.vocThematic;
    }

    public String getLevel() {
        return this.level;
    }

    public int getIndexInSet() {
        return this.indexInSet;
    }

    public String getLessonChooserFormatedData() {
        if (this.vocabularies == null || this.vocabularies.size() <= 0) return null;
        return String.format(Locale.ENGLISH,
                "%s,%s,%s,%s,%s,%s",
                getTitle(),
                getTitle(),
                getFilePath(),
                getLevel(),
                getThematic(),
                String.valueOf(getIndexInSet())
        );
    }


}
