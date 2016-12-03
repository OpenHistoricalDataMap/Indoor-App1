package net.sharksystem.indoor.w_lan_scanner;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class MainActivity extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] PERMISSIONS_STORAGE={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION};

        //this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission_group.STORAGE}, 1);
        int permission = ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,PERMISSIONS_STORAGE,1);
        }

        ThatApp.initThatApp(this);

        setContentView(R.layout.activity_main);

        Button button = (Button) this.findViewById(R.id.refresh);
        button.setOnClickListener(this);

        button = (Button) this.findViewById(R.id.scanAgain);
        button.setOnClickListener(this);

        button = (Button) this.findViewById(R.id.saveFile);
        button.setOnClickListener(this);

        button = (Button) this.findViewById(R.id.saveIntervall);
        button.setOnClickListener(this);
    }

    public void onClick(View v) {
        Button refreshButton = (Button) this.findViewById(R.id.refresh);
        Button scanButton = (Button) this.findViewById(R.id.scanAgain);
        Button saveButton = (Button) this.findViewById(R.id.saveFile);
        Button saveIntervall = (Button) this.findViewById(R.id.saveIntervall);

        if(v == refreshButton) {
            this.refresh();
        }

        if(v == scanButton) {
            this.scanAgain();
        }

        if(v == saveButton){
            this.saveFileJSON("ZFR",0);
        }

        if(v == saveIntervall){
            this.saveIntervallJSON("WLaLbSs");
        }

    }

    public void scanAgain() {
        ThatApp.getThatApp().getWifiManager().startScan();
    }

    /**
     * Speichern der momentanen aufnahme in eine Datei mit Timestamp
     */
    private int saveFile(String ssid, int x, int y)
    {
        try{
            File newDir = new File(Environment.getExternalStorageDirectory(),"wlanscan");
            if(!newDir.exists()) {
                newDir.mkdir();
            }
            String filename = "ergebnisse"+x+"-"+y+".log";
            File target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", filename);
            while(target.exists()) {
                x++;
                target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", "ergebnisse"+x+"-"+y+".log");
            }
            target.createNewFile();
            FileWriter fw = new FileWriter(target.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String content = "";
            List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
            for(ScanResult sr : scanResults)
            {
                if(sr.SSID.equals(ssid))
                {
                    content += ""+sr.BSSID+"|"+sr.level+"|"+"\n";
                }
            }
            bw.write(content,0,content.length());
            bw.close();


        } catch(IOException ioe)
        {
            System.out.println(ioe.toString());
            System.out.println(ioe.getStackTrace());
        }
        return x;
    }

    private int saveFileJSON(String ssid, int x)
    {
        try{
            JSONObject jObj = new JSONObject();
            jObj.put("id","Placeholder");
            jObj.put("zValue",0);

            JSONObject signalInf = new JSONObject();
            JSONArray signalInfList = new JSONArray();
            JSONArray signalStrength = new JSONArray();
            List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
            for(ScanResult sr : scanResults)
            {
                if(sr.SSID.equals(ssid))
                {
                    JSONObject measurement = new JSONObject();
                    measurement.put("macAdress",sr.BSSID);
                    measurement.put("strength",sr.level);
                    signalStrength.put(measurement);
                }
            }
            signalInfList.put(signalStrength);
            signalInf.put("timestamp","0000"); //PLACEHOLDER
            signalInf.put("signalStrenghtInformationList", signalStrength);
            jObj.put("signalInformationList",signalInf);

            File newDir = new File(Environment.getExternalStorageDirectory(),"wlanscan");
            if(!newDir.exists()) {
                newDir.mkdir();
            }
            String filename = "ergebnisse"+x+".txt";
            File target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", filename);
            while(target.exists()) {
                x++;
                target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", "ergebnisse"+x+".log");
            }
            target.createNewFile();
            FileWriter fw = new FileWriter(target.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String content =jObj.toString();
            bw.write(content,0,content.length());
            bw.close();


        } catch(IOException ioe)
        {
            System.out.println(ioe.toString());
            System.out.println(ioe.getStackTrace());
        }
        catch(JSONException je)
        {

        }
        return x;
    }

    /**
     * Speichern der Messdaten in einem Intervall in 5 Dateien.
     * Ruft saveFile() auf
     */
    private void saveIntervall(String ssid)
    {
        int x = 0;
        for(int i = 0; i < 5; i++) {
            x = saveFile(ssid, x, i);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(10000);
                            scanAgain();
                            refresh();
                        }
                    } catch (InterruptedException ex) {}
                }
            });
            thread.run();
        }
        average(x);
    }

    /**
     * Speichern von Messdaten zu spezifischer SSID in einer JSON file
     * @param ssid SSID für die messdaten erhoben und gespeichert werden sollen über Zeitintervall
     */
    private void saveIntervallJSON(String ssid)
    {
        int x =0;
        try{
            JSONObject node = new JSONObject();
            node.put("id","PLACEHOLDER");
            node.put("zValue",0);
            JSONArray signalInformationList = new JSONArray();
            int timestamp = 0;
            for(int i = 0; i<10;i++){
                JSONObject signalInformation = new JSONObject();
                signalInformation.put("timestamp",timestamp);
                JSONArray signalStrengthInformationList = new JSONArray();

                List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
                for (ScanResult sr : scanResults) {
                    if (sr.SSID.equals(ssid)) {
                        JSONObject signalStrengthInformation = new JSONObject();
                        signalStrengthInformation.put("macAdress", sr.BSSID);
                        signalStrengthInformation.put("strength", sr.level);
                        signalStrengthInformationList.put(signalStrengthInformation);
                    }
                }
                signalInformation.put("signalStrengthInformationList", signalStrengthInformationList);
                signalInformationList.put(signalInformation);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(1000);
                                scanAgain();
                                refresh();
                            }
                        } catch (InterruptedException ie) {
                        }
                    }
                });
                timestamp +=1;
            }
            node.put("signalInformationList",signalInformationList);
            File newDir = new File(Environment.getExternalStorageDirectory(),"wlanscan");
            if(!newDir.exists()) {
                newDir.mkdir();
            }
            String filename = "ergebnisse"+x+".txt";
            File target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", filename);
            while(target.exists()) {
                x++;
                target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", "ergebnisse"+x+".txt");
            }
            target.createNewFile();
            FileWriter fw = new FileWriter(target.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String content =node.toString();
            bw.write(content,0,content.length());
            bw.close();


        } catch(IOException ioe)
        {
            System.out.println(ioe.toString());
            System.out.println(ioe.getStackTrace());
        }
        catch(JSONException je) {

        }
    }

    private void average(int x)
    {
        int y=0;
        ArrayList<BssidRelevant> bssids = new ArrayList<BssidRelevant>();
        String filename = "ergebnisse"+x+"-"+y+".log";
        File target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", filename);
        try {
            //SOLANGE NOCH MESSDATEN VORLIEGEN
            while (target.exists()) {
                //AUSLESEN VON ALLEM AUS EINER DATEI UND ABLEGEN IN DIE ARRAYLISTE
                FileReader fr = new FileReader(target);
                BufferedReader br = new BufferedReader(fr);
                String s;
                //JEDE ZEILE AUSLESEN
                while((s = br.readLine())!= null ) {
                    String[] sar = s.split("|");
                    boolean entryfound = false;
                    //LVL für SSID ADDIEREN
                    for(int i = 0; i<bssids.size(); i++)
                    {
                        if(bssids.get(i).getName().equals(sar[0]))
                        {
                            bssids.get(i).setLvl(bssids.get(i).getLvl()+Integer.parseInt(sar[1]));
                            bssids.get(i).incrementCounter();
                            entryfound = true;
                        }
                    }
                    //FALLS BSSID NOCH NICHT DABEI ZUR LISTE HINZUFÜGEN
                    if(!entryfound)
                    {
                        bssids.add(new BssidRelevant(sar[0],Integer.parseInt(sar[1])));
                    }
                }
                y+=1;
                target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", "ergebnisse"+x+"-"+y+".log");
            }
            String c = "";
            for(BssidRelevant br : bssids)
            {
                br.computeAverage();
                c+=br.getName()+"|"+br.getLvl()+"\n";
            }
            File savetar = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", "ergebnisse"+x+"-"+"AVERAGE.log");
            FileWriter fw = new FileWriter(savetar.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(c,0,c.length());
            bw.close();
        }catch(FileNotFoundException fne)
        {
            TextView tv = (TextView) this.findViewById(R.id.textView);
            tv.setText("FILE NOT FOUND \n"+fne.getLocalizedMessage());
        }catch(IOException ioe)
        {
            TextView tv = (TextView) this.findViewById(R.id.textView);
            tv.setText("IO EXCEPTION \n"+ioe.getLocalizedMessage());
        }
    }

    public void refresh() {
        TextView tv_connectedWLAN = (TextView) this.findViewById(R.id.connectedWLAN);
        tv_connectedWLAN.setText("nix");

        TextView tv = (TextView) this.findViewById(R.id.textView);
        tv.setText("nix");

        try {
            WifiInfo connectionInfo = ThatApp.getThatApp().getWifiManager().getConnectionInfo();
            if(connectionInfo != null) {
                String infoString = "connected W-LAN: ";

                infoString += "\nBSSID: ";
                infoString += connectionInfo.getBSSID();

                infoString += "\nMacAddress: ";
                infoString += connectionInfo.getMacAddress();

                infoString += "\nSSID: ";
                infoString += connectionInfo.getSSID();

                tv_connectedWLAN.setText(infoString);

                ThatApp.getThatApp().printScan();
            }
        }
        catch (Exception e) {
            tv.setText("Exception: " + e.getLocalizedMessage());
        }
    }
}
