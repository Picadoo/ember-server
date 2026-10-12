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

    @Test
    public void remainLine_unary_regression() {
        assertEquals(5, EmberDropCraftPity.clearsLeft(0));
        assertEquals(2, EmberDropCraftPity.clearsLeft(3));
        assertEquals(0, EmberDropCraftPity.clearsLeft(5));
        assertEquals("精工软保底 0/5 · 再连出精工0×5触发精工1底", EmberDropCraftPity.remainLine(0));
        assertEquals("精工软保底 3/5 · 再连出精工0×2触发精工1底", EmberDropCraftPity.remainLine(3));
        assertEquals("精工软保底已满 · 下次主线装必≥精工1", EmberDropCraftPity.remainLine(5));
    }

    @Test
    public void clearsLeft_nudgeReady() {
        assertEquals(1, EmberDropCraftPity.clearsLeft(3, true));  // 2-1
        assertEquals(2, EmberDropCraftPity.clearsLeft(2, true));  // 3-1
        assertEquals(1, EmberDropCraftPity.clearsLeft(4, true));  // left==1, no subtract
        assertEquals(2, EmberDropCraftPity.clearsLeft(3, false));
        assertEquals(0, EmberDropCraftPity.clearsLeft(5, true));
    }

    @Test
    public void remainLine_nudgeReady_cases() {
        String r3 = EmberDropCraftPity.remainLine(3, true);
        assertTrue(r3.contains("再连出精工0×1"));
        assertTrue(r3.contains("今日加码待用"));

        String r2 = EmberDropCraftPity.remainLine(2, true);
        assertTrue(r2.contains("再连出精工0×2"));
        assertTrue(r2.contains("今日加码待用"));

        String r4 = EmberDropCraftPity.remainLine(4, true);
        assertTrue(r4.contains("再连出精工0×1"));
        assertTrue(r4.contains("今日加码待用"));

        String r3f = EmberDropCraftPity.remainLine(3, false);
        assertEquals("精工软保底 3/5 · 再连出精工0×2触发精工1底", r3f);
        assertFalse(r3f.contains("今日加码待用"));

        assertEquals("精工软保底已满 · 下次主线装必≥精工1", EmberDropCraftPity.remainLine(5, true));
        assertFalse(EmberDropCraftPity.remainLine(5, true).contains("今日加码待用"));
    }

    @Test
    public void remainLine_pd_feeds_craftNudgeReady() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberDropCraftPity.C_PITY, "all", 3);
        assertTrue(EmberDropCraftNudge.ready(d));
        String rem = EmberDropCraftPity.remainLine(d);
        assertTrue(rem.contains("再连出精工0×1"));
        assertTrue(rem.contains("今日加码待用"));

        EmberDropCraftNudge.consumeExtra(d);
        assertFalse(EmberDropCraftNudge.ready(d));
        String after = EmberDropCraftPity.remainLine(d);
        assertEquals("精工软保底 3/5 · 再连出精工0×2触发精工1底", after);
        assertFalse(after.contains("今日加码待用"));
    }
}
