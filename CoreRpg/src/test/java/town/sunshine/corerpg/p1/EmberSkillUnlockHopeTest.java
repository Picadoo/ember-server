package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

public class EmberSkillUnlockHopeTest {

    @Test
    public void setHope_nullLoadout() {
        // D12282: kit_set_hope → menuLine unset
        assertTrue(EmberSkillUnlock.setHope(null).contains("未成套"));
    }

    @Test
    public void setHope_scorchBlade_menuLine() {
        EmberTables tables = EmberTables.defaults();
        EmberItemData blade = EmberItemData.create("burst", "blade", 2, 0, 0, 0, true, "admin");
        EmberLoadout lo = EmberLoadout.compute(tables, blade, null, 10);
        String s = EmberSkillUnlock.setHope(lo);
        assertEquals("§e烬爆烬斩 §f直线 §8· 缓速/闪", s);
        // long form still available
        assertTrue(EmberSkillUnlock.setHopeLong(lo).contains("套装手感"));
        assertTrue(EmberSkillUnlock.setHopeLong(lo).contains("直线"));
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
