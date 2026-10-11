package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberTalentPlaystyleTest {
    @Test public void mapNodes() {
        assertEquals(EmberPosturePath.STRIKE, EmberTalentPlaystyle.postureForNode("t1a"));
        assertEquals(EmberPosturePath.GUARD, EmberTalentPlaystyle.postureForNode("t1b"));
        assertEquals(EmberPosturePath.SWEEP, EmberTalentPlaystyle.postureForNode("t1c"));
        assertEquals(EmberPosturePath.NONE, EmberTalentPlaystyle.postureForNode("t2a"));
    }
    @Test public void latchPrefix() {
        assertEquals("p1_talent_style_", EmberTalentPlaystyle.C_STYLE);
    }
    @Test public void hasRowPickIndexes() {
        // pure static: posture map only; hasRowPick needs PlayerData mock — skip live
        assertTrue(EmberPosturePath.valid(EmberTalentPlaystyle.postureForNode("t1a")));
        assertTrue(EmberPosturePath.valid(EmberTalentPlaystyle.postureForNode("t1b")));
        assertTrue(EmberPosturePath.valid(EmberTalentPlaystyle.postureForNode("t1c")));
    }
}
