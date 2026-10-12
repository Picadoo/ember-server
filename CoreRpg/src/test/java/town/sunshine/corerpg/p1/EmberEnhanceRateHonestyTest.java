package town.sunshine.corerpg.p1;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TIP-BN D13128–D13143: held enhance preview rate honesty.
 * SoT = EmberUpgradeRules.RATE / guaranteed; display only — zero RATE/MAX_TRIES/cost edits.
 */
public class EmberEnhanceRateHonestyTest {

    private static EmberItemData bladeEnh(int enh, int pity) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade", 2, 0, 0, enh, pity, false, "drop", 1, 0);
    }

    private static EmberItemData armorPiece() {
        return EmberItemData.create("scorch", "head", 2, 0, 0, 0, false, "drop");
    }

    @Test
    public void enhanceableHeldShowsRatePct() {
        // +4 → +5 · RATE[5]=0.70 · not guaranteed (MAX_TRIES[5]=4, pity 0)
        assertEquals("70%", EmberHopeMeter.enhanceRate(bladeEnh(4, 0)));
        // +3 → +4 · RATE[4]=0.85 · attempt 1/3
        assertEquals("85%", EmberHopeMeter.enhanceRate(bladeEnh(3, 0)));
        // +5 → +6 · RATE[6]=0.55
        assertEquals("55%", EmberHopeMeter.enhanceRate(bladeEnh(5, 0)));
    }

    @Test
    public void pityGuaranteedShowsBiCheng() {
        // +3 → +4 · MAX_TRIES[4]=3 · pity 2 → next attempt guaranteed
        assertTrue(EmberUpgradeRules.guaranteed(bladeEnh(3, 2)));
        assertEquals("必成", EmberHopeMeter.enhanceRate(bladeEnh(3, 2)));
        // +0 → +1 · MAX_TRIES[1]=1 · always guaranteed (align chat 本次必成)
        assertTrue(EmberUpgradeRules.guaranteed(bladeEnh(0, 0)));
        assertEquals("必成", EmberHopeMeter.enhanceRate(bladeEnh(0, 0)));
    }

    @Test
    public void emptyAndArmorSlotsMatchPityEmpty() {
        assertEquals(EmberHopeMeter.emptySlot("无件"), EmberHopeMeter.enhanceRate(null));
        assertEquals(EmberHopeMeter.emptySlot("甲不可强"), EmberHopeMeter.enhanceRate(armorPiece()));
        // held PAPI empty label (isomorphic to held_enhance_pity)
        assertEquals("§8手持—", EmberHopeMeter.emptySlot("手持"));
        assertEquals("§8甲不可强—", EmberHopeMeter.emptySlot("甲不可强"));
    }

    @Test
    public void maxEnhanceShortRefuse() {
        String full = EmberHopeMeter.enhanceRate(bladeEnh(10, 0));
        assertTrue(full, full.contains("已满"));
        assertFalse(full.contains("%"));
        assertFalse(full.equals("必成"));
    }

    @Test
    public void rateMatchesUpgradeRulesRoundOnly() {
        for (int enh = 3; enh <= 9; enh++) {
            EmberItemData d = bladeEnh(enh, 0);
            if (EmberUpgradeRules.guaranteed(d)) continue;
            int expect = (int) Math.round(EmberUpgradeRules.rate(enh + 1) * 100);
            assertEquals(expect + "%", EmberHopeMeter.enhanceRate(d));
        }
    }

    @Test
    public void papiRoutesHeldEnhanceRate() {
        assertEquals(EmberRunPapi.Section.LOADOUT, EmberRunPapi.route("held_enhance_rate"));
        assertTrue(EmberRunPapi.isLoadoutKey("held_enhance_rate"));
    }
}
