package com.example.mohamadghalayini.testingjsoup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

;

public class MainActivity extends AppCompatActivity {
    TextView count;
    TextView rooms;
    TextView errors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        count=(TextView) findViewById(R.id.counter);
        rooms=(TextView) findViewById(R.id.rooms);
        errors=(TextView) findViewById(R.id.Totalerrors);
        errors.setText("0");
        count.setText("0");
        firstthread();
        secondthread();
        thirdthread();
    }

    private class Title extends AsyncTask<Void, Void, Void> {
        String title="";
        boolean errordetected=false;



        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("http://gold-hold-183404.appspot.com/rooms/").get();
                for(Element div : doc.select("h4")){
                     title +=div.text();
                  //  for(Element img : div.select("img")){
                   //     System.out.println(img.attr("src"));
                 //   }
                }
            } catch (IOException e) {
                e.printStackTrace();
                 errordetected=true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!errordetected){
            // Set title into TextView
            String c=count.getText().toString();
            int i=Integer.parseInt(c);
            c= Integer.toString(++i);
            count.setText(c);
            rooms.setText(title);}
            if(errordetected) {
                errordetected = false;
                String errortext = errors.getText().toString();
                int errorcount = Integer.parseInt(errortext);
                errortext = Integer.toString(++errorcount);
                errors.setText(errortext);
            }
        }

    }
    void firstthread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (true) {

                    new Title().execute();
                    synchronized (this) {
                        try {
                            wait(600);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }).start();

    }
    void secondthread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (true) {

                    new Title().execute();
                    synchronized (this) {
                        try {
                            wait(600);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }).start();

    }
    void thirdthread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (true) {

                    new Title().execute();
                    synchronized (this) {
                        try {
                            wait(600);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }).start();

    }
}

