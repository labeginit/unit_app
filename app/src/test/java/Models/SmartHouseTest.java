package Models;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

public class SmartHouseTest {

    @Test
    public void clear() {
        fillSmartHouse(); // Adds 8 devices to the smartHouse
        SmartHouse.getInstance().clear();
        Assert.assertEquals(0, SmartHouse.getInstance().getLampList().size());
    }


    @Test
    public void getLampList() {
        fillSmartHouse();
        ArrayList list = SmartHouse.getInstance().getLampList();
        assertTrue(list.get(0) instanceof Lamp);
    }

    @Test
    public void getFanList() {
        fillSmartHouse();
        ArrayList list = SmartHouse.getInstance().getFanList();
        assertTrue(list.get(0) instanceof Fan);
    }

    @Test
    public void getCurtainList() {
        fillSmartHouse();
        ArrayList list = SmartHouse.getInstance().getCurtainList();
        assertTrue(list.get(0) instanceof Curtain);
    }

    @Test
    public void getTemperatureSensorList() {
        fillSmartHouse();
        ArrayList list = SmartHouse.getInstance().getThermometerList();
        assertTrue(list.get(0) instanceof Thermometer);
    }

    @Test
    public void getAlarmList() {
        fillSmartHouse();
        ArrayList list = SmartHouse.getInstance().getAlarmList();
        assertTrue(list.get(0) instanceof Alarm);
    }

    @Test
    public void addLamp() {
        SmartHouse.getInstance().addLamp(new Lamp("DummyLamp", false));
        assertTrue(SmartHouse.getInstance().getLampList().get(0) instanceof Lamp);
    }

    @Test
    public void addFan() {
        SmartHouse.getInstance().addFan(new Fan("DummyFan", 0));
        assertTrue(SmartHouse.getInstance().getFanList().get(0) instanceof Fan);
    }

    @Test
    public void addCurtain() {
        SmartHouse.getInstance().addCurtain(new Curtain("DummyCurtain", false));
        assertTrue(SmartHouse.getInstance().getCurtainList().get(0) instanceof Curtain);
    }

    @Test
    public void addTemperatureSensor() {
        SmartHouse.getInstance().addThermometer(new Thermometer("DummyThermometer", 20));
        assertTrue(SmartHouse.getInstance().getThermometerList().get(0) instanceof Thermometer);
    }

    @Test
    public void addAlarm() {
        SmartHouse.getInstance().addAlarm(new Alarm("DummyAlarm", 0));
        assertTrue(SmartHouse.getInstance().getAlarmList().get(0) instanceof Alarm);
    }

    @Test
    public void getNumberOfDevices() {
        SmartHouse.getInstance().clear();
        fillSmartHouse();
        int deviceTotal = 0;
        deviceTotal += SmartHouse.getInstance().getLampList().size();
        deviceTotal += SmartHouse.getInstance().getFanList().size();
        deviceTotal += SmartHouse.getInstance().getCurtainList().size();
        deviceTotal += SmartHouse.getInstance().getThermometerList().size();
        deviceTotal += SmartHouse.getInstance().getAlarmList().size();
        Assert.assertEquals(8, deviceTotal);
    }

    private void fillSmartHouse() { // Utility method to add devices to smarthouse
        SmartHouse.getInstance().addLamp(new Lamp("DummyLamp", false));
        SmartHouse.getInstance().addLamp(new Lamp("DummyLamp2", false));
        SmartHouse.getInstance().addFan(new Fan("DummyFan", 0));
        SmartHouse.getInstance().addCurtain(new Curtain("DummyCurtain", false));
        SmartHouse.getInstance().addThermometer(new Thermometer("DummyThermometer", 20));
        SmartHouse.getInstance().addThermometer(new Thermometer("DummyThermometer2", 21));
        SmartHouse.getInstance().addAlarm(new Alarm("DummyAlarm", 0));
        SmartHouse.getInstance().addAlarm(new Alarm("DummyAlarm", 1));
    }
}