package Models;

public class Heater {
    private String _id;
    private boolean status;

    public Heater(String _id, boolean status) {
        this._id = _id;
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public boolean getStatus() {
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
        return "Heater{" +
                "_id='" + _id + '\'' +
                ", status=" + status +
                '}';
    }
}
