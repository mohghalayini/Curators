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

public class RoomPopper extends AppCompatActivity {
    ArrayAdapter roomAdapter;
    String[]allRooms;
    ListView placeholder;
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
        String floorValue = roomDecoder.getString("floorValue");
        new RoomFetcher().execute();
    }

    public void roomDisplayer(){
        if(allRooms!=null){
            String[] outputArray= new String[allRooms.length];
            String actualRoom;
            String roomCurrentSize;
            String roomCapacity;
            for  (int i=0;i<allRooms.length;i++){
                String str = allRooms[i];
                actualRoom = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                str= str.substring(str.indexOf(";") + 1, str.length());
                roomCurrentSize = str.substring(str.indexOf(":") + 1, str.indexOf(";"));
                str= str.substring(str.indexOf(";") + 1, str.length());
                roomCapacity =str.substring(str.indexOf(":") + 1, str.length());
               outputArray[i]="Room:"+actualRoom+" Current Student Count:"+roomCurrentSize+" Max Capacity"+roomCapacity;
            }
        }
    }

    private class RoomFetcher extends AsyncTask<Void, Void, Void> {
        String rooms="";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("http://gold-hold-183404.appspot.com/rooms/").get();
                for(Element div : doc.select("h4")){
                    rooms +=div.text()+"/";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
           allRooms =rooms.split("/");
            roomDisplayer();
        }
    }
}
