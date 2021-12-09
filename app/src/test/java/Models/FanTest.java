package Models;

import org.junit.Test;

import static org.junit.Assert.*;

public class FanTest {

    @Test
    public void get_id() {
        Fan fan = new Fan("DummyFan", 0);
        assertEquals("DummyFan", fan.get_id());
    }

    @Test
    public void getStatus() {
        Fan fan = new Fan("DummyFan", 0);
        assertEquals(0, fan.getStatus());
    }

    @Test
    public void set_id() {
        Fan fan = new Fan("DummyFan", 0);
        fan.set_id("FanDummy");
        assertEquals("FanDummy", fan.get_id());
    }

    @Test
    public void setStatus() {
        Fan fan = new Fan("DummyFan", 0);
        fan.setStatus(1);
        assertEquals(1, fan.getStatus());
    }
}