package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-CU D13326: short_div — 日差 chase / n / ok / done（零经济软目标 3）。
 */
public class EmberShortDayDivChaseTest {

    private static java.util.function.Predicate<String> unlockedOf(String... keys) {
        final Set<String> set = new HashSet<String>(Arrays.asList(keys));
        return new java.util.function.Predicate<String>() {
            @Override public boolean test(String k) { return set.contains(k); }
        };
    }

    private static java.util.function.ToIntFunction<String> intOf(Map<String, Integer> m) {
        final Map<String, Integer> map = m;
        return new java.util.function.ToIntFunction<String>() {
            @Override public int applyAsInt(String k) {
                Integer v = map.get(k);
                return v == null ? 0 : v.intValue();
            }
        };
    }

    private static Map<String, Integer> mapOf(String... pairs) {
        Map<String, Integer> m = new HashMap<String, Integer>();
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            m.put(pairs[i], Integer.valueOf(pairs[i + 1]));
        }
        return m;
    }

    @Test
    public void chaseUnlockedDayLeftZeroRewardedStableOrder() {
        // sx01 rewarded, sx03 has dayLeft but already rewarded → skip; sx05 qualifies first in order
        assertEquals("sx05", EmberShortDayDivChase.chaseKey("sx09",
                unlockedOf("sx01", "sx03", "sx05"),
                intOf(mapOf("sx01", "0", "sx03", "2", "sx05", "3")),
                intOf(mapOf("sx01", "1", "sx03", "1", "sx05", "0"))));
    }

    @Test
    public void todayKeyPreferredWhenQualifies() {
        assertEquals("sx07", EmberShortDayDivChase.chaseKey("sx07",
                unlockedOf("sx01", "sx03", "sx07"),
                intOf(mapOf("sx01", "3", "sx03", "2", "sx07", "1")),
                intOf(mapOf("sx01", "0", "sx03", "0", "sx07", "0"))));
    }

    @Test
    public void todayKeySkippedWhenAlreadyRewarded() {
        // today sx07 already rewarded → skip; next qualifying sx03
        assertEquals("sx03", EmberShortDayDivChase.chaseKey("sx07",
                unlockedOf("sx03", "sx07"),
                intOf(mapOf("sx03", "2", "sx07", "1")),
                intOf(mapOf("sx03", "0", "sx07", "2"))));
    }

    @Test
    public void dayLeftZeroExcludedEvenIfRewardedZero() {
        // dayLeft 0 (cap exhausted conceptually) — not chase
        assertEquals("sx04", EmberShortDayDivChase.chaseKey(null,
                unlockedOf("sx02", "sx04"),
                intOf(mapOf("sx02", "0", "sx04", "1")),
                intOf(mapOf("sx02", "0", "sx04", "0"))));
    }

    @Test
    public void unlockedSkipped() {
        assertEquals("sx04", EmberShortDayDivChase.chaseKey("sx01",
                unlockedOf("sx04", "sx08"),
                intOf(mapOf("sx01", "3", "sx04", "1", "sx08", "2")),
                intOf(mapOf("sx01", "0", "sx04", "0", "sx08", "0"))));
        assertEquals("", EmberShortDayDivChase.chaseKey("sx01", unlockedOf(),
                intOf(mapOf("sx01", "3")), intOf(mapOf("sx01", "0"))));
        assertEquals("", EmberShortDayDivChase.chaseKey("sx01", null,
                intOf(mapOf()), intOf(mapOf())));
        assertEquals("", EmberShortDayDivChase.chaseKey("sx01", unlockedOf("sx01"), null, intOf(mapOf())));
        assertEquals("", EmberShortDayDivChase.chaseKey("sx01", unlockedOf("sx01"),
                intOf(mapOf("sx01", "2")), null));
    }

    @Test
    public void allRewardedOrNoDayLeftEmptyChase() {
        assertEquals("", EmberShortDayDivChase.chaseKey("sx05",
                unlockedOf("sx01", "sx05"),
                intOf(mapOf("sx01", "0", "sx05", "0")),
                intOf(mapOf("sx01", "3", "sx05", "3"))));
        assertEquals("", EmberShortDayDivChase.chaseKey("sx05",
                unlockedOf("sx01", "sx05"),
                intOf(mapOf("sx01", "0", "sx05", "0")),
                intOf(mapOf("sx01", "0", "sx05", "0"))));
    }

    @Test
    public void distinctRewardedCountsUnlockedOnly() {
        assertEquals(0, EmberShortDayDivChase.distinctRewarded(unlockedOf(), intOf(mapOf("sx01", "2"))));
        assertEquals(2, EmberShortDayDivChase.distinctRewarded(
                unlockedOf("sx01", "sx03", "sx05"),
                intOf(mapOf("sx01", "1", "sx03", "0", "sx05", "3", "sx07", "9"))));
        assertEquals(0, EmberShortDayDivChase.distinctRewarded(null, intOf(mapOf())));
        assertEquals(0, EmberShortDayDivChase.distinctRewarded(unlockedOf("sx01"), null));
    }

    @Test
    public void chaseOkEmptyAndPresent() {
        assertEquals("0", EmberShortDayDivChase.chaseOk(""));
        assertEquals("0", EmberShortDayDivChase.chaseOk(null));
        assertEquals("1", EmberShortDayDivChase.chaseOk("sx01"));
        assertEquals("0", EmberShortDayDivChase.chaseOk(
                EmberShortDayDivChase.chaseKey("sx01", unlockedOf(),
                        intOf(mapOf("sx01", "2")), intOf(mapOf("sx01", "0")))));
        assertEquals("1", EmberShortDayDivChase.chaseOk(
                EmberShortDayDivChase.chaseKey("sx01", unlockedOf("sx01"),
                        intOf(mapOf("sx01", "2")), intOf(mapOf("sx01", "0")))));
    }

    @Test
    public void doneNGe3OrNoChase() {
        assertEquals("1", EmberShortDayDivChase.done(3, "sx01"));
        assertEquals("1", EmberShortDayDivChase.done(4, "sx01"));
        assertEquals("1", EmberShortDayDivChase.done(0, ""));
        assertEquals("1", EmberShortDayDivChase.done(2, null));
        assertEquals("0", EmberShortDayDivChase.done(0, "sx01"));
        assertEquals("0", EmberShortDayDivChase.done(2, "sx05"));
    }

    @Test
    public void lineAndProgress() {
        assertEquals("今日有奖不同本 0/3", EmberShortDayDivChase.line(0));
        assertEquals("今日有奖不同本 2/3", EmberShortDayDivChase.line(2));
        assertEquals("2/3", EmberShortDayDivChase.progress(2));
        assertEquals(3, EmberShortDayDivChase.SOFT_TARGET);
    }

    /**
     * TIP-CG hotfix alignment: chase 谓词须对齐 EmberShortPath（progressFlag/q01），
     * 而非 EmberRunService.unlocked（C_UNLOCK）。仅 Q01 首通 → chase 可开。
     */
    @Test
    public void onlyQ01ProgressFlagOpensChase() {
        PlayerData d = new PlayerData();
        EmberFirstClear.setFact(d, "q01", true);
        java.util.function.Predicate<String> adminUnlock = new java.util.function.Predicate<String>() {
            @Override public boolean test(String k) {
                return d.periodCount(EmberRunService.C_UNLOCK + k, "all") > 0;
            }
        };
        java.util.function.Predicate<String> shortPath = new java.util.function.Predicate<String>() {
            @Override public boolean test(String k) {
                return EmberFirstClear.fact(d, EmberShortRules.REQUIRES);
            }
        };
        java.util.function.ToIntFunction<String> left = new java.util.function.ToIntFunction<String>() {
            @Override public int applyAsInt(String k) { return 2; }
        };
        java.util.function.ToIntFunction<String> rewarded = new java.util.function.ToIntFunction<String>() {
            @Override public int applyAsInt(String k) { return 0; }
        };
        assertEquals("0", EmberShortDayDivChase.chaseOk(
                EmberShortDayDivChase.chaseKey("sx01", adminUnlock, left, rewarded)));
        String chase = EmberShortDayDivChase.chaseKey("sx01", shortPath, left, rewarded);
        assertEquals("sx01", chase);
        assertEquals("1", EmberShortDayDivChase.chaseOk(chase));

        EmberRunMaps.MapDef sx = new EmberRunMaps.MapDef("sx01", java.util.Collections.<String, Object>emptyMap());
        sx.shortExpedition = true;
        assertFalse(EmberShortPath.unlocked(null, d, sx));
    }
}
