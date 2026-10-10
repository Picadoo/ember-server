package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberRecruitPathTest {
    @Test public void parseAndAuto() {
        assertEquals(EmberRecruitPath.OPEN, EmberRecruitPath.parse("敞开"));
        assertEquals(EmberRecruitPath.GATE, EmberRecruitPath.parse("审核"));
        assertEquals(EmberRecruitPath.MUTE, EmberRecruitPath.parse("静默"));
        assertTrue(EmberRecruitPath.wantsAutoAccept(EmberRecruitPath.OPEN));
        assertFalse(EmberRecruitPath.wantsAutoAccept(EmberRecruitPath.GATE));
        assertFalse(EmberRecruitPath.wantsAutoAccept(EmberRecruitPath.MUTE));
        assertFalse(EmberRecruitPath.wantsAutoAccept(EmberRecruitPath.NONE));
    }
}
