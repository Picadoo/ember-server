package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberParryReadyTest {
    @Test public void ready_line_names_parry() {
        String s = EmberParry.readyActionBar();
        assertTrue(s.contains("就绪"));
        assertTrue(s.contains(EmberSkillKit.DISPLAY_PARRY));
        assertTrue(s.contains("守招"));
    }
}
