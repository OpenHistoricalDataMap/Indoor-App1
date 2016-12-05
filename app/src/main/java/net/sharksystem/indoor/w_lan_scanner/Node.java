package net.sharksystem.indoor.w_lan_scanner;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Carola Walter
 * Changed by Christoph Bose
 */

public class Node {
    String id;
    float zValue;
    //HashMap<String, Integer> neighborMeter;
    List<SignalInformation> signalInformationList;


    public static class SignalInformation {
        String timestamp;
        List<SignalStrenghtInformation> signalStrenghtInformationList;

        public SignalInformation(String timestamp, List<SignalStrenghtInformation> signalStrenghtInformationList) {
            this.timestamp = timestamp;
            this.signalStrenghtInformationList = signalStrenghtInformationList;
        }
    }

    public static class SignalStrenghtInformation {
        String macAdress;
        int signalStrength;

        public SignalStrenghtInformation(String macAdress, int signalStrength) {
            this.macAdress = macAdress;
            this.signalStrength = signalStrength;
        }
    }

    public Node(String id, float zValue, List<SignalInformation> signalInformationList) {
        this.id = id;
        this.zValue = zValue;
        this.signalInformationList = signalInformationList;
        //this.neighborMeter = new HashMap<String, Integer>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getzValue() {
        return zValue;
    }

    public void setzValue(float zValue) {
        this.zValue = zValue;
    }
/*
    public HashMap<String, Integer> getNeighborMeter() {
        return neighborMeter;
    }

    public void setNeighborMeter(HashMap<String, Integer> neighborMeter) {
        this.neighborMeter = neighborMeter;
    }
*/
    public List<SignalInformation> getSignalInformationList() {
        return signalInformationList;
    }

    public void setSignalInformationList(List<SignalInformation> signalInformationList) {
        this.signalInformationList = signalInformationList;
    }
}
