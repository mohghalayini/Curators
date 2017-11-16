package com.example.mohamadghalayini.curators;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdminData extends AppCompatActivity {
    Context thisthing = this;
    ArrayAdapter roomAdapterAdmin;
    String[] allRooms;
    SwipeRefreshLayout refresher;
    ArrayList<ArrayList<String>> outputArray;
    ListView adminListView[] = new ListView[4];
    LinearLayout headerHolder;
    long timeDifference = 0;
    long lastRefresh = 0;
    SharedPreferenceHelper statusesAndPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admindata);
        initialiseRefresher();
        statusesAndPreferences = new SharedPreferenceHelper(this);
        headerHolder = (LinearLayout) findViewById(R.id.headerHolder);
    }

    @Override
    public void onResume() {
        super.onResume();
        timeDifference = System.currentTimeMillis() - lastRefresh;
        if (timeDifference > 550) {
            lastRefresh = System.currentTimeMillis();
            initialiseContainers();
            login();
        }
    }

    public void login() {
        if (statusesAndPreferences.getAdminStatus().equals("basic")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Please enter your Username and Password");
            View viewInflated = LayoutInflater.from(this).inflate(R.layout.activity_admindata, (ViewGroup) findViewById(R.id.Loginlayout), false);
            ((ViewGroup) findViewById(R.id.mainlayout)).setVisibility(View.INVISIBLE);
            final EditText password = (EditText) viewInflated.findViewById(R.id.Password);
            final EditText username = (EditText) viewInflated.findViewById(R.id.username);
            builder.setView(viewInflated);

            builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((ViewGroup) findViewById(R.id.mainlayout)).setVisibility(View.VISIBLE);

                    String thepassword = password.getText().toString();
                    String theusername = username.getText().toString();
                    findViewById(R.id.Password).setVisibility(View.INVISIBLE);
                    findViewById(R.id.username).setVisibility(View.INVISIBLE);
                    if (theusername.equals("1") && thepassword.equals("2")) {
                        dialog.dismiss();
                        new RoomFetcher().execute();
                        headerHolder.setVisibility(View.VISIBLE);
                        statusesAndPreferences.saveAdminStatus("Admin");
                    } else {
                        Intent begone = new Intent(thisthing, RoomPopper.class);
                        startActivity(begone);
                        Toast.makeText(thisthing, "Wrong Username or Password try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((ViewGroup) findViewById(R.id.mainlayout)).setVisibility(View.VISIBLE);
                    dialog.cancel();
                    Intent begone = new Intent(thisthing, RoomPopper.class);
                    startActivity(begone);
                }
            });


            builder.show();
        } else {
            findViewById(R.id.Password).setVisibility(View.INVISIBLE);
            findViewById(R.id.username).setVisibility(View.INVISIBLE);
            new RoomFetcher().execute();
            headerHolder.setVisibility(View.VISIBLE);
            statusesAndPreferences.saveAdminStatus("Admin");
        }

    }

    public void roomDisplayer() {
        if (allRooms != null) {
            String actualRoom;
            String roomCurrentSize;
            String roomCapacity;
            String status;
            for (int i = 0; i < allRooms.length; i++) {
                String str = allRooms[i];
                try {
                    actualRoom = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomCurrentSize = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomCapacity = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    status = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    double ratio = ((double) Integer.parseInt(roomCurrentSize)) / ((double) Integer.parseInt(roomCapacity));
                    ratio = Double.parseDouble(new DecimalFormat("##.##").format(ratio * 100));
                    ;
                    outputArray.get(0).add(actualRoom);
                    outputArray.get(1).add(roomCurrentSize);
                    outputArray.get(2).add(ratio + "%");
                    outputArray.get(3).add(status);
                } catch (Exception e) {
                }
            }
            for (int i = 0; i < 4; i++) {
                roomAdapterAdmin = new ArrayAdapter(thisthing, R.layout.admin_listview, outputArray.get(i));
                adminListView[i].setAdapter(roomAdapterAdmin);
                setListViewHeightBasedOnChildren(adminListView[i]);
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

    public void initialiseContainers() {
        new Thread(new Runnable() {
            public void run() {
                outputArray = new ArrayList<ArrayList<String>>();
                for (int i = 0; i < 4; i++) {
                    outputArray.add(new ArrayList<String>());
                }
                outputArray.get(0).add("Room");
                outputArray.get(1).add("Size");
                outputArray.get(2).add("Ratio");
                outputArray.get(3).add("Status");
                adminListView[0] = (ListView) findViewById(R.id.roomName);
                adminListView[1] = (ListView) findViewById(R.id.roomSize);
                adminListView[2] = (ListView) findViewById(R.id.roomOcc);
                adminListView[3] = (ListView) findViewById(R.id.roomStatus);

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

    public void initialiseRefresher() {
        refresher = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refresher.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        timeDifference = System.currentTimeMillis() - lastRefresh;
                        if (timeDifference > 550) {
                            lastRefresh = System.currentTimeMillis();
                            initialiseContainers();
                            new RoomFetcher().execute();
                        }
                        refresher.setRefreshing(false);
                    }
                }
        );
    }
}


