package com.canyinghao.canphotos.demo;

import android.app.Application;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;


public class App extends Application {
    private static App sInstance;


    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        Fresco.initialize(this);
    }

    public static App getInstance() {

        if (sInstance == null) {
            sInstance = new App();
        }
        return sInstance;
    }


    public void show(String msg){


        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

}