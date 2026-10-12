package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.p1.encounter.EmberBossFeel;

/**
 * D12366 TIP-N: warn_bar_hold refresh + pre_half_cue once/latch + disable flags.
 */
public class EmberBossFeelThinTest {

    @Test
    public void shouldRefreshWarnBar_intervalAndFlags() {
        long t0 = 10_000L;
        assertFalse(EmberBossFeel.shouldRefreshWarnBar(t0, t0, 250L, true, true)); // just seeded
        assertFalse(EmberBossFeel.shouldRefreshWarnBar(t0 + 249L, t0, 250L, true, true));
        assertTrue(EmberBossFeel.shouldRefreshWarnBar(t0 + 250L, t0, 250L, true, true));
        assertTrue(EmberBossFeel.shouldRefreshWarnBar(t0, 0L, 250L, true, true)); // never held
        assertFalse(EmberBossFeel.shouldRefreshWarnBar(t0 + 1000L, t0, 250L, false, true)); // hold off
        assertFalse(EmberBossFeel.shouldRefreshWarnBar(t0 + 1000L, t0, 250L, true, false)); // warn inactive
        assertEquals(250L, EmberBossFeel.WARN_BAR_HOLD_MS);
    }

    @Test
    public void shouldPreHalfCue_onceAt55() {
        assertTrue(EmberBossFeel.shouldPreHalfCue(0.55, false, true));
        assertTrue(EmberBossFeel.shouldPreHalfCue(0.40, false, true));
        assertFalse(EmberBossFeel.shouldPreHalfCue(0.56, false, true));
        assertFalse(EmberBossFeel.shouldPreHalfCue(0.50, true, true)); // already told
        assertFalse(EmberBossFeel.shouldPreHalfCue(0.50, false, false)); // disabled
        assertEquals(0.55, EmberBossFeel.PRE_HALF_RATIO, 1e-9);
    }

    @Test
    public void preHalfCueLine_storyRoots() {
        assertEquals("§e即将半血 · 招式变强", EmberBossFeel.preHalfCueLine("q01"));
        assertEquals("§e即将半血 · 破招", EmberBossFeel.preHalfCueLine("q02"));
        assertEquals("§e即将半血 · 增援+破招", EmberBossFeel.preHalfCueLine("q03"));
        assertEquals("§e即将半血 · 破招", EmberBossFeel.preHalfCueLine("q07"));
        assertTrue(EmberBossFeel.preHalfCueLine("r01").startsWith("§e即将半血"));
        assertEquals("破招", EmberBossFeel.preHalfShortRoot("q05"));
    }
}
