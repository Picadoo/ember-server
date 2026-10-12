package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberAwakenBreakTest {
    @Test
    public void charmUpgradeRaisesAwk() {
        EmberItemData blade = EmberItemData.create("scorch", "blade", 2, 1, 0, 6, true, "drop");
        EmberItemData charm = EmberItemData.create("scorch", "charm", 1, 1, 0, 6, true, "drop");
        EmberItemData better = EmberItemData.create("scorch", "charm", 2, 1, 0, 6, true, "drop");
        int cur = EmberLoadout.awakening(blade, charm);
        int next = EmberAwakenBreak.awkIfReplace(blade, charm, better);
        assertEquals(1, cur); // min tier 1 → awk I
        assertEquals(2, next); // both T2 +6 → awk II
    }

    @Test
    public void samePieceNoRaise() {
        EmberItemData blade = EmberItemData.create("burst", "blade", 2, 1, 0, 6, true, "drop");
        EmberItemData charm = EmberItemData.create("burst", "charm", 2, 1, 0, 6, true, "drop");
        EmberItemData other = EmberItemData.create("burst", "charm", 2, 1, 0, 5, true, "drop");
        assertEquals(2, EmberLoadout.awakening(blade, charm));
        assertTrue(EmberAwakenBreak.awkIfReplace(blade, charm, other) <= 2);
    }

    @Test
    public void breakKeyPrefix() {
        assertEquals("p1_awk_break_", EmberAwakenBreak.C_BREAK);
    }

    @Test
    public void shouldSwapWhenDonorHigher() {
        EmberItemData old = EmberItemData.create("scorch", "charm", 1, 1, 0, 8, true, "drop");
        EmberItemData neu = EmberItemData.create("scorch", "charm", 2, 1, 0, 3, true, "drop");
        assertTrue(EmberAwakenBreak.shouldSwapEnhance(old, neu));
        assertFalse(EmberAwakenBreak.shouldSwapEnhance(neu, old));
        assertFalse(EmberAwakenBreak.shouldSwapEnhance(old, old));
    }

    @Test
    public void awkDeltaLine_wiredForBreakPath() {
        // D12396: break path appends EmberSetFeel.awkDeltaLine; pure helper stays non-empty for rises
        assertTrue(EmberSetFeel.awkDeltaEnabled());
        assertTrue(EmberSetFeel.awkDeltaLine("scorch", 1, 2).contains("燃跳更烫"));
        EmberSetFeel.setAwkDeltaEnabledForTest(Boolean.FALSE);
        assertFalse(EmberSetFeel.awkDeltaEnabled());
        EmberSetFeel.setAwkDeltaEnabledForTest(null);
    }
}
