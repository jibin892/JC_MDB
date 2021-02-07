package com.example.jc_mdb.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jc_mdb.utils.Tools;
import com.sg.moviesindex.R;


public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Tools.setSystemBarColorInt(this, getResources().getColor(R.color.cblue1));



        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                Intent splash=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(splash);
            }
        }, 1000);

    }
}
