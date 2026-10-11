package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

public class EmberDoorTempoTest {
    @Test
    public void askScaleConstant() {
        assertEquals(0.85, EmberDoorTempo.ASK_DELAY_SCALE, 1e-9);
    }

    @Test
    public void secondsLeft() {
        assertEquals(0, EmberDoorTempo.secondsLeft(1000, 1000));
        assertEquals(2, EmberDoorTempo.secondsLeft(0, 1500));
        assertEquals(1, EmberDoorTempo.secondsLeft(500, 1000));
    }

    @Test
    public void actionBar() {
        assertTrue(EmberDoorTempo.actionBar(0).contains("开启"));
        assertTrue(EmberDoorTempo.actionBar(3).contains("3s"));
    }

    @Test
    public void muteHidesCountdown() {
        assertFalse(EmberDoorTempo.showCountdown(EmberGatePath.MUTE));
        assertTrue(EmberDoorTempo.showCountdown(EmberGatePath.ASK));
        assertTrue(EmberDoorTempo.showCountdown(EmberGatePath.AUTO));
        assertTrue(EmberDoorTempo.showCountdown(EmberGatePath.NONE));
    }
}
