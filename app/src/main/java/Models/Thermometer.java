package Models;

public class Thermometer {

    private String deviceID;
    private double temperature;

    public Thermometer(String deviceID, double temperature) {
        this.deviceID = deviceID;
        this.temperature = temperature;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
