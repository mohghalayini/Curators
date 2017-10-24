package com.example.mohamadghalayini.curators;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class admindata extends AppCompatActivity {
    Context thisthing= this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admindata);
        login();

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
                findViewById(R.id.Password).setVisibility(View.INVISIBLE);
                findViewById(R.id.username).setVisibility(View.INVISIBLE);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((ViewGroup) findViewById(R.id.mainlayout)).setVisibility(View.VISIBLE);
                Toast.makeText(admindata.this, "begone thot", Toast.LENGTH_SHORT).show();
                dialog.cancel();
                Intent begone= new Intent(thisthing, MainActivity.class);
                startActivity(begone);
            }
        });


        builder.show();
    }
}
