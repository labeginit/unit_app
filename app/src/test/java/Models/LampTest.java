package Models;

import org.junit.Test;

import static org.junit.Assert.*;

public class LampTest {

    @Test
    public void get_id() {
        Lamp lamp = new Lamp("DummyLamp", false);
        assertEquals("DummyLamp", lamp.get_id());
    }

    @Test
    public void getStatus() {
        Lamp lamp = new Lamp("DummyLamp", false);
        assertFalse(lamp.getStatus());
    }

    @Test
    public void set_id() {
        Lamp lamp = new Lamp("DummyLamp", false);
        lamp.set_id("LampDummy");
        assertEquals("LampDummy", lamp.get_id());
    }

    @Test
    public void setStatus() {
        Lamp lamp = new Lamp("DummyLamp", false);
        lamp.setStatus(true);
        assertTrue(lamp.getStatus());
    }
}