package com.example.mohamadghalayini.applicationprototypemc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void gotoRoomPicker(View view){
    Intent gotoRp= new Intent(this, RoomPicker.class);
        startActivity(gotoRp);
    }
}

