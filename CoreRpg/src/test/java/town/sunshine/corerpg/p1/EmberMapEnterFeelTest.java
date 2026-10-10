package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberMapEnterFeelTest {
    @Test public void mode_tags_and_line() {
        assertEquals("主线", EmberMapEnterFeel.modeTag(false, false, null, false, false, 0));
        assertEquals("挑战", EmberMapEnterFeel.modeTag(false, false, null, false, true, 0));
        assertEquals("深渊3", EmberMapEnterFeel.modeTag(false, false, null, false, true, 3));
        assertEquals("残响", EmberMapEnterFeel.modeTag(false, true, "echo", false, false, 0));
        assertEquals("前哨", EmberMapEnterFeel.modeTag(false, true, "outpost", false, false, 0));
        assertEquals("连战", EmberMapEnterFeel.modeTag(false, true, "rush", false, false, 0));
        assertEquals("团本", EmberMapEnterFeel.modeTag(false, false, null, true, false, 0));
        assertEquals("短征", EmberMapEnterFeel.modeTag(true, false, null, false, false, 0));
        String line = EmberMapEnterFeel.line("余烬序章", "主线");
        assertTrue(line.contains("入场"));
        assertTrue(line.contains("余烬序章"));
        assertTrue(line.contains("主线"));
    }
}
