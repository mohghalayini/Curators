package com.example.mohamadghalayini.curators;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbah); //used a toolbar because I prefer it over the default action bar
        myToolbar.setTitle("");
        myToolbar.setSubtitle("");
        setSupportActionBar(myToolbar);
        setupSpinner();

    }
    private void setupSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.FloorSpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.FloorArray, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Intent roomSwitcher = new Intent(MainActivity.this, RoomPopper.class);
                if (position == 1) {
                    roomSwitcher.putExtra("floorValue", "5");
                    startActivity(roomSwitcher);
                    setupSpinner();
                }
                if (position == 2) {
                    roomSwitcher.putExtra("floorValue", "4");
                    startActivity(roomSwitcher);
                    setupSpinner();
                }
                if (position == 3) {
                    roomSwitcher.putExtra("floorValue", "3");
                    startActivity(roomSwitcher);
                    setupSpinner();
                }
                if (position == 4) {
                    roomSwitcher.putExtra("floorValue", "2");
                    startActivity(roomSwitcher);
                    setupSpinner();
                }
                if (position == 5) {
                    roomSwitcher.putExtra("floorValue", "Any");
                    startActivity(roomSwitcher);
                    setupSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

    }
    public boolean onCreateOptionsMenu(Menu menu) {//calls the menu for toolbar
        getMenuInflater().inflate(R.menu.overflowmenu, menu);
        return true;
    }
      @Override
    public boolean onOptionsItemSelected(MenuItem item) {// this is the part the interprets clicks on the overfloow menu items
        switch (item.getItemId()) {
            case R.id.iamAdmin: {
                Intent adminpage =new Intent(this, Admindata.class);
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