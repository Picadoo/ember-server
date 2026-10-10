package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

public class EmberEnhanceUndoTest {

    private static EmberItemData blade(int enh, int pity) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade", 2, 0, 0, enh, pity, false, "drop", 2, 0);
    }

    @Test
    public void window_only_fail() {
        UUID id = UUID.randomUUID();
        EmberEnhanceUndo.clear(id);
        EmberItemData before = blade(4, 2);
        EmberItemData afterFail = blade(4, 3);
        // force same uid
        afterFail = new EmberItemData(before.uid, afterFail.ni, afterFail.family, afterFail.slot, afterFail.tier, afterFail.quality,
                afterFail.craft, afterFail.enhance, 3, afterFail.bound, afterFail.source, afterFail.version, before.rev + 1,
                afterFail.affix, afterFail.afPity, afterFail.sigCode, afterFail.rerollN, afterFail.origin, afterFail.sock0, afterFail.sock1);
        Cost c = new Cost(5, 0, 0, 0, 20);
        EmberEnhanceUndo.afterCommit(null, Arrays.asList(new TxnItem(before, afterFail, null)), c);
        assertNull(EmberEnhanceUndo.peek(id)); // null player

        // success clears
        EmberItemData afterWin = new EmberItemData(before.uid, before.ni, before.family, before.slot, before.tier, before.quality,
                before.craft, 5, 0, before.bound, before.source, before.version, before.rev + 1,
                before.affix, before.afPity, before.sigCode, before.rerollN, before.origin, before.sock0, before.sock1);
        assertTrue(EmberEnhanceLoop.leveled(Arrays.asList(new TxnItem(before, afterWin, null))));
        assertFalse(EmberEnhanceLoop.leveled(Arrays.asList(new TxnItem(before, afterFail, null))));
    }

    @Test
    public void remainSec() {
        EmberEnhanceUndo.Snap s = new EmberEnhanceUndo.Snap(UUID.randomUUID(), "u", 1, 0, 1, 1, Cost.NONE, System.currentTimeMillis());
        assertTrue(EmberEnhanceUndo.remainSec(s, System.currentTimeMillis()) > 0);
        assertEquals(0, EmberEnhanceUndo.remainSec(s, System.currentTimeMillis() + EmberEnhanceUndo.WINDOW_MS + 1));
    }

    @Test
    public void forgeFeel_pityWin() {
        String line = EmberForgeFeel.line("enhance", "强化成功 +4 → +5（本档第 3/3 次，保底）");
        assertTrue(line.contains("保底必成"));
    }
}
