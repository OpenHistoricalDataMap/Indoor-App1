package net.sharksystem.indoor.w_lan_scanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

/**
 * Created by thsc on 11.10.2016.
 */

public class ThatApp {
    private static WifiManager wifiManager;
    private static ThatApp app;
    private Activity activity;

    static ThatApp initThatApp(Activity activity) {
        if(app == null) {
            ThatApp.app = new ThatApp(activity);
        }

        return ThatApp.app;
    }

    static ThatApp getThatApp() {
        return ThatApp.app;
    }

    ThatApp(Activity activity) {
        this.activity = activity;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_PERMISSION_CODE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }

        // set up wifi manager
        wifiManager=(WifiManager) activity.getSystemService(Context.WIFI_SERVICE);

        this.wifiManager.startScan();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        //filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);

        WLANScanResultsReceiver wlanScanResultsReceiver = new WLANScanResultsReceiver();

        activity.registerReceiver(wlanScanResultsReceiver, filter);
    }

    public WifiManager getWifiManager() {
        return ThatApp.wifiManager;
    }

    int ACCESS_COARSE_LOCATION_PERMISSION_CODE = 1;

    void printScan() {
        TextView tv = (TextView) this.activity.findViewById(R.id.textView);

        this.getWifiManager();

        String scanString = "Scan Results";

        try {
            List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();

            if (scanResults != null) {
                Iterator<ScanResult> resultIter = scanResults.iterator();
                if (resultIter.hasNext()) {
                    scanString += "\n+++++++++ start +++++++++++";
                    while (resultIter.hasNext()) {
                        ScanResult scanResult = resultIter.next();
                        scanString += "\nName: ";
                        scanString += scanResult.SSID;
                        scanString += " ("+scanResult.BSSID+")";
                        scanString += "lvl: "+scanResult.level;
                    }
                    scanString += "\n+++++++++ end +++++++++++";
                } else {
                    scanString += "\nno scan results at all";
                }
            }  else {
                scanString += "\nnull iterator - weired";
            }
        }
        catch(Exception e) {
            scanString += "Exception: " + e.getLocalizedMessage();
        }

        tv.setText(scanString);
    }

    void processPOI()
    {

    }
}
