package com.example.admin2.myvocabulary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static final String TAG = "Memorizer";

    private TextView topTextMessage;
    private TextView bottomTextMessage;
    private static List<Couple> coples ;
    private static SynContainer container;
    private static List<Collocation> words;
    private int index = 0;
    private Langs lang;
    private static final int READ_REQUEST_CODE = 42;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_translate:
                    mTextMessage.setText(R.string.title_translate);
                    showTranslation();
                    return true;
                case R.id.navigation_open:
                    mTextMessage.setText(R.string.title_open);
                    openDialog();
                    return true;
                case R.id.navigation_togle:
                    mTextMessage.setText(R.string.title_togle);
                    if (lang == Langs.ENG) {
                        lang = Langs.RUS;
                        return true;
                    }
                    if (lang == Langs.RUS) {
                        lang = Langs.ENG;
                        return true;
                    }
                case R.id.navigation_next:
                    mTextMessage.setText(R.string.title_next);
                    showNext();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coples = new ArrayList<>();;
        words = new ArrayList<>();
        container = new SynContainer();;

        setContentView(R.layout.activity_main);

        lang = Langs.RUS;

        mTextMessage = (TextView) findViewById(R.id.message);

        topTextMessage = (TextView) findViewById(R.id.textView4);
        bottomTextMessage = (TextView) findViewById(R.id.textView6);
        topTextMessage.setText("");
        bottomTextMessage.setText("");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "requestCode: " + requestCode + "  resultCode: " + resultCode + " RESULT_OK: " + Activity.RESULT_OK);
        Uri uri = null;

        switch(requestCode){
            case READ_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK){
                    String FilePath = data.getData().getPath();
                    uri = data.getData();
                     mTextMessage.setText(FilePath);
                    try {
                        fillFilteredContainers(uri);
                        synSeek();
                      //  mTextMessage.setText(FilePath);
                        topTextMessage.setText(coples.get(0).getEnglish());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }
    private void openDialog(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent,READ_REQUEST_CODE);
    }
    private static int random(int min, int max) {
        Log.i(TAG, "min: " + min + "  max: " + max );
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
    public static String toEnglish(String s) {
        char[] c = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < c.length; i++) {
            if (c[i] == '.') break;
            if ((int)c[i] < 123) sb.append(c[i]);
        }
        return sb.toString().trim();
    }
    public static String toRussian(String s) {
        char[] c = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < c.length; i++) {
            if (c[i] == '.') break;
            if ((int)c[i] == 32 | (int)c[i] > 123 ) sb.append(c[i]);
        }
        return sb.toString().trim();
    }
    private  void fillFilteredContainers(Uri uri) throws IOException {
        coples.clear();
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if(lang == Langs.ENG) {
                coples.add(new Couple(toEnglish(line), toRussian(line)));
            }
            else {
                coples.add(new Couple(toRussian(line),toEnglish(line)));
            }
        }
        inputStream.close();
        reader.close();

    }
    public static void synSeek() {
        List<Couple> tracker = new ArrayList<>(coples);
        for (int i = 0; i < coples.size(); i++) {
            String eng = coples.get(i).getEnglish();
            String rus = coples.get(i).getRussian();
            Collocation col = new Collocation(rus);
            col.addSyn(eng);
            for (int k = i; k < tracker.size(); k++) {
                if (tracker.get(k).getRussian().equals(rus)) {
                    col.addSyn(tracker.get(k).getEnglish());
                }
            }
            container.add(col);
        }
        words.clear();
        for( Collocation c : container.getCollocatins() ){
            words.add(c);
        }
    }
    private  void fillContainers(Uri uri) throws IOException {
        coples.clear();
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            coples.add(new Couple(toEnglish(line), toRussian(line)));
        }
        inputStream.close();
        reader.close();
    }
    private void showNext() {
        if (words.size() > 0){
            index = random(0,container.getCollocatins().size()-1);
            bottomTextMessage.setText( "" );
            String text = (lang == Langs.RUS) ?  words.get(index).getOriginal() : words.get(index).getSynonims().toString();
            text = text.replace("[", "").replaceAll("]", "");
            topTextMessage.setText( text );
        }else{
            openDialog();
        }
    }
    private void showTranslation() {
        if (words.size() > 0) {
            String orig = words.get(index).getOriginal().replace("[", "").replaceAll("]", "");
            String syn = words.get(index).getSynonims().toString().replace("[", "").replaceAll("]", "");
            String text = (lang == Langs.RUS) ? syn : orig;
            bottomTextMessage.setText(text);
        }else{
            bottomTextMessage.setText("");
        }
    }


}
