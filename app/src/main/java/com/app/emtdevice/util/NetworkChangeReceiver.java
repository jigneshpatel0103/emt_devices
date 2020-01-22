package com.app.emtdevice.util;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.view.View;

import com.app.emtdevice.activity.MainActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            if (MainActivity.isSocketAlive()) {

            } else {
                MainActivity.constraintLayoutDeviceNotConnected.setVisibility(View.VISIBLE);
                MainActivity.constraintLayoutUi.setVisibility(View.GONE);
                MainActivity.constraintLayoutWifiNotConnected.setVisibility(View.GONE);
            }
        } else {
            MainActivity.constraintLayoutUi.setVisibility(View.GONE);
            MainActivity.constraintLayoutWifiNotConnected.setVisibility(View.VISIBLE);
            MainActivity.constraintLayoutDeviceNotConnected.setVisibility(View.GONE);
        }
    }
}
