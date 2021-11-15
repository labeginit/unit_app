package Models;

import java.util.ArrayList;

public class SmartHouse {

    private static SmartHouse smartHouse = null;

    ArrayList<Lamp> lampList;
    ArrayList<Fan> fanList;
    ArrayList<Curtain> curtainList;
    ArrayList<Thermometer> temperatureSensorList;


    private SmartHouse() {
        lampList = new ArrayList<>();
        fanList = new ArrayList<>();
        curtainList = new ArrayList<>();
        temperatureSensorList = new ArrayList<>();
    }

    public static SmartHouse getInstance() {
        if (smartHouse == null) {
            smartHouse = new SmartHouse();
        }
        return smartHouse;
    }

    public ArrayList<Lamp> getLampList() {
        return lampList;
    }

    public ArrayList<Fan> getFanList() {
        return fanList;
    }

    public ArrayList<Curtain> getCurtainList() {
        return curtainList;
    }

    public ArrayList<Thermometer> getTemperatureSensorList() {
        return temperatureSensorList;
    }

    public void addLamp(Lamp lamp) {
        this.lampList.add(lamp);
    }

    public void addFan(Fan fan) {
        this.fanList.add(fan);
    }

    public void addCurtain(Curtain curtain) {
        this.curtainList.add(curtain);
    }

    public void addTemperatureSensor(Thermometer thermometer) {
        this.temperatureSensorList.add(thermometer);
    }

    @Override
    public String toString() {
        return "SmartHouse{" +
                "lampList=" + lampList.toString() +
                ", fanList=" + fanList.size() +
                ", curtainList=" + curtainList.size() +
                ", thermometerList=" + temperatureSensorList.size() +
                '}';
    }
}
