package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGemAcquireTest {
    @Test public void pity_need_and_craft() {
        assertEquals(5, EmberGemAcquire.PITY_NEED);
        assertEquals(8, EmberGemAcquire.CRAFT_SHARD);
        assertEquals(4, EmberGemAcquire.CRAFT_BONE);
        assertEquals(40, EmberGemAcquire.CRAFT_COIN);
        EmberUpgradeRules.Cost c = EmberGemAcquire.craftCost();
        assertEquals(8, c.shards);
        assertEquals(4, c.bone);
        assertEquals(40, c.coins);
        assertEquals("p1_gem_clear_pity", EmberGemAcquire.C_PITY);
    }
    @Test public void hope_meter_style() {
        String empty = EmberHopeMeter.gemPity(0, 5);
        assertTrue(empty.contains("0") && empty.contains("5"));
        assertTrue(EmberHopeMeter.gemPity(5, 5).contains("下一通关得石"));
        assertNotNull(EmberGemAcquire.hopeLine(null));
    }
    @Test public void null_map_no_pity() {
        assertFalse(EmberGemAcquire.countsForPity(null, null));
        EmberRunSession s = new EmberRunSession();
        s.abyss = 0;
        assertFalse(EmberGemAcquire.countsForPity(null, s));
    }
}
