package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

public class EmberSkillUnlockHopeTest {

    @Test
    public void setHope_nullLoadout() {
        assertTrue(EmberSkillUnlock.setHope(null).contains("手持刃"));
    }

    @Test
    public void setForkHope_shape() {
        String s = EmberSkillUnlock.setForkHope("q04");
        assertNotNull(s);
        assertTrue(s.contains("套装差"));
    }

    @Test
    public void nextHope_nullSafe() {
        assertTrue(EmberSkillUnlock.nextHope(null, null).contains("未载入"));
    }
}
