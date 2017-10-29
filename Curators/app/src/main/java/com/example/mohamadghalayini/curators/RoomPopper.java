package com.example.mohamadghalayini.curators;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class RoomPopper extends AppCompatActivity {
    ArrayAdapter[] roomAdapters = new ArrayAdapter[3];
    ListView[] listViews = new ListView[3];
    String[] allRooms;
    ArrayList<String> light = new ArrayList<String>();
    ArrayList<String> normal = new ArrayList<String>();
    ArrayList<String> medium = new ArrayList<String>();
    String floorValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_popper);
        decoder();
        //roomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, outputarray);// here is my adapter for the listview that will hold my courses
        // placeholder = (ListView) findViewById(R.id.mlv);
        // placeholder.setAdapter(myadapter);
    }

    public void decoder() {
        Bundle roomDecoder = getIntent().getExtras();
        floorValue = roomDecoder.getString("floorValue");
        new RoomFetcher().execute();
    }

    public void roomDisplayer() {
        if (allRooms != null) {
            String[] outputArray = new String[allRooms.length];
            String actualRoom = "";
            String roomCurrentSize = "";
            String roomCapacity = "";
            for (int i = 0; i < allRooms.length; i++) {
                String str = allRooms[i];
                try {
                    actualRoom = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomCurrentSize = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomCapacity = str.substring(str.indexOf(":") + 1, str.length());
                    sort(actualRoom, roomCurrentSize, roomCapacity, floorValue);
                } catch (Exception e) {
                }
            }
            if (light != null) {
                listViews[0] = (ListView) findViewById(R.id.firstListView);
                roomAdapters[0] = new ArrayAdapter<String>(this, R.layout.empty_listview, light);
                listViews[0].setAdapter(roomAdapters[0]);
            }
            if (normal != null) {
                listViews[1] = (ListView) findViewById(R.id.secondListView);
                roomAdapters[1] = new ArrayAdapter<String>(this, R.layout.normal_listview, normal);
                listViews[1].setAdapter(roomAdapters[1]);
            }
            if (medium != null) {
                listViews[2] = (ListView) findViewById(R.id.thirdListView);
                roomAdapters[2] = new ArrayAdapter<String>(this, R.layout.busy_listview, medium);
                listViews[2].setAdapter(roomAdapters[2]);
            }
        }
    }

    public void sort(String Room, String Size, String Capacity, String Preference) {

        float size = Integer.parseInt(Size);
        float capacity = Integer.parseInt(Capacity);
        float ratio = 0;
        if (size > 0) {
            ratio = size / capacity;
        }
        if (Preference.equals("Any") || Preference.equals(Room.substring(0, 1))) {
            if (ratio < 0.25) {
                light.add("Room: " + Room);
            } else if (ratio < 0.5) {
                normal.add("Room: " + Room);
            } else if (ratio < 0.75) {
                medium.add("Room: " + Room);
            }
        }
    }

    private class RoomFetcher extends AsyncTask<Void, Void, Void> {
        String rooms = "";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("http://gold-hold-183404.appspot.com/rooms/").get();
                for (Element div : doc.select("h4")) {
                    rooms += div.text() + "/";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            allRooms = rooms.split("/");
            roomDisplayer();
        }
    }
}
