package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSetFeelTest {
    @Test public void runLine_set_and_4pc() {
        assertEquals("", EmberSetFeel.runLine("none", 1, 0, false));
        String s = EmberSetFeel.runLine("scorch", 1, 1, false);
        assertTrue(s.contains("本局套装"));
        assertTrue(s.contains("焚烬"));
        assertTrue(s.contains("点燃"));
        assertTrue(s.contains("1/2"));
        String on = EmberSetFeel.runLine("burst", 2, 2, true);
        assertTrue(on.contains("烬爆"));
        assertTrue(on.contains("四件套受伤"));
        assertTrue(on.contains("觉醒II"));
    }
}
