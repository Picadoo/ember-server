package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

/**
 * TIP-BD DESIGN §2.3: enhance preview hole copy honesty;
 * preview never latches UnlockSoft; button sublabel follows ready.
 */
public class EmberEnhancePlus5HoleCopyTest {

    @Test
    public void ready_bladePlus3_previewContainsCue_noConsume() {
        PlayerData pd = new PlayerData();
        EmberItemData blade = EmberItemData.create("scorch", "blade", 2, 1, 0, 3, true, "drop");
        assertTrue(EmberGemHoleUnlockSoft.ready(pd));
        assertTrue(EmberGemSocket.heldHasLockedHole(blade));
        String preview = "强化预览: +3 → +4  成功率 70%"
                + EmberGemSocket.enhancePreviewHoleTail(pd, blade);
        assertTrue(preview.contains("开孔门槛−1"));
        assertTrue(preview.contains("≥4"));
        assertTrue(preview.contains(EmberGemHoleUnlockSoft.CUE));
        assertFalse(EmberGemHoleUnlockSoft.usedToday(pd));
        assertTrue(EmberGemHoleUnlockSoft.ready(pd));
    }

    @Test
    public void used_samePiece_previewNoCueTail() {
        PlayerData pd = new PlayerData();
        pd.addPeriodCount(EmberGemHoleUnlockSoft.C_USED, "day", 1);
        EmberItemData blade = EmberItemData.create("scorch", "blade", 2, 1, 0, 3, true, "drop");
        assertTrue(EmberGemSocket.heldHasLockedHole(blade));
        String tail = EmberGemSocket.enhancePreviewHoleTail(pd, blade);
        assertTrue(tail.isEmpty());
        assertFalse(tail.contains("开孔门槛−1待用"));
    }

    @Test
    public void ready_buttonHint_softTier() {
        PlayerData pd = new PlayerData();
        String hint = EmberGemSocket.enhanceUnlockButtonHint(pd);
        assertTrue(hint.contains("≥4"));
        assertTrue(hint.contains("≥2"));
        assertTrue(hint.contains("冲软开孔"));
    }

    @Test
    public void used_buttonHint_hardTable() {
        PlayerData pd = new PlayerData();
        pd.addPeriodCount(EmberGemHoleUnlockSoft.C_USED, "day", 1);
        String hint = EmberGemSocket.enhanceUnlockButtonHint(pd);
        assertTrue(hint.contains("≥5"));
        assertTrue(hint.contains("≥3"));
        assertTrue(hint.contains("冲强化开孔"));
        assertFalse(hint.contains("冲软开孔"));
    }

    @Test
    public void multiplePreview_doesNotLatch() {
        PlayerData pd = new PlayerData();
        EmberItemData blade = EmberItemData.create("scorch", "blade", 2, 1, 0, 3, true, "drop");
        for (int i = 0; i < 5; i++) {
            String tail = EmberGemSocket.enhancePreviewHoleTail(pd, blade);
            assertFalse(tail.isEmpty());
            assertTrue(tail.contains(EmberGemHoleUnlockSoft.CUE));
            assertTrue(EmberGemHoleUnlockSoft.ready(pd));
            assertFalse(EmberGemHoleUnlockSoft.usedToday(pd));
        }
    }

    @Test
    public void hopeEnhance_readyLocked_appendsCue_pityUnchanged() {
        PlayerData pd = new PlayerData();
        EmberItemData blade = EmberItemData.create("scorch", "blade", 2, 1, 0, 3, true, "drop");
        String plain = EmberHopeMeter.enhance(blade);
        String soft = EmberHopeMeter.enhance(blade, pd);
        assertEquals(plain + " · " + EmberGemHoleUnlockSoft.CUE, soft);
        assertFalse(plain.contains("开孔门槛−1"));
        assertFalse(EmberGemHoleUnlockSoft.usedToday(pd));
        // used → no cue
        pd.addPeriodCount(EmberGemHoleUnlockSoft.C_USED, "day", 1);
        assertFalse(EmberHopeMeter.enhance(blade, pd).contains("开孔门槛−1待用"));
    }

    @Test
    public void unlockedBlade_noTailEvenIfReady() {
        PlayerData pd = new PlayerData();
        EmberItemData blade5 = EmberItemData.create("scorch", "blade", 2, 1, 0, 5, true, "drop");
        assertTrue(blade5.sockUnlocked(1));
        assertFalse(EmberGemSocket.heldHasLockedHole(blade5));
        assertTrue(EmberGemSocket.enhancePreviewHoleTail(pd, blade5).isEmpty());
    }

    @Test
    public void deltaAndCueUnchanged() {
        assertEquals(1, EmberGemHoleUnlockSoft.DELTA);
        assertEquals("p1_gem_hole_unlock_soft", EmberGemHoleUnlockSoft.C_USED);
        assertEquals("今日开孔门槛−1待用（刃二孔≥4·护符≥2）", EmberGemHoleUnlockSoft.CUE);
    }
}
