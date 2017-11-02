package com.example.mohamadghalayini.curators;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.roomPoppertoolbar); //used a toolbar because I prefer it over the default action bar
        myToolbar.setTitle("");
        myToolbar.setSubtitle("");
        setSupportActionBar(myToolbar);
        decoder();
        //roomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, outputarray);// here is my adapter for the listview that will hold my courses
        // placeholder = (ListView) findViewById(R.id.mlv);
        // placeholder.setAdapter(myadapter);
    }

    public void decoder() {
        floorValue = "Any";
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
    public boolean onCreateOptionsMenu(Menu menu) {//calls the menu for toolbar
        getMenuInflater().inflate(R.menu.overflowmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// this is the part the interprets clicks on the overfloow menu items
        switch (item.getItemId()) {
            case R.id.iamAdmin: {
                Intent adminpage =new Intent(this, AdminData.class);
                startActivity(adminpage);

                break;
            }
            case R.id.about: {

                break;
            }

        }
        return false;
    }
}
