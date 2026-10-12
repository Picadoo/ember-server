package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

public class EmberDropCraftPityTest {

    private EmberRunRules.Grant item(int craft) {
        return new EmberRunRules.Grant("base_item", EmberRunRules.Kind.ITEM, "u", 1,
                new EmberRunRules.ItemRoll("scorch", "blade", 1, 0, craft));
    }

    @Test
    public void forcesFloorAfterNeed() {
        PlayerData d = new PlayerData();
        // Isolate from D10737 daily craft nudge (+BONUS on first craft0 of day) — NEED path only
        d.addPeriodCount(EmberDropCraftNudge.C_USED, "day", 1);
        for (int i = 0; i < EmberDropCraftPity.NEED; i++) {
            List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
            g.add(item(0));
            assertFalse(EmberDropCraftPity.applyToGrants(d, g));
            assertEquals(0, g.get(0).item.craft);
        }
        assertEquals(EmberDropCraftPity.NEED, EmberDropCraftPity.get(d));
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(item(0));
        assertTrue(EmberDropCraftPity.applyToGrants(d, g));
        assertEquals(1, g.get(0).item.craft);
        assertEquals(0, EmberDropCraftPity.get(d));
    }

    @Test
    public void naturalCraftClears() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberDropCraftNudge.C_USED, "day", 1);
        EmberDropCraftPity.applyToGrants(d, Collections.singletonList(item(0)));
        EmberDropCraftPity.applyToGrants(d, Collections.singletonList(item(0)));
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(item(2));
        assertFalse(EmberDropCraftPity.applyToGrants(d, g));
        assertEquals(0, EmberDropCraftPity.get(d));
    }

    @Test
    public void needFloorLiteralsUnchanged() {
        assertEquals(5, EmberDropCraftPity.NEED);
        assertEquals(1, EmberDropCraftPity.FLOOR);
    }

    @Test
    public void remainLine_nonEmpty_forCur0ThroughNeed() {
        for (int cur = 0; cur <= EmberDropCraftPity.NEED; cur++) {
            String rem = EmberDropCraftPity.remainLine(cur);
            assertNotNull(rem);
            assertFalse("cur=" + cur, rem.isEmpty());
            assertTrue("cur=" + cur, rem.contains("精工"));
        }
    }

    @Test
    public void shouldAnnounceSettleRemain_onlyWhenApplicableNotForcedEnabled() {
        assertTrue(EmberDropCraftPity.shouldAnnounceSettleRemain(true, false, true));
        assertFalse(EmberDropCraftPity.shouldAnnounceSettleRemain(true, true, true)); // forced: no remain
        assertFalse(EmberDropCraftPity.shouldAnnounceSettleRemain(false, false, true));
        assertFalse(EmberDropCraftPity.shouldAnnounceSettleRemain(true, false, false)); // hot off
        assertFalse(EmberDropCraftPity.shouldAnnounceSettleRemain(true, true, false));
    }

    @Test
    public void settleRemainEnabled_defaultTrue_and_testHook() {
        EmberDropCraftPity.setSettleRemainEnabledForTest(null);
        assertTrue(EmberDropCraftPity.settleRemainEnabled());
        EmberDropCraftPity.setSettleRemainEnabledForTest(Boolean.FALSE);
        assertFalse(EmberDropCraftPity.settleRemainEnabled());
        // Forced path must not call remain even when enabled restored
        assertFalse(EmberDropCraftPity.shouldAnnounceSettleRemain(true, true, EmberDropCraftPity.settleRemainEnabled()));
        EmberDropCraftPity.setSettleRemainEnabledForTest(null);
        assertTrue(EmberDropCraftPity.settleRemainEnabled());
        assertTrue(EmberDropCraftPity.shouldAnnounceSettleRemain(true, false, EmberDropCraftPity.settleRemainEnabled()));
    }
}
