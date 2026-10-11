package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberMapStashPinTest {
    @Test
    public void pinKeyNormalizes() {
        assertEquals("q04", EmberMapStashPin.pinKey("q04"));
        assertEquals("q04", EmberMapStashPin.pinKey("q04c"));
        assertEquals("q01", EmberMapStashPin.pinKey("Q01"));
        assertNull(EmberMapStashPin.pinKey("raid01"));
        assertNull(EmberMapStashPin.pinKey(null));
    }

    @Test
    public void filterMapMatchesOrigin() {
        EmberStorageRules.Filter f = new EmberStorageRules.Filter();
        f.map = "q04";
        EmberItemData hit = EmberItemData.create("scorch", "blade", 2, 1, 0, 0, true, "drop");
        // create may not set origin — build with origin via constructor path
        EmberItemData.Origin o = EmberItemData.Origin.of("q04", "S01", "q04-abc", 1L);
        EmberItemData with = new EmberItemData(hit.uid, hit.ni, hit.family, hit.slot, hit.tier, hit.quality, hit.craft,
                hit.enhance, hit.pity, hit.bound, hit.source, EmberItemData.DATA_VERSION, hit.rev,
                hit.affix, hit.afPity, hit.sigCode, hit.rerollN, o);
        EmberItemData miss = new EmberItemData(hit.uid + "x", hit.ni, hit.family, hit.slot, hit.tier, hit.quality, hit.craft,
                hit.enhance, hit.pity, hit.bound, hit.source, EmberItemData.DATA_VERSION, hit.rev,
                hit.affix, hit.afPity, hit.sigCode, hit.rerollN, EmberItemData.Origin.of("q01", "S01", "q01-x", 1L));
        assertTrue(f.matches(with));
        assertFalse(f.matches(miss));
        f.map = null;
        assertTrue(f.matches(miss));
    }

    @Test
    public void cycleMapWalks() {
        EmberStorageRules.Filter f = new EmberStorageRules.Filter();
        assertNull(f.map);
        f.cycleMap();
        assertEquals("q01", f.map);
        for (int i = 0; i < 7; i++) f.cycleMap();
        assertNull(f.map);
    }
}
