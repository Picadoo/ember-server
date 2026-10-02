package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

public class EmberCodexTest {

    private static PlayerData pd() { return new PlayerData(); }

    @Test public void twentyEntries() {
        assertEquals(20, EmberCodex.ENTRIES.size());
        assertEquals("none_blade_t0", EmberCodex.ENTRIES.get(0).key());
    }

    @Test public void registerOnceAndAdminNeverCounts() {
        PlayerData d = pd();
        EmberItemData a = EmberItemData.create("burst", "blade", 2, 0, 0, 0, true, "drop");
        assertTrue(EmberCodex.register(d, a));
        assertFalse(EmberCodex.register(d, EmberItemData.create("burst", "blade", 2, 3, 3, 9, true, "drop")));
        assertFalse(EmberCodex.register(d, EmberItemData.create("scorch", "charm", 3, 0, 0, 0, true, "admin")));
        assertTrue(EmberCodex.register(d, EmberItemData.create("burst", "charm", 0, 0, 0, 0, true, "quest"))); // T0 → none
        assertTrue(EmberCodex.has(d, "none_charm_t0"));
        assertEquals(2, EmberCodex.count(d));
    }

    @Test public void stagesClaimOnce() {
        PlayerData d = pd();
        for (int i = 0; i < 10; i++) {
            EmberCodex.Entry e = EmberCodex.ENTRIES.get(i);
            EmberCodex.register(d, e.family, e.slot, e.tier, "drop");
        }
        assertEquals(Arrays.asList(0, 1), EmberCodex.claimable(d));
        d.addPeriodCount(EmberCodex.C_CLAIM + 0, "all", 1);
        assertEquals(Arrays.asList(1), EmberCodex.claimable(d));
        assertTrue(EmberCodex.stageLabel(d, 0).contains("已领取"));
        assertTrue(EmberCodex.stageLabel(d, 2).contains("还差 5"));
    }
}
