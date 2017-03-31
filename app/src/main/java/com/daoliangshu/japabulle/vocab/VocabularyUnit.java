package com.daoliangshu.japabulle.vocab;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;


import com.daoliangshu.japabulle.DBHelper;

import org.w3c.dom.Element;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Represents a vocabulary unit
 * Created by daoliangshu on 2016/10/9.
 */
public class VocabularyUnit implements Parcelable {
    private int wid = -1;
    private String word;
    private int DB_TYPE = -1;
    private int index = -1;
    private ArrayList<Integer> tids;
    private ArrayList<String> trans;

    /*-----------------------------------------------*/
    /*-----------------CONSTRUCTOR------------------*/
    /*-----------------------------------------------*/

    /**
     * Extract information from an item in a vocabulary list
     *
     * @param vocUnit an ET.Element with tag 'item'
     */
    public VocabularyUnit(Context context, Element vocUnit) {
        index = Integer.parseInt(vocUnit.getAttribute("index"));
        String tmpWID[] = vocUnit.getElementsByTagName("wid").item(0).getTextContent().split(",");

        wid = Integer.parseInt(tmpWID[0]);
        if (tmpWID.length == 1) {
            DB_TYPE = DBHelper.SUBST;
        } else {
            DB_TYPE = Integer.parseInt(tmpWID[1]);
        }

        DBHelper dbHelper;
        try{
            dbHelper = new DBHelper(context);
        }catch(SQLException sqle){
            sqle.printStackTrace();
            return;
        }
        word = dbHelper.getWordById(wid);
        String tmpTID[] = vocUnit.getElementsByTagName("tid").item(0).getTextContent().split(",");
        tids = new ArrayList<>();
        trans = new ArrayList<>();
        for (int i = 0; i < tmpTID.length; i++) {
            tids.add(Integer.parseInt(tmpTID[i]));
            trans.add(dbHelper.getTransById(tids.get(i)));
        }
    }

    public VocabularyUnit(Parcel in) {
        this.wid = Integer.parseInt(in.readString());
        this.word = in.readString();
        this.DB_TYPE = Integer.parseInt(in.readString());
        this.index = Integer.parseInt(in.readString());
        this.tids = in.readArrayList(null);
        this.trans = in.readArrayList(null);
    }

    /*-----------------------------------------------*/
    /*-----------------GETTERS-----------------------*/
    /*-----------------------------------------------*/
    public String getWord() {
        return this.word;
    }

    public ArrayList<Integer> getTids() {
        return this.tids;
    }

    public int getTid(int index) {
        return (index < tids.size()) && (index >= 0) ? tids.get(index) : -1;
    }

    public String getTrans(int index) {
        return (index < tids.size()) && (index >= 0) ? trans.get(index) : null;
    }

    public String getAllTrans() {
        String tmp = "";
        for (int i = 0; i < this.trans.size(); i++) {
            if (i != 0) {
                tmp += ",";
            }
            tmp += trans.get(i);
        }
        return tmp;
    }

    public ArrayList<String> getTrans() {
        return trans;
    }

    @Override
    public String toString() {
        return "<VocabularyUnit>word:" + this.word + "(" + this.wid + ")" +
                this.trans + "(" + this.tids + ")</VocabularyUnit>";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(String.valueOf(this.wid));
        dest.writeString(this.word);
        dest.writeString(String.valueOf(this.DB_TYPE));
        dest.writeString(String.valueOf(index));
        dest.writeList(this.tids);
        dest.writeList(this.trans);
    }

    public static Creator<VocabularyUnit> CREATOR = new Creator<VocabularyUnit>() {
        @Override
        public VocabularyUnit createFromParcel(Parcel source) {
            return new VocabularyUnit(source);
        }

        @Override
        public VocabularyUnit[] newArray(int size) {
            return new VocabularyUnit[size];
        }
    };
}
