package Models;

public class Fan {

    private String _id;
    private int status; // Status is the speed setting on the fan

    public Fan(String _id, int status) {
        this._id = _id;
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
        return "Fan{" +
                "_id='" + _id + '\'' +
                ", status=" + status +
                '}';
    }
}
