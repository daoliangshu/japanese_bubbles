package com.daoliangshu.japabulle.vocab;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.daoliangshu.japabulle.DBHelper;
import com.daoliangshu.japabulle.R;
import com.daoliangshu.japabulle.lettrabulle.LettrabulleActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;


/**
 * Created by gigitintin on 04/04/16.
 * This activity provides a flashcard interface
 */
public class VocabularyActivity extends Activity implements TextToSpeech.OnInitListener {


    private ViewFlipper flippy;


    private TextView txtSrc;
    private TextView txtDst;
    private VocabularyListSet vocContainer;
    //private SentenceContainer sentenceContainer;
    private Map<String, String> voc_list;
    private TextToSpeech ttsObj;
    public static final int test = 1234;
    private ArrayList<Integer> id_list;
    private DBHelper dbHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Retrieve the initUnit of vocabularies to practice
         given in bundle */
        //Bundle b = this.getIntent().getExtras();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.vocabulary_layout);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        flippy = (ViewFlipper) findViewById(R.id.vocab_view_flipper);
        try {
            dbHelper = new DBHelper(getApplicationContext());
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }

        // Fire off an intent to check if a TTS engine is installed
        //Intent checkIntent = new Intent();
        //checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        //startActivityForResult(checkIntent, test);

        SpeechRecognizer system = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        system.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        ttsObj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            int result;

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = ttsObj.setLanguage(Locale.FRENCH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not available, attempting download");
                        Intent installIntent = new Intent();
                        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        startActivity(installIntent);
                    }
                } else {
                    Log.e("TTS", "Initialization Failed!");
                }
            }
        }, "com.google.android.tts");


        init();

        id_list = dbHelper.getWordIds();
        try {
            // vocContainer = new VocabularyListSet(getBaseContext(), getAssets().open("vocab/sport_1.vocab"));
            //vocContainer = new VocabularyListSet(getBaseContext(), openFileInput("librefra_vocab/sport_1.vocab"));
            vocContainer = new VocabularyListSet(getBaseContext(),
                    new FileInputStream(new File(getExternalFilesDir("librefra_vocab"), "sport_1.vocab")),
                    "librefra_vocab/sport_1.vocab");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        current();

        VocabChooserView vocabChooserView = new VocabChooserView(getBaseContext(), this);
        vocabChooserView.setParentActivity(this);
        ScrollView sv = (ScrollView) findViewById(R.id.vocab_chooser_scroll);
        flippy.setDisplayedChild(0);

        Button searchSentenceButton = (Button) findViewById(R.id.vocab_search_sentence_btn);
        searchSentenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                TextView sentenceTextView = (TextView) findViewById(R.id.vocab_sentence_textview);
                String word = vocContainer.getCurWord();
                if (sentenceContainer != null && sentenceContainer.getWord().equals(word)) {
                    sentenceContainer.next();
                } else {
                    sentenceContainer = new SentenceContainer(word, dbHelper.getSentences(word));
                }
                sentenceTextView.setText(String.format("%s <=> %s",
                        sentenceContainer.getSentenceFr(),
                        sentenceContainer.getSentenceZh()));
                        */
            }
        });

    }

    @Override
    public void onInit(int i) {
        Log.i("TTS", "Passed TTS init");
    }

    /**
     * Initialize the interface: Button & listeners.
     */
    void init() {
        Button btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i("BUT", "NEXT has been pressed");
                next();
            }
        });
        Button buttonChooseVocabularyList = (Button) findViewById(R.id.btn_goto3);
        buttonChooseVocabularyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChooserVocabulary();
            }
        });
        Button btnPrev = (Button) findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i("BUT", "NEXT has been pressed");
                previous();
            }
        });

        Button btnCheck = (Button) findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i("BUT", "NEXT has been pressed");
                random();
            }
        });


        Button btnSpeak = (Button) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ttsObj.speak(txtDst.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        txtSrc = (TextView) findViewById(R.id.textvSrc1);
        txtDst = (TextView) findViewById(R.id.textvDst);

        Button btnGoTo1 = (Button) findViewById(R.id.btn_goto1);
        btnGoTo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToGame = new Intent(getApplicationContext(), LettrabulleActivity.class);
                ArrayList<VocabularyUnit> vocUnits = vocContainer.getAll();
                if (vocUnits != null) {
                    Parcelable[] passedArray = new Parcelable[vocUnits.size()];
                    for (int i = 0; i < vocUnits.size(); i++) {
                        passedArray[i] = vocUnits.get(i);
                    }
                    goToGame.putExtra("vocUnits", passedArray);
                    startActivity(goToGame);
                }
            }
        });
    }


    /**
     * Load a vocabulary list from a file into a vocabulary container
     *
     * @param dataDirRalativePath at most one "/"
     * @param vocListIndexInSet   The index to find the list into the file(if the file contains several ones)
     */
    public void loadVocList(String dataDirRalativePath, int vocListIndexInSet) {
        try {
            String filenameSeparated[];
            if (dataDirRalativePath.contains("/")) {
                filenameSeparated = dataDirRalativePath.trim().split("/", 2);
                File fileDir = new File(getApplicationContext().getFilesDir(), filenameSeparated[0]);


                vocContainer = new VocabularyListSet(getBaseContext(),
                        new FileInputStream(new File(fileDir, filenameSeparated[1])),
                        dataDirRalativePath);
            } else {
                vocContainer = new VocabularyListSet(getBaseContext(),
                        new FileInputStream(new File(getApplicationContext().getFilesDir(),
                                dataDirRalativePath)),
                        dataDirRalativePath);
            }
            vocContainer.setListIndex(vocListIndexInSet);
            flippy.setDisplayedChild(0);
            //vocContainer = new VocabularyListSet(getBaseContext(), pathToAssets);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Go to next vocabulary entry
     */

    public void current() {
        txtSrc.setText(this.vocContainer.getCurTransAll());
        txtDst.setText(this.vocContainer.getCurWord());
    }

    public void previous() {
        vocContainer.previous();
        txtSrc.setText(this.vocContainer.getCurTransAll());
        txtDst.setText(this.vocContainer.getCurWord());
    }

    public void random() {
        vocContainer.random();
        txtSrc.setText(this.vocContainer.getCurTransAll());
        txtDst.setText(this.vocContainer.getCurWord());
    }

    public void next() {
        vocContainer.next();
        txtSrc.setText(this.vocContainer.getCurTransAll());
        txtDst.setText(this.vocContainer.getCurWord());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == test) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                ttsObj = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
                Toast.makeText(getApplicationContext(), "Installed Now", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onDestroy() {
        if (ttsObj != null) {
            ttsObj.stop();
            ttsObj.shutdown();
        }
        super.onDestroy();
    }

    public void displayChooserVocabulary() {
        if (flippy != null) {
            flippy.setDisplayedChild(1);
        }
    }

    public void copyVoc(String assetFolderName, String newFolderName) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "librefra_voc_list");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory");
                return;
            }
        }
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list(assetFolderName);
        } catch (IOException e) {
            Log.e("vocab", "Failed to get asset file list.", e);
        }
        if (files == null) return;
        for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(assetFolderName + "/" + filename);
                File outFile = new File(getExternalFilesDir(newFolderName + "/"), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}


