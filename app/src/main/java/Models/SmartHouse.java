package Models;

import java.util.ArrayList;

public class SmartHouse {

    private static SmartHouse smartHouse = null;

    ArrayList<Lamp> lampList;
    ArrayList<Fan> fanList;
    ArrayList<Curtain> curtainList;
    ArrayList<Thermometer> temperatureSensorList;
    ArrayList<Alarm> alarmList;
    ArrayList<Heater> heaterList;

    public void clear() {
        lampList.clear();
        fanList.clear();
        curtainList.clear();
        temperatureSensorList.clear();
        alarmList.clear();
        heaterList.clear();
    }


    private SmartHouse() {
        lampList = new ArrayList<>();
        fanList = new ArrayList<>();
        curtainList = new ArrayList<>();
        temperatureSensorList = new ArrayList<>();
        alarmList = new ArrayList<>();
        heaterList = new ArrayList<>();
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

    public ArrayList<Thermometer> getThermometerList() {
        return temperatureSensorList;
    }

    public ArrayList<Alarm> getAlarmList(){ return alarmList; }

    public ArrayList<Heater> getHeaterList(){ return heaterList; }

    public void addLamp(Lamp lamp) {
        this.lampList.add(lamp);
    }

    public void addFan(Fan fan) {
        this.fanList.add(fan);
    }

    public void addCurtain(Curtain curtain) {
        this.curtainList.add(curtain);
    }

    public void addThermometer(Thermometer temperatureSensor) { this.temperatureSensorList.add(temperatureSensor); }

    public void addAlarm(Alarm alarm){ this.alarmList.add(alarm); }

    public void addHeater(Heater heater){ this.heaterList.add(heater); }

    public int getNumberOfDevices() {
        int deviceTotal = 0;

        deviceTotal += getLampList().size();
        deviceTotal += getFanList().size();
        deviceTotal += getCurtainList().size();
        deviceTotal += getThermometerList().size();
        deviceTotal += getAlarmList().size();
        deviceTotal += getHeaterList().size();

        return deviceTotal;
    }

    @Override
    public String toString() {
        return "SmartHouse{" +
                "lampList=" + lampList +
                ", fanList=" + fanList +
                ", curtainList=" + curtainList +
                ", temperatureSensorList=" + temperatureSensorList +
                ", alarmList=" + alarmList +
                ", heaterList=" + heaterList +
                '}';
    }
}