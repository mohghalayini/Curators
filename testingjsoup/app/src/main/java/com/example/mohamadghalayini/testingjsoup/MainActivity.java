package com.example.mohamadghalayini.testingjsoup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Title().execute();
    }

    private class Title extends AsyncTask<Void, Void, Void> {
        String title="";



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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT).show();
        }

    }

}

