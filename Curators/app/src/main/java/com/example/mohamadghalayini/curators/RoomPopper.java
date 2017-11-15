package com.example.mohamadghalayini.curators;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class RoomPopper extends AppCompatActivity {
    ListView[] listViews = new ListView[20];
    int floorClick[] = {0, 0, 0, 0};
    String[] allRooms;
    int heights[] = {0, 0, 0, 0};
    Boolean firstTime = true;
    SwipeRefreshLayout swipeLayout;
    SharedPreferenceHelper preferences;
    char[] roomPreference = new char[4];
    TextView[] floorTexts = new TextView[4];
    ArrayList<ArrayList<ArrayList<String>>> floorContainer;//floor container

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_popper);
        initialiseLists(this);
        initialiseRefresher();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.roomPoppertoolbar); //used a toolbar because I prefer it over the default action bar
        myToolbar.setTitle("");
        myToolbar.setSubtitle("");
        setSupportActionBar(myToolbar);
        preferences = new SharedPreferenceHelper(this);
        getRoomStatuses();

    }

    public void onResume() {
        super.onResume();
        initialiseContainers();
        new RoomFetcher().execute();
        if (firstTime) {
            firstTime = false;
            Toast.makeText(this, "Click on a floor to expand or hide its rooms", Toast.LENGTH_LONG).show();
        }
    }


    public void roomDisplayer() {
        if (allRooms != null) {
            String actualRoom;
            String roomCurrentSize;
            String roomCapacity;
            String nickname;
            String roomStatus;
            for (int i = 0; i < allRooms.length; i++) {
                String str = allRooms[i];
                try {
                    actualRoom = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomCurrentSize = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomCapacity = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomStatus = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    nickname=str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    int floor = Integer.parseInt(actualRoom.substring(0, 1)) - 2;

                    sort(actualRoom, roomCurrentSize, roomCapacity, floor, roomStatus,nickname);
                } catch (Exception e) {
                }
            }
            connectTheViews();
        }
    }

    public void sort(String Room, String Size, String Capacity, int floor, String status,String nickname) {

        float size = Integer.parseInt(Size);
        float capacity = Integer.parseInt(Capacity);
        float ratio = 0;
        if (size > 0) {
            ratio = size / capacity;
        }
        if (status.equals("Active")) {
            if (ratio < 0.25) {
                floorContainer.get(floor).get(0).add("Room: " + Room+"-"+nickname);
            } else if (ratio < 0.5) {
                floorContainer.get(floor).get(1).add("Room: " + Room+"-"+nickname);
            } else if (ratio < 0.75) {
                floorContainer.get(floor).get(2).add("Room: " + Room+"-"+nickname);
            } else if (ratio <= 1) {
                floorContainer.get(floor).get(3).add("Room: " + Room+"-"+nickname);
            }
        } else if (status.equals("Inactive")) {
            floorContainer.get(floor).get(4).add("Room: " + Room+"-"+nickname);
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
        for (int i = 0; i < 4; i++) {
            if (roomPreference[i] == '1') {
                menu.getItem(i + 2).setChecked(true);
            }
        }
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
            case R.id.secondFloorCheck: {
                if (item.isChecked()) {
                    item.setChecked(false);
                    roomPreference[0] = '0';
                    preferences.saveRoomPreference(new String(roomPreference));
                } else {
                    item.setChecked(true);
                    roomPreference[0] = '1';
                    preferences.saveRoomPreference(new String(roomPreference));
                }
                connectTheViews();
                break;
            }
            case R.id.thirdFloorCheck: {
                if (item.isChecked()) {
                    item.setChecked(false);
                    roomPreference[1] = '0';
                    preferences.saveRoomPreference(new String(roomPreference));
                } else {
                    item.setChecked(true);
                    roomPreference[1] = '1';
                    preferences.saveRoomPreference(new String(roomPreference));
                }
                connectTheViews();
                break;
            }
            case R.id.fourthFloorCheck: {
                if (item.isChecked()) {
                    item.setChecked(false);
                    roomPreference[2] = '0';
                    preferences.saveRoomPreference(new String(roomPreference));
                } else {
                    item.setChecked(true);
                    roomPreference[2] = '1';
                    preferences.saveRoomPreference(new String(roomPreference));
                }
                connectTheViews();
                break;
            }
            case R.id.fifthFloorCheck: {
                if (item.isChecked()) {
                    item.setChecked(false);
                    roomPreference[3] = '0';
                    preferences.saveRoomPreference(new String(roomPreference));
                } else {
                    item.setChecked(true);
                    roomPreference[3] = '1';
                    preferences.saveRoomPreference(new String(roomPreference));
                }
                connectTheViews();
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
                floorTexts[0] = (TextView) findViewById(R.id.secondFloorText);
                floorTexts[1] = (TextView) findViewById(R.id.thirdFloorText);
                floorTexts[2] = (TextView) findViewById(R.id.fourthFloorText);
                floorTexts[3] = (TextView) findViewById(R.id.fifthFloorText);
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void getRoomStatuses() {
        String pref = preferences.getRoomPreference();
        roomPreference[0] = pref.charAt(0);
        roomPreference[1] = pref.charAt(1);
        roomPreference[2] = pref.charAt(2);
        roomPreference[3] = pref.charAt(3);

    }

    public void connectTheViews() {
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            if (floorContainer.get(i).get(0).size() != 0 || floorContainer.get(i).get(1).size() != 0 || floorContainer.get(i).get(2).size() != 0 || floorContainer.get(i).get(3).size() != 0) {
                floorTexts[i].setVisibility(View.VISIBLE);
            }
            if (roomPreference[i] == '1') {
                if (floorContainer.get(i).get(0).size() != 0 || floorContainer.get(i).get(1).size() != 0 ||
                        floorContainer.get(i).get(2).size() != 0 || floorContainer.get(i).get(3).size() != 0 ||
                        floorContainer.get(i).get(4).size() != 0) {
                    floorTexts[i].setVisibility(View.VISIBLE);
                } else floorTexts[i].setVisibility(View.GONE);
                if (floorClick[i] == 1) {
                    for (int j = 0; j < 5; j++) {
                        counter = (5 * i) + j;
                        listViews[counter].setVisibility(View.VISIBLE);
                        listViews[counter].setAdapter(findAdapter(i, j));
                        setListViewHeightBasedOnChildren(listViews[counter]);
                    }
                } else {
                    for (int j = 0; j < 5; j++) {
                        counter = (5 * i) + j;
                        listViews[counter].setVisibility(View.GONE);
                    }
                }
            } else {
                floorTexts[i].setVisibility(View.GONE);
                for (int j = 0; j < 5; j++) {
                    counter = (5 * i) + j;
                    listViews[counter].setVisibility(View.GONE);
                }
            }

        }
        setButtonHeight();
    }

    public void initialiseRefresher() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        initialiseContainers();
                        new RoomFetcher().execute();
                        swipeLayout.setRefreshing(false);
                    }
                }
        );
    }

    public void hideOrShow(View view) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (view.getId() == R.id.secondFloorText) {
            if (floorClick[0] == 0) {
                floorClick[0] = 1;
            } else {
                floorClick[0] = 0;
            }
        } else if (view.getId() == R.id.thirdFloorText) {
            if (floorClick[1] == 0) {
                floorClick[1] = 1;
            } else {
                floorClick[1] = 0;
            }
        } else if (view.getId() == R.id.fourthFloorText) {
            if (floorClick[2] == 0) {
                floorClick[2] = 1;
            } else {
                floorClick[2] = 0;
            }
        } else if (view.getId() == R.id.fifthFloorText) {
            if (floorClick[3] == 0) {
                floorClick[3] = 1;
            } else {
                floorClick[3] = 0;
            }
        }
        connectTheViews();
    }

    public void setButtonHeight() {
        for (int i = 0; i < 4; i++) {
            TextView text = null;
            switch (i) {
                case 0:
                    text = (TextView) findViewById(R.id.secondFloorText);
                    break;
                case 1:
                    text = (TextView) findViewById(R.id.thirdFloorText);
                    break;
                case 2:
                    text = (TextView) findViewById(R.id.fourthFloorText);
                    break;
                case 3:
                    text = (TextView) findViewById(R.id.fifthFloorText);
            }

            if (floorClick[i] == 1) {
                int counter ;
                int finalHeight = 0;
                for (int j = 0; j < 5; j++) {
                    counter = (5 * i) + j;
                    ViewGroup.LayoutParams params = listViews[counter].getLayoutParams();
                    finalHeight += params.height;
                }
                LinearLayout.LayoutParams parameter = (LinearLayout.LayoutParams) text.getLayoutParams();
                parameter.height = finalHeight;
                text.setLayoutParams(parameter);
            } else {
                if (heights[i] == 0) {
                    LinearLayout.LayoutParams parameter = (LinearLayout.LayoutParams) text.getLayoutParams();
                    heights[i] = parameter.height;
                } else {
                    LinearLayout.LayoutParams parameter = (LinearLayout.LayoutParams) text.getLayoutParams();
                    parameter.height = heights[i];
                    text.setLayoutParams(parameter);

                }
            }
        }

    }

}
