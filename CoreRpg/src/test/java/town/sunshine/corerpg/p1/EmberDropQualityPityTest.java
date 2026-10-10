package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

public class EmberDropQualityPityTest {

    @Test
    public void hope_glance() {
        String mid = EmberDropQualityPity.hope(null);
        assertTrue(mid.contains("/" + EmberDropQualityPity.NEED));
        assertFalse(mid.contains("下次掉落至少精良"));
    }

    @Test
    public void applicable_mainline_only() {
        EmberRunMaps.MapDef q = new EmberRunMaps.MapDef("q01", java.util.Collections.emptyMap());
        EmberRunSession s = new EmberRunSession();
        s.challenge = false;
        s.abyss = 0;
        assertTrue(EmberDropQualityPity.applicable(s, q));
        s.challenge = true;
        assertFalse(EmberDropQualityPity.applicable(s, q));
    }

    @Test
    public void force_floor_after_need() {
        PlayerData pd = new PlayerData();
        pd.addPeriodCount(EmberDropQualityPity.C_PITY, "all", EmberDropQualityPity.NEED);
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("base_item", EmberRunRules.Kind.ITEM, "uid", 1,
                new EmberRunRules.ItemRoll("scorch", "blade", 1, 0, 0)));
        assertTrue(EmberDropQualityPity.applyToGrants(pd, g));
        assertEquals(1, g.get(0).item.quality);
        assertEquals(0, EmberDropQualityPity.get(pd));
    }

    @Test
    public void increment_on_standard() {
        PlayerData pd = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("base_item", EmberRunRules.Kind.ITEM, "uid", 1,
                new EmberRunRules.ItemRoll("scorch", "blade", 1, 0, 0)));
        assertFalse(EmberDropQualityPity.applyToGrants(pd, g));
        assertEquals(1, EmberDropQualityPity.get(pd));
        assertEquals(0, g.get(0).item.quality);
    }
}
