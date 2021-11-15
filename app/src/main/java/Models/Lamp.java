package Models;

public class Lamp {

    private String deviceID;
    private boolean on;

    public Lamp(String deviceID, boolean on) {
        this.deviceID = deviceID;
        this.on = on;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public boolean isOn() {
        return on;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public String toString() {
        return "Lamp{" +
                "name='" + deviceID + '\'' +
                ", on=" + on +
                '}';
    }
}
