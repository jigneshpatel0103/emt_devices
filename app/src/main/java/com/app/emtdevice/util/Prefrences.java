package com.app.emtdevice.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Prefrences {


    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "EMT_DEVICE";
    public static final String PREF_IMAGE = "image";
    public static final String PREF_CHECKSUM = "cecksum";

    public Prefrences(Context contx) {
        context = contx;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

    }

    public void storeImage(String image) {
        editor = pref.edit();
        editor.putString(PREF_IMAGE, image);
        editor.commit();
    }

    public String getImage() {
        String userid = pref.getString(PREF_IMAGE, "");
        return userid;
    }

    public void storeChecksum(String checksum) {
        editor = pref.edit();
        editor.putString(PREF_CHECKSUM, checksum);
        editor.commit();
    }

    public String getChecksum() {
        String checksum = pref.getString(PREF_CHECKSUM, "");
        return checksum;
    }

}
