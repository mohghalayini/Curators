package com.example.mohamadghalayini.curators;

import android.content.Context;
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
    ListView[] listViews = new ListView[20];
    String[] allRooms;
    ArrayList<ArrayList<ArrayList<String>>> floorContainer;//floor container
    String floorValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_popper);
        initialiseLists(this);
        initialiseContainers();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.roomPoppertoolbar); //used a toolbar because I prefer it over the default action bar
        myToolbar.setTitle("");
        myToolbar.setSubtitle("");
        setSupportActionBar(myToolbar);
        decoder();
    }

    public void decoder() {
        floorValue = "Any";
        new RoomFetcher().execute();
    }

    public void roomDisplayer() {
        if (allRooms != null) {
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
                    int floor = Integer.parseInt(actualRoom.substring(0, 1)) - 2;

                    sort(actualRoom, roomCurrentSize, roomCapacity, floorValue, floor);
                } catch (Exception e) {
                }
            }
            int counter = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 5; j++) {
                    if (floorContainer.get(i).get(j) != null) {
                        listViews[counter].setAdapter(findAdapter(i, j));
                    }
                    counter++;
                }
            }
        }
    }

    public void sort(String Room, String Size, String Capacity, String Preference, int floor) {

        float size = Integer.parseInt(Size);
        float capacity = Integer.parseInt(Capacity);
        float ratio = 0;
        if (size > 0) {
            ratio = size / capacity;
        }
        if (Preference.equals("Any") || Preference.equals(Room.substring(0, 1))) {
            if (ratio < 0.25) {
                floorContainer.get(floor).get(0).add("Room: " + Room);
            } else if (ratio < 0.5) {
                floorContainer.get(floor).get(1).add("Room: " + Room);
            } else if (ratio < 0.75) {
                floorContainer.get(floor).get(2).add("Room: " + Room);
            } else if (ratio <= 1) {
                floorContainer.get(floor).get(3).add("Room: " + Room);
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
                Intent adminpage = new Intent(this, AdminData.class);
                startActivity(adminpage);

                break;
            }
            case R.id.about: {

                break;
            }

        }
        return false;
    }

    public ArrayAdapter<String> findAdapter(int floor, int condition) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.empty_listview, floorContainer.get(floor).get(condition));
        if (condition == 1) {
            adapter = new ArrayAdapter<String>(this, R.layout.normal_listview, floorContainer.get(floor).get(condition));
        } else if (condition == 2) {
            adapter = new ArrayAdapter<String>(this, R.layout.busy_listview, floorContainer.get(floor).get(condition));
        } else if (condition == 3) {
            adapter = new ArrayAdapter<String>(this, R.layout.full_listview, floorContainer.get(floor).get(condition));
        } else if (condition == 4) {
            adapter = new ArrayAdapter<String>(this, R.layout.unavailable_listview, floorContainer.get(floor).get(condition));
        }
        return adapter;
    }

    public void initialiseLists(final Context thiscontext) {
        new Thread(new Runnable() {
            public void run() {

                listViews[0] = (ListView) findViewById(R.id.secondFloorFirstListView);
                listViews[1] = (ListView) findViewById(R.id.secondFloorSecondListView);
                listViews[2] = (ListView) findViewById(R.id.secondFloorThirdListView);
                listViews[3] = (ListView) findViewById(R.id.secondFloorFourthListView);
                listViews[4] = (ListView) findViewById(R.id.secondFloorFifthListView);
                //3rd floor
                listViews[5] = (ListView) findViewById(R.id.thirdFloorFirstListView);
                listViews[6] = (ListView) findViewById(R.id.thirdFloorSecondListView);
                listViews[7] = (ListView) findViewById(R.id.thirdFloorThirdListView);
                listViews[8] = (ListView) findViewById(R.id.thirdFloorFourthListView);
                listViews[9] = (ListView) findViewById(R.id.thirdFloorFifthListView);
                //4rth floor
                listViews[10] = (ListView) findViewById(R.id.fourthFloorFirstListView);
                listViews[11] = (ListView) findViewById(R.id.fourthFloorSecondListView);
                listViews[12] = (ListView) findViewById(R.id.fourthFloorThirdListView);
                listViews[13] = (ListView) findViewById(R.id.fourthFloorFourthListView);
                listViews[14] = (ListView) findViewById(R.id.fourthFloorFifthListView);
                //5th floor
                listViews[15] = (ListView) findViewById(R.id.fifthFloorFirstListView);
                listViews[16] = (ListView) findViewById(R.id.fifthFloorSecondListView);
                listViews[17] = (ListView) findViewById(R.id.fifthFloorthirdListView);
                listViews[18] = (ListView) findViewById(R.id.fifthFloorFourthListView);
                listViews[19] = (ListView) findViewById(R.id.fifthFloorFifthListView);
            }
        }).start();

    }

    public void initialiseContainers() {
        new Thread(new Runnable() {
            public void run() {
                floorContainer = new ArrayList<ArrayList<ArrayList<String>>>();
                for (int i = 0; i < 4; i++) {
                    floorContainer.add(new ArrayList<ArrayList<String>>());
                    for (int j = 0; j < 5; j++) {
                        floorContainer.get(i).add(new ArrayList<String>());
                    }
                }
            }
        }).start();
    }
}
