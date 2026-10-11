package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberRaidShortEnterTest {
    @Test public void mapAccessNullSafe() {
        assertFalse(EmberSkillKit.mapAccessUnlocked(null, null, "q01"));
        assertFalse(EmberSkillKit.mapAccessUnlocked(null, null, "q07"));
        assertFalse(EmberSkillKit.mapAccessUnlocked(null, null, "q04"));
    }
    @Test public void shortRequiresDefault() {
        assertEquals("q01", EmberShortRules.REQUIRES);
    }
}
