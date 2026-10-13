package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * TIP-CG D13312: goal_short_chase — day_left 优先 / 全满回落练习 / 未解锁跳过 / done·ok 边界。
 */
public class EmberGoalShortChaseTest {

    private static java.util.function.Predicate<String> unlockedOf(String... keys) {
        final Set<String> set = new HashSet<String>(Arrays.asList(keys));
        return new java.util.function.Predicate<String>() {
            @Override public boolean test(String k) { return set.contains(k); }
        };
    }

    private static java.util.function.ToIntFunction<String> leftOf(Map<String, Integer> m) {
        final Map<String, Integer> map = m;
        return new java.util.function.ToIntFunction<String>() {
            @Override public int applyAsInt(String k) {
                Integer v = map.get(k);
                return v == null ? 0 : v.intValue();
            }
        };
    }

    private static Map<String, Integer> leftMap(String... pairs) {
        Map<String, Integer> m = new HashMap<String, Integer>();
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            m.put(pairs[i], Integer.valueOf(pairs[i + 1]));
        }
        return m;
    }

    @Test
    public void dayLeftPreferredStableOrder() {
        // unlocked sx01+sx03+sx05; only sx03/sx05 have day_left>0 → sx03 (stable, first in SHORT_KEYS)
        assertEquals("sx03", EmberGoalShortChase.chaseKey("sx07",
                unlockedOf("sx01", "sx03", "sx05"),
                leftOf(leftMap("sx01", "0", "sx03", "2", "sx05", "1"))));
    }

    @Test
    public void sxTodayPreferredWhenDayLeft() {
        assertEquals("sx07", EmberGoalShortChase.chaseKey("sx07",
                unlockedOf("sx01", "sx03", "sx07"),
                leftOf(leftMap("sx01", "3", "sx03", "2", "sx07", "1"))));
    }

    @Test
    public void allFullFallsBackToPractice() {
        // all day_left=0 → first unlocked in stable order
        assertEquals("sx02", EmberGoalShortChase.chaseKey("sx09",
                unlockedOf("sx02", "sx05"),
                leftOf(leftMap("sx02", "0", "sx05", "0", "sx09", "0"))));
        // practice: prefer unlocked sx_today when all full
        assertEquals("sx05", EmberGoalShortChase.chaseKey("sx05",
                unlockedOf("sx02", "sx05"),
                leftOf(leftMap("sx02", "0", "sx05", "0"))));
    }

    @Test
    public void unlockedSkipped() {
        // sx01 has day_left but locked; sx04 unlocked with day_left → sx04
        assertEquals("sx04", EmberGoalShortChase.chaseKey("sx01",
                unlockedOf("sx04", "sx08"),
                leftOf(leftMap("sx01", "3", "sx04", "1", "sx08", "0"))));
        // sx_today locked with day_left → ignore today, take first unlocked with left
        assertEquals("sx03", EmberGoalShortChase.chaseKey("sx01",
                unlockedOf("sx03"),
                leftOf(leftMap("sx01", "2", "sx03", "1"))));
    }

    @Test
    public void noneUnlockedEmpty() {
        assertEquals("", EmberGoalShortChase.chaseKey("sx01", unlockedOf(), leftOf(leftMap("sx01", "3"))));
        assertEquals("", EmberGoalShortChase.chaseKey(null, unlockedOf(), leftOf(leftMap())));
        assertEquals("", EmberGoalShortChase.chaseKey("sx01", null, leftOf(leftMap())));
        assertEquals("", EmberGoalShortChase.chaseKey("sx01", unlockedOf("sx01"), null));
    }

    @Test
    public void doneBoundary() {
        assertEquals("0", EmberGoalShortChase.done(0, 5));
        assertEquals("0", EmberGoalShortChase.done(4, 5));
        assertEquals("1", EmberGoalShortChase.done(5, 5));
        assertEquals("1", EmberGoalShortChase.done(6, 5));
        assertEquals("0", EmberGoalShortChase.done(0, 0)); // target absent
        assertEquals("0", EmberGoalShortChase.done(3, -1));
    }

    @Test
    public void chaseOkEmptyAndPresent() {
        assertEquals("0", EmberGoalShortChase.chaseOk(""));
        assertEquals("0", EmberGoalShortChase.chaseOk(null));
        assertEquals("1", EmberGoalShortChase.chaseOk("sx01"));
        assertEquals("0", EmberGoalShortChase.chaseOk(
                EmberGoalShortChase.chaseKey("sx01", unlockedOf(), leftOf(leftMap("sx01", "2")))));
        assertEquals("1", EmberGoalShortChase.chaseOk(
                EmberGoalShortChase.chaseKey("sx01", unlockedOf("sx01"), leftOf(leftMap("sx01", "2")))));
    }
}
