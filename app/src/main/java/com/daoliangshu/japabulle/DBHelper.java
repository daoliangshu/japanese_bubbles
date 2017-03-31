package com.daoliangshu.japabulle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by daoliangshu on 1/28/17.
 * Class containing the db connection,
 * provides methods to get data from db.
 */

public class DBHelper extends SQLiteOpenHelper {



    /**---- uselesspart --**/
    public final static int SUBST = 0;
    public final static int VERB = 1;
    public final static int OTHER = 2;
    public final static int WORD = 3;


    public final static int RESULT_EXACT = 1;
    public final static int RESULT_POST = 0;


    public final static String COL_ID = "_id";
    public final static String COL_WORD = "zh_1";
    public final static String COL_TRANS = "jp_1";
    public final static String COL_PHON = "jp_2";
    public final static String COL_LESSON = "lesson";
    public final static String TB_BASIC = "dico";

    private SQLiteDatabase myDB;
    private static String DB_PATH;
    private static final String DB_NAME = "japonais_chinois.db";
    private final Context myContext;

    public DBHelper(Context context) throws SQLException {
        super(context, DB_NAME, null, 3);
        myContext = context;
        DB_PATH = myContext.getFilesDir().getPath();
        openDB();
    }

    private void openDB() throws SQLException {
        String path = DB_PATH + "/dic_librefra.db";
        File dbFile = new File(path);
        if (!dbFile.exists() || Settings.REQUEST_UPDATE) {
            try {
                copyDB(dbFile);
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }
        File file = new File(path);
        if (file.exists() && !file.isDirectory()) {
            myDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

            Log.i("DB", "Opend db succesfully !!");

        } else {
            Log.i("ERR", "File not found");
            System.exit(-1);
        }
    }

    public void copyDB(File dbFile) throws IOException {
        //Open local db as input stream
        Log.e("Err0", "Could not open a stream");
        InputStream input = myContext.getAssets().open(DB_NAME);
        //Path to the new created empty db
        Log.e("Info", "Opening:" + dbFile.toString());


        OutputStream output = new FileOutputStream(dbFile);
        Log.e("Err", "Could not open a stream");

        //transfer bytes from inputfile to outputfile
        byte[] buffer = new byte[1024];
        Log.e("Err3", "Could not open a stream");
        while (input.read(buffer) > 0) {
            output.write(buffer);
            System.out.println(buffer.toString());
        }
        Log.i("CopyDB", "OK");
        //close
        output.flush();
        output.close();
        input.close();
    }

    public synchronized void close() {
        if (myDB != null) myDB.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public ArrayList<HashMap<String, String>> getTrans(String colWord) {
        return getTransByStartWith(colWord, 0, 0);
    }


    private boolean entryExist(String table, String colName, String value) {
        try {
            String q = "SELECT _id" +
                    " FROM '" + table + "' WHERE " +
                    colName + " LIKE \"" + value + "\"";
            Cursor c = myDB.rawQuery(q, null);
            Log.i("query", q);
            Log.i("count", String.valueOf(c.getCount()));
            if (c.moveToFirst()) {
                c.close();
                return true;
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }




    public ArrayList<HashMap<String, String>> getTransByStartWith(String zhWord,
                                                                  int mode,
                                                                  int lessonIndex) {
        String query = "SELECT " +
                COL_ID + ", " +
                COL_TRANS + ", " + COL_PHON + ", " + COL_WORD +
                " FROM " + TB_BASIC;
        if (mode == 1)
            query += " WHERE " + COL_WORD + " LIKE '" + zhWord + "%' ";
        else
            query += " WHERE " + COL_WORD + "=" + zhWord;
        if (lessonIndex > 0)
            query += " AND " + COL_LESSON + "=" + lessonIndex;
        query += ";";
        Cursor c = myDB.rawQuery(query, null);
        ArrayList<HashMap<String, String>> resList = new ArrayList<>();
        while (c.moveToNext()) {
            HashMap<String, String> res = new HashMap<>();
            res.put(COL_ID, c.getString(0));
            res.put(COL_TRANS, c.getString(1) != null ? c.getString(1).trim() : c.getString(1));
            res.put(COL_PHON, c.getString(2) != null ? c.getString(2).trim() : c.getString(2));
            res.put(COL_WORD, c.getString(3) != null ? c.getString(3).trim() : c.getString(3));
            resList.add(res);
        }
        c.close();
        return resList;
    }



    // Compability methods
    public String getTransById(int tid){
        String query = "SELECT " +
                        COL_TRANS + " " +
                        "FROM " + TB_BASIC +
                        " WHERE " + COL_ID + "=" + String.format("%d", tid) + ";";
        Cursor c = myDB.rawQuery(query, null);
        while(c.moveToNext()){
            return c.getString(0);
        }
        return null;
    }

    public String getWordById(int tid){
        String query = "SELECT " +
                COL_WORD + " " +
                "FROM " + TB_BASIC +
                " WHERE " + COL_ID + "=" + String.format("%d", tid) + ";";
        Cursor c = myDB.rawQuery(query, null);
        while(c.moveToNext()){
            return c.getString(0);
        }
        return null;
    }

    public ArrayList<Integer> getWordIds(){
        String query = "SELECT " +
                COL_ID + " " +
                "FROM " + TB_BASIC + " " +
                "COUNT 100" + ";";
        Cursor c = myDB.rawQuery(query, null);
        ArrayList<Integer> results = new ArrayList<>();
        while(c.moveToNext()){
            results.add(c.getInt(0));
        }
        return results;
    }

    public String getWordTrans(String zhWord){
        String query = "SELECT " +
                COL_TRANS + " " +
                "FROM " + TB_BASIC + " " +
                "WHERE " + COL_WORD + "=\"" + zhWord.trim() + "\"";
        Cursor c = myDB.rawQuery(query, null);
        while(c.moveToNext()){
            return c.getString(0);
        }
        return null;
    }

    public String getSourceWord(String jpWord){
        String query = "SELECT " +
                COL_WORD + " " +
                "FROM " + TB_BASIC + " " +
                "WHERE " + COL_TRANS + "=\"" + jpWord.trim() + "\"";
        Cursor c = myDB.rawQuery(query, null);
        while(c.moveToNext()){
            return c.getString(0);
        }
        return null;
    }

    public HashMap<String, String> getEntryById(int wid){
        Cursor c = myDB.rawQuery("SELECT " +
                DBHelper.COL_WORD + ", " +
                DBHelper.COL_TRANS +  " " +
                " FROM " + TB_BASIC +
                " WHERE _id=" + wid + ";", null);
        if (c == null) return null;
        if (!c.moveToFirst()) return null;
        HashMap<String, String> res = new HashMap<>();
        res.put(DBHelper.COL_WORD, c.getString(0));
        res.put(DBHelper.COL_TRANS, c.getString(1));
        res.put(COL_ID, String.format("%d", wid));
        return res;
    }
}
