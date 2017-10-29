package com.example.mohamadghalayini.curators;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class Admindata extends AppCompatActivity {
    Context thisthing = this;
    ArrayAdapter roomAdapterAdmin;
    String[] allRooms;
    ArrayList<String> outputArray = new ArrayList<String>();
    ListView adminListView;
    LinearLayout headerHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admindata);
        login();
        adminListView = (ListView) findViewById(R.id.adminListView);
        headerHolder = (LinearLayout) findViewById(R.id.headerHolder);
    }

    public void login() {
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
                } else {
                    Intent begone = new Intent(thisthing, MainActivity.class);
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
                Intent begone = new Intent(thisthing, MainActivity.class);
                startActivity(begone);
            }
        });


        builder.show();
    }


    public void roomDisplayer() {
        if (allRooms != null) {
            String actualRoom;
            String roomCurrentSize;
            String roomCapacity;
            for (int i = 0; i < allRooms.length; i++) {
                String str = allRooms[i];
                try {
                    actualRoom = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomCurrentSize = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                    str = str.substring(str.indexOf(";") + 1, str.length());
                    roomCapacity = str.substring(str.indexOf(":") + 1, str.length());
                    outputArray.add(actualRoom + "                       " + roomCurrentSize + "                           " + roomCapacity);
                } catch (Exception e) {
                }
            }

            roomAdapterAdmin = new ArrayAdapter(thisthing, R.layout.admin_listview, outputArray);
            adminListView.setAdapter(roomAdapterAdmin);

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


