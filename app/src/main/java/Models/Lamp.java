package Models;

public class Lamp {

    private String _id;
    private boolean status;

    public Lamp(String _id, boolean status) {
        this._id = _id;
        this.status = status;
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
        return "Lamp{" +
                "_id='" + _id + '\'' +
                ", status=" + status +
                '}';
    }
}
