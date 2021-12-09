package Models;

import org.junit.Test;

import static org.junit.Assert.*;

public class ThermometerTest {

    @Test
    public void get_id() {
        Thermometer thermometer = new Thermometer("DummyThermometer", 20);
        assertEquals("DummyThermometer", thermometer.get_id());
    }

    @Test
    public void getStatus() {
        Thermometer thermometer = new Thermometer("DummyThermometer", 20);
        assertEquals(20, (int) thermometer.getStatus());
    }

    @Test
    public void set_id() {
        Thermometer thermometer = new Thermometer("DummyThermometer", 20);
        thermometer.set_id("ThermometerDummy");
        assertEquals("ThermometerDummy", thermometer.get_id());
    }

    @Test
    public void setStatus() {
        Thermometer thermometer = new Thermometer("DummyThermometer", 20);
        thermometer.setStatus(30);
        assertEquals(30, (int) thermometer.getStatus());

    }
}