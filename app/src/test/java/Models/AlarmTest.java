package Models;

import org.junit.Test;

import static org.junit.Assert.*;

public class AlarmTest {

    @Test
    public void get_id() {
        Alarm alarm = new Alarm("DummyAlarm", 1);
        assertEquals("DummyAlarm", alarm.get_id());
    }

    @Test
    public void getStatus() {
        Alarm alarm = new Alarm("DummyAlarm", 1);
        assertEquals(1, alarm.getStatus());
    }

    @Test
    public void set_id() {
        Alarm alarm = new Alarm("DummyAlarm", 1);
        alarm.set_id("AlarmDummy");
        assertEquals("AlarmDummy", alarm.get_id());
    }

    @Test
    public void setStatus() {
        Alarm alarm = new Alarm("DummyAlarm", 1);
        alarm.setStatus(2);
        assertEquals(2, alarm.getStatus());
    }
}