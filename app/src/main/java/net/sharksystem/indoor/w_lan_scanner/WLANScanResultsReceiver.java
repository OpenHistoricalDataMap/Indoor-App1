package net.sharksystem.indoor.w_lan_scanner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.view.View;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class WLANScanResultsReceiver extends BroadcastReceiver {
    public WLANScanResultsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        ThatApp.getThatApp().printScan();
    }
}
