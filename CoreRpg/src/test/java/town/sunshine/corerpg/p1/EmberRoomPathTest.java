package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberRoomPathTest {
    @Test public void applyBias_remaps_rooms_keeps_kinds() {
        String[] in = {"r2", "blazing", "r1", "hold"};
        String[] front = EmberRoomPath.applyBias(in, EmberRoomPath.FRONT);
        assertEquals("r1", front[0]);
        assertEquals("blazing", front[1]);
        assertEquals("r1", front[2]);
        assertEquals("hold", front[3]);
        String[] back = EmberRoomPath.applyBias(in, EmberRoomPath.BACK);
        assertEquals("r3", back[0]);
        assertEquals("r3", back[2]);
        String[] none = EmberRoomPath.applyBias(in, EmberRoomPath.NONE);
        assertEquals("r2", none[0]);
        assertEquals("r1", none[2]);
    }
    @Test public void parse() {
        assertEquals(EmberRoomPath.FRONT, EmberRoomPath.parse("前房"));
        assertEquals(EmberRoomPath.MID, EmberRoomPath.parse("r2"));
        assertEquals(EmberRoomPath.BACK, EmberRoomPath.parse("后房"));
    }
}
