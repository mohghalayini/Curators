package com.example.mohamadghalayini.curators;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.Map;

public class AdminData extends AppCompatActivity {
    Context thisthing = this;
    ArrayAdapter roomAdapterAdmin;
    String[] allRooms;
    SwipeRefreshLayout refresher;
    ArrayList<ArrayList<String>> outputArray;
    ListView adminListView[] = new ListView[4];
    LinearLayout headerHolder;
    int counter = 3;
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
            refresher.setRefreshing(true);
            if (isNetworkAvailable()) {
                lastRefresh = System.currentTimeMillis();
                initialiseContainers();
                login();
            } else {
                Toast.makeText(thisthing, "Failed to fetch the rooms, please make sure you're connected to the internet", Toast.LENGTH_SHORT).show();
            }
            refresher.setRefreshing(false);
        }


        initialiseListeners();
    }


    public void login() {
        long millisecondsLeft = (System.currentTimeMillis() - statusesAndPreferences.getLockTime());
        if (millisecondsLeft > 300000) {
            if (statusesAndPreferences.getAdminStatus().equals("basic")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("Please enter your Username and Password");
                LayoutInflater inflater = AdminData.this.getLayoutInflater();
                ((ViewGroup) findViewById(R.id.mainlayout)).setVisibility(View.INVISIBLE);
                View view = (inflater.inflate(R.layout.builder_layout, null));
                builder.setView(view);
                final EditText password = (EditText) view.findViewById(R.id.passwordtwo);
                final EditText username = (EditText) view.findViewById(R.id.usernametwo);

                builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ViewGroup) findViewById(R.id.mainlayout)).setVisibility(View.VISIBLE);
                        String thepassword = password.getText().toString();
                        String theusername = username.getText().toString();
                        if (theusername.equals("1") && thepassword.equals("2")) {
                            dialog.dismiss();
                            new RoomFetcher().execute();
                            headerHolder.setVisibility(View.VISIBLE);
                            statusesAndPreferences.saveAdminStatus("Admin");
                        } else {
                            counter--;
                            if (counter == 0) {
                                Intent begone = new Intent(thisthing, RoomPopper.class);
                                startActivity(begone);
                                statusesAndPreferences.saveLockTimer(System.currentTimeMillis());
                                Toast.makeText(thisthing, "Login locked try again in 5 minutes", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(thisthing, "Wrong Username or Password try again", Toast.LENGTH_SHORT).show();
                                login();
                            }
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
                new RoomFetcher().execute();
                headerHolder.setVisibility(View.VISIBLE);
            }
        } else {
            Intent begone = new Intent(thisthing, RoomPopper.class);
            startActivity(begone);
            Toast.makeText(thisthing, "Still locked come back in:" + Long.toString((300000 - millisecondsLeft) / 1000) + "seconds", Toast.LENGTH_SHORT).show();
        }
    }

    public void roomDisplayer() {
        if (allRooms != null) {
            String actualRoom;
            String roomCurrentSize;
            String roomCapacity;
            String status;
            String id;
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
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    id = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    ;
                    outputArray.get(0).add(actualRoom);
                    outputArray.get(1).add(roomCurrentSize);
                    outputArray.get(2).add(ratio + "%");
                    outputArray.get(3).add(status);
                    outputArray.get(4).add(id);
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

    private class RoomStatusChanger extends AsyncTask<Void, Void, Void> {

        String roomID;
        String status;
        String resetter;

        RoomStatusChanger(String roomId, String newStatus) {
            roomID = roomId;
            status = newStatus;
        }

        RoomStatusChanger(String roomId, int NumberCrusher, String oldStatus) {
            roomID = roomId;
            resetter = Integer.toString(-NumberCrusher);
            status = oldStatus;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Map<String, String> map = new HashMap<String, String>();
                map.put("title", "");
                if (resetter != null) {
                    map.put("counter", resetter);
                } else map.put("counter", "");
                map.put("capacity", "");
                map.put("status", status);

                Document doc = Jsoup.connect("http://gold-hold-183404.appspot.com/rooms/" + roomID + "/edit").data(map).userAgent("Mozilla").post();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            lastRefresh = System.currentTimeMillis();
            adminListView[1].setFocusable(false);
            adminListView[3].setFocusable(false);
            adminListView[1].setClickable(false);
            adminListView[3].setClickable(false);
            initialiseContainers();
            login();
            adminListView[1].setFocusable(true);
            adminListView[3].setFocusable(true);
            adminListView[1].setClickable(true);
            adminListView[3].setClickable(true);
        }
    }

    public void initialiseContainers() {
        new Thread(new Runnable() {
            public void run() {
                outputArray = new ArrayList<ArrayList<String>>();
                for (int i = 0; i < 5; i++) {
                    outputArray.add(new ArrayList<String>());
                }
                outputArray.get(0).add("Room");
                outputArray.get(1).add("Size");
                outputArray.get(2).add("Ratio");
                outputArray.get(3).add("Status");
                outputArray.get(4).add("Ids");
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
                        if (timeDifference > 750) {
                            if (isNetworkAvailable()) {
                                lastRefresh = System.currentTimeMillis();
                                initialiseContainers();
                                new RoomFetcher().execute();
                                initialiseListeners();
                            } else {
                                Toast.makeText(thisthing, "Failed to fetch the rooms, please make sure you're connected to the internet", Toast.LENGTH_SHORT).show();
                            }
                        }
                        refresher.setRefreshing(false);
                    }
                }
        );
    }

    public void roomStatus(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminData.this);
        builder.setPositiveButton("Active", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    new RoomStatusChanger(outputArray.get(4).get(position), "Active").execute();
                } catch (Exception e) {
                }
            }
        });
        builder.setTitle("Room:" + outputArray.get(0).get(position) + " Status");
        builder.setNegativeButton("Inactive", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    new RoomStatusChanger(outputArray.get(4).get(position), "Inactive").execute();
                } catch (Exception e) {
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void counterResetter(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminData.this);
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    new RoomStatusChanger(outputArray.get(4).get(position), Integer.parseInt(outputArray.get(1).get(position)), outputArray.get(3).get(position)).execute();
                } catch (Exception e) {
                }
            }
        });
        builder.setTitle(" Reset counter for Room:" + outputArray.get(0).get(position) + "?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void initialiseListeners() {
        adminListView[3].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                try {
                    if (position != 0) {
                        roomStatus(position);
                    }
                } catch (Exception e) {
                }
            }
        });
        adminListView[1].setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                try {
                    if (position != 0) {
                        counterResetter(position);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}


