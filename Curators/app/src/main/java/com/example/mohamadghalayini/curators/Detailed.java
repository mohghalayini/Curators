package com.example.mohamadghalayini.curators;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Detailed extends AppCompatActivity {
    String id;
    TextView[] textViews = new TextView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        Intent intehMin = getIntent();
        id = intehMin.getStringExtra("id");
        textViews[0]= (TextView) findViewById(R.id.roomName);
        textViews[1]= (TextView) findViewById(R.id.roomNumber);
        textViews[2]= (TextView) findViewById(R.id.roomStatus);
        new RoomFetcher().execute();
    }

    private class RoomFetcher extends AsyncTask<Void, Void, Void> {
        String actualRoom;
        String roomCurrentSize;
        String roomCapacity;
        String roomStatus;
        String nickname;
        String roomId;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("http://gold-hold-183404.appspot.com/rooms/").get();
                for (Element div : doc.select("h4")) {

                    String str = div.text();
                    actualRoom = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomCurrentSize = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomCapacity = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomStatus = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomId = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    nickname = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    if (roomId.equals(id))
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            float size = Integer.parseInt(roomCurrentSize);
            float capacity = Integer.parseInt(roomCapacity);
            float ratio = 0;
            String status="";
            if (size > 0) {
                ratio = size / capacity;
            }
            if (roomStatus.equals("Active")) {
                if (ratio < 0.25) {
                    status = "Empty";
                } else if (ratio < 0.5) {
                    status = "Normal";
                } else if (ratio < 0.75) {
                    status = "Crowded";
                } else if (ratio <= 1) {
                    status = "Full";
                }
            } else if (roomStatus.equals("Inactive")) {
                status = "Inactive";
            }
            textViews[0].setText(nickname);
            textViews[1].setText("Room number: " + actualRoom);
            textViews[2].setText("Room status: " + status);
        }
    }
}
