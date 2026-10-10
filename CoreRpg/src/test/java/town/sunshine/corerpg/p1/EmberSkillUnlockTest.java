package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSkillUnlockTest {
    @Test public void maps_to_skills() {
        assertTrue(EmberSkillUnlock.unlockLine("q02").contains("烬突"));
        assertTrue(EmberSkillUnlock.unlockLine("q03").contains("招架"));
        assertTrue(EmberSkillUnlock.unlockLine("q04").contains("符文"));
        assertTrue(EmberSkillUnlock.unlockLine("q05").contains("身法"));
        assertNull(EmberSkillUnlock.unlockLine("q01"));
        assertNull(EmberSkillUnlock.unlockLine("q07"));
        assertEquals("烬突", EmberSkillUnlock.shortName("q02"));
        assertEquals("p1_shape_pick_offered", EmberSkillUnlock.C_SHAPE_OFFERED);
        assertEquals("p1_step_pick_offered", EmberSkillUnlock.C_STEP_OFFERED);
    }
}
