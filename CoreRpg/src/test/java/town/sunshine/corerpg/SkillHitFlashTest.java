package town.sunshine.corerpg;

import org.junit.Test;
import town.sunshine.corerpg.p1.EmberSetService;

import static org.junit.Assert.*;

/**
 * D445: skill hit-confirm flash pure helpers (烬斩命中 / 烬突落地).
 * Mirrors EmberSetEngineTest D444 debounce style — no Bukkit required.
 */
public class SkillHitFlashTest {

    @Test
    public void slashHitFlashesOnce_whenValidTarget_D445() {
        assertTrue(SkillService.shouldFlashSlashHit(1, 0L, 1000L));
        assertTrue(SkillService.shouldFlashSlashHit(5, 0L, 1000L)); // multi-hit same swing still eligible once
    }

    @Test
    public void slashHitDebounce_sameWindowNoSecondFlash_D445() {
        long t0 = 10_000L;
        assertTrue(SkillService.shouldFlashSlashHit(3, 0L, t0));
        // within 400ms debounce → skip
        assertFalse(SkillService.shouldFlashSlashHit(2, t0, t0 + 200L));
        assertFalse(SkillService.shouldFlashSlashHit(1, t0, t0 + SkillService.SKILL_HIT_FLASH_DEBOUNCE_MS - 1L));
        // at/after debounce → allow
        assertTrue(SkillService.shouldFlashSlashHit(1, t0, t0 + SkillService.SKILL_HIT_FLASH_DEBOUNCE_MS));
        assertTrue(SkillService.shouldFlashSlashHit(1, t0, t0 + 1000L));
    }

    @Test
    public void slashNoTarget_noFlash_D445() {
        assertFalse(SkillService.shouldFlashSlashHit(0, 0L, 1000L));
        assertFalse(SkillService.shouldFlashSlashHit(-1, 0L, 1000L));
    }

    @Test
    public void dashCancel_noFlash_D445() {
        assertFalse(SkillService.shouldFlashDashLand(false)); // 前方受阻
        assertTrue(SkillService.shouldFlashDashLand(true));   // 位移成功
    }

    @Test
    public void skillFlashWindow_atMost12s_andYieldContract_D445() {
        assertTrue("skill flash ≤1.2s", EmberSetService.SKILL_FLASH_MS <= 1200L);
        assertTrue(EmberSetService.SKILL_FLASH_MS > 0L);
        assertTrue("debounce ≥0.4s", SkillService.SKILL_HIT_FLASH_DEBOUNCE_MS >= 400L);
    }
}
