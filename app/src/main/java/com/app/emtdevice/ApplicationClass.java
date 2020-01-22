package com.app.emtdevice;

import android.app.Application;

import com.app.emtdevice.util.Prefrences;

/**
 * Created by Bhargav on 05/01/19.
 */

public class ApplicationClass extends Application {

    public static Prefrences preference;

    @Override
    public void onCreate() {
        super.onCreate();
        preference = new Prefrences(getApplicationContext());
    }

}
