package Models;

public class Curtain {

    private String deviceID;
    private boolean open;

    public Curtain(String deviceID, boolean open) {
        this.deviceID = deviceID;
        this.open = open;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public boolean isOpen() {
        return open;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
