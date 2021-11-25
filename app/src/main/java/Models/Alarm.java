package Models;

public class Alarm {

    String _id;
    boolean status;

    public Alarm(String deviceID, boolean on) {
        _id = deviceID;
        status = on;
    }

    public String get_id() {
        return _id;
    }

    public boolean isStatus() {
        return status;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "_id='" + _id + '\'' +
                ", status=" + status +
                '}';
    }
}
