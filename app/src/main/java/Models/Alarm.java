package Models;

public class Alarm {

    String _id;
    int status; // 0 = OFF, 1 = ON, 2 = Alarm is going off

    public Alarm(String deviceID, int status) {
        _id = deviceID;
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public int getStatus() {
        return status;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setStatus(int status) {
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
