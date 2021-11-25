package Models;

public class Thermometer {

    private String _id;
    private double status; // Status is temperature

    public Thermometer(String _id, double status) {
        this._id = _id;
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public double getStatus() {
        return status;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setStatus(double status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Thermometer{" +
                "_id='" + _id + '\'' +
                ", status=" + status +
                '}';
    }
}
