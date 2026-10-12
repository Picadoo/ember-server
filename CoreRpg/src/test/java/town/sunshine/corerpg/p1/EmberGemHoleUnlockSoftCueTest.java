package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

/**
 * TIP-BC DESIGN §2.3: Open/list ready cue; reject line follows ready;
 * opening list never latches; unlocked only on soft pass.
 */
public class EmberGemHoleUnlockSoftCueTest {

    /** Simulate Open/list remain line tail (no Bukkit). */
    private static String openLine(PlayerData d) {
        return "宝石通关保底：再通关×2" + EmberGemSocket.unlockSoftListTail(d);
    }

    @Test
    public void ready_openContainsCue_noConsume() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGemHoleUnlockSoft.ready(d));
        String line = openLine(d);
        assertTrue(line.contains("开孔门槛−1待用"));
        assertTrue(line.contains("≥4"));
        assertTrue(line.contains(EmberGemHoleUnlockSoft.CUE));
        assertFalse(EmberGemHoleUnlockSoft.usedToday(d));
        assertTrue(EmberGemHoleUnlockSoft.ready(d));
    }

    @Test
    public void ready_bladePlus3_insertRejectSoft_stillReady() {
        PlayerData d = new PlayerData();
        EmberItemData blade = EmberItemData.create("scorch", "blade", 2, 1, 0, 3, true, "drop");
        assertFalse(blade.sockUnlocked(1));
        assertFalse(EmberGemHoleUnlockSoft.unlocked(blade, 1, d)); // soft fail · no latch
        String rej = EmberGemSocket.unlockRejectLine(d);
        assertTrue(rej.contains("≥4"));
        assertTrue(rej.contains("今日门槛−1"));
        assertTrue(EmberGemHoleUnlockSoft.ready(d));
        assertFalse(EmberGemHoleUnlockSoft.usedToday(d));
    }

    @Test
    public void used_openNoCue() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberGemHoleUnlockSoft.C_USED, "day", 1);
        assertFalse(EmberGemHoleUnlockSoft.ready(d));
        String line = openLine(d);
        assertFalse(line.contains("开孔门槛−1待用"));
        assertTrue(EmberGemSocket.unlockSoftListTail(d).isEmpty());
    }

    @Test
    public void used_rejectHardTable() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberGemHoleUnlockSoft.C_USED, "day", 1);
        String rej = EmberGemSocket.unlockRejectLine(d);
        assertTrue(rej.contains("≥5"));
        assertTrue(rej.contains("≥3"));
        assertFalse(rej.contains("今日门槛−1"));
    }

    @Test
    public void multipleOpen_doesNotLatch() {
        PlayerData d = new PlayerData();
        for (int i = 0; i < 5; i++) {
            openLine(d);
            assertFalse(EmberGemSocket.unlockSoftListTail(d).isEmpty());
            assertTrue(EmberGemHoleUnlockSoft.ready(d));
            assertFalse(EmberGemHoleUnlockSoft.usedToday(d));
        }
    }

    @Test
    public void cueConstantStable_deltaUnchanged() {
        assertEquals("今日开孔门槛−1待用（刃二孔≥4·护符≥2）", EmberGemHoleUnlockSoft.CUE);
        assertEquals(1, EmberGemHoleUnlockSoft.DELTA);
        assertEquals("p1_gem_hole_unlock_soft", EmberGemHoleUnlockSoft.C_USED);
    }

    @Test
    public void softEligible_noLatch() {
        PlayerData d = new PlayerData();
        EmberItemData blade4 = EmberItemData.create("scorch", "blade", 2, 1, 0, 4, true, "drop");
        assertTrue(EmberGemHoleUnlockSoft.softEligible(blade4, 1));
        assertTrue(EmberGemHoleUnlockSoft.ready(d));
        assertFalse(EmberGemHoleUnlockSoft.usedToday(d));
        EmberItemData blade3 = EmberItemData.create("scorch", "blade", 2, 1, 0, 3, true, "drop");
        assertFalse(EmberGemHoleUnlockSoft.softEligible(blade3, 1));
    }
}
