package com.example.mohamadghalayini.applicationprototypemc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class SecondFloorRoomStatus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_floor_room_status);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbahinsfr); //used a toolbar because I prefer it over the default action bar
        myToolbar.setTitle("");
        myToolbar.setSubtitle("");
        setSupportActionBar(myToolbar);
    }

    public boolean onCreateOptionsMenu(Menu menu) {//calls the menu for toolbar
        getMenuInflater().inflate(R.menu.overflowmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// this is the part the interprets clicks on the overfloow menu items
        switch (item.getItemId()) {
            case R.id.iamAdmin: {
                Intent adminpriviledge =new Intent(this, admindata.class);
                startActivity(adminpriviledge);

                break;
            }
            case R.id.about: {


                break;
            }

        }
        return false;
    }
}