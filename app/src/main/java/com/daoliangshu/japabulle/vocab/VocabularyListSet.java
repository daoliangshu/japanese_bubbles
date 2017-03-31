package com.daoliangshu.japabulle.vocab;

import android.content.Context;
import android.content.res.AssetManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by daoliangshu on 2016/10/9.
 */
public class VocabularyListSet {
    private String fn;
    private ArrayList<VocabularyListUnit> lists;
    private int listIndex = 0;
    private int vocIndex = 0;
    private Context context = null;

    /*-----------------------------------------------*/
    /*-----------------CONSTRUCTORS------------------*/
    /*-----------------------------------------------*/
    public VocabularyListSet(Context context, String filepath) {
        this.context = context;
        this.set(filepath, null);
    }

    public VocabularyListSet(Context context, InputStream is, String filePath) {
        this.context = context;
        this.set(filePath, is);
    }

    public VocabularyListSet(Context context, Element el) {
        this.context = context;
        this.set(el);
    }

    /*-----------------------------------------------*/
    /*-----------------SETTERS-----------------------*/
    /*-----------------------------------------------*/

    /***
     * There is two way for creating a vocabulary list:
     * 1) give a filepath
     * 2) give a InputStream ( eg :  accessing from assets)
     *
     * @param filepath
     * @param is
     */
    public void set(String filepath, InputStream is) {
        this.fn = filepath;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException pe) {
            pe.printStackTrace();
        }
        Document document = null;
        if (builder != null) {
            try {
                if (is == null) {
                    /* Depreciated */
                    if (filepath.contains("/")) {
                        //This part will be removed
                    }
                    AssetManager assetManager = context.getAssets();
                    InputStream ims = assetManager.open(filepath);
                    document = builder.parse(ims);
                } else {
                    document = builder.parse(is);
                    is.close();
                }
            } catch (SAXException sae) {
                sae.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (document != null) {
                Element root = document.getDocumentElement();
                String category = "no";
                if (document.getElementsByTagName("category") != null &&
                        document.getElementsByTagName("category").getLength() > 0) {
                    category = (document.getElementsByTagName("category").
                            item(0)).
                            getTextContent();
                }
                NodeList elements = document.getElementsByTagName("vocabulary");
                lists = new ArrayList<>();
                int indexInSet = 0;
                for (int i = 0; i < elements.getLength(); i++) {
                    Element element = (Element) elements.item(i);
                    VocabularyListUnit vl = new VocabularyListUnit(context, element);
                    vl.setFilePath(this.fn);
                    vl.setVocThematic(category);
                    vl.setIndexInSet(indexInSet);
                    indexInSet++;
                    lists.add(vl);
                }
            }
        }
    }

    public void set(Element vocabList) {
        NodeList elements = vocabList.getElementsByTagName("vocabulary");
        lists = new ArrayList<>();
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            lists.add(new VocabularyListUnit(context, element));
            lists.get(0).setIndexInSet(i);
        }
    }

    /*-----------------------------------------------*/
    /*-----------------UPDATE------------------------*/
    /*-----------------------------------------------*/
    public void next() {
        if (lists != null && lists.size() > 0 && lists.get(listIndex).getSize() > 0) {
            vocIndex = (vocIndex + 1) % lists.get(listIndex).getSize();
        }
    }

    public void previous() {
        vocIndex = vocIndex - 1 < 0 ? lists.get(listIndex).getSize() - 1 : vocIndex - 1;
    }

    public void random() {
        Random rand = new Random();
        vocIndex = rand.nextInt(lists.get(listIndex).getSize());
    }

    public void setListIndex(int indexList) {
        this.listIndex = indexList;
    }


    /*-----------------------------------------------*/
    /*-----------------GETTERS------------------*/
    /*-----------------------------------------------*/
    public String getCurWord() {
        if (lists.get(listIndex).getSize() <= 0) return null;
        return lists.get(listIndex).get(vocIndex).getWord();
    }

    public String getCurTransAll() {
        if (lists.get(listIndex).getSize() > 0) {
            return lists.get(listIndex).get(vocIndex).getAllTrans();
        } else return null;
    }

    public ArrayList<VocabularyUnit> getAll() {
        ArrayList<VocabularyUnit> vocUnits = new ArrayList<>();
        for (VocabularyListUnit vl : this.lists) {
            if (vl.getAll() != null) {
                vocUnits.addAll(vl.getAll());
            }
        }
        return vocUnits;
    }

    public ArrayList<VocabularyListUnit> getVocbularyLists() {
        ArrayList<VocabularyListUnit> notEmptyLists = new ArrayList<>();
        for (VocabularyListUnit vu : this.lists) {
            if (vu.getSize() > 0) {
                notEmptyLists.add(vu);
            }
        }
        return notEmptyLists;
    }

    public int getVocabularyListCount() {
        if (this.lists == null) return 0;
        return this.lists.size();
    }

    public VocabularyListUnit getVocabularyList(int index) {
        return this.lists.get(index);
    }
}


