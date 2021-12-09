package Models;

import org.junit.Test;

import static org.junit.Assert.*;

public class CurtainTest {

    @Test
    public void get_id() {
        Curtain curtain = new Curtain("DummyCurtain", false);
        assertEquals("DummyCurtain", curtain.get_id());
    }

    @Test
    public void isStatus() {
        Curtain curtain = new Curtain("DummyCurtain", false);
        assertFalse(curtain.getStatus());
    }

    @Test
    public void set_id() {
        Curtain curtain = new Curtain("DummyCurtain", false);
        curtain.set_id("CurtainDummy");
        assertEquals("CurtainDummy", curtain.get_id());
    }

    @Test
    public void setStatus() {
        Curtain curtain = new Curtain("DummyCurtain", false);
        curtain.setStatus(true);
        assertTrue(curtain.getStatus());
    }
}