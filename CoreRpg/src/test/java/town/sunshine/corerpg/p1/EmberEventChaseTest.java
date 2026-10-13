package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-CE D13310: event chase pin — featured优先 / 回落最高已通 / 全未通空 / soft_ready 边界。
 */
public class EmberEventChaseTest {

    private static java.util.function.Predicate<String> clearedOf(String... keys) {
        final Set<String> set = new HashSet<String>(Arrays.asList(keys));
        return new java.util.function.Predicate<String>() {
            @Override public boolean test(String k) { return set.contains(k); }
        };
    }

    @Test
    public void featuredClearedPreferred() {
        assertEquals("q03", EmberEventChase.chaseKey("q03", clearedOf("q01", "q03", "q07")));
        assertEquals("q07", EmberEventChase.chaseKey("q07", clearedOf("q01", "q07")));
    }

    @Test
    public void fallbackHighestClearedWhenFeaturedUncleared() {
        assertEquals("q05", EmberEventChase.chaseKey("q03", clearedOf("q01", "q02", "q05")));
        assertEquals("q07", EmberEventChase.chaseKey("q01", clearedOf("q04", "q07")));
        assertEquals("q02", EmberEventChase.chaseKey("q06", clearedOf("q02")));
    }

    @Test
    public void allUnclearedEmpty() {
        assertEquals("", EmberEventChase.chaseKey("q03", clearedOf()));
        assertEquals("", EmberEventChase.chaseKey(null, clearedOf()));
        assertEquals("", EmberEventChase.chaseKey("", clearedOf()));
        assertEquals("", EmberEventChase.chaseKey("q03", null));
    }

    @Test
    public void featuredBlankFallsToHighest() {
        assertEquals("q04", EmberEventChase.chaseKey("", clearedOf("q01", "q04")));
        assertEquals("q04", EmberEventChase.chaseKey(null, clearedOf("q01", "q04")));
    }

    @Test
    public void softReadyBoundary() {
        assertEquals("0", EmberEventChase.softReady(0));
        assertEquals("0", EmberEventChase.softReady(EmberEventSoftPity.NEED - 1));
        assertEquals("1", EmberEventChase.softReady(EmberEventSoftPity.NEED));
        assertEquals("1", EmberEventChase.softReady(EmberEventSoftPity.NEED + 5));
        assertFalse(EmberEventChase.isSoftReady(0));
        assertTrue(EmberEventChase.isSoftReady(EmberEventSoftPity.NEED));
    }

    @Test
    public void softReadyMatchesLiveSoftPityGet() {
        PlayerData pd = new PlayerData();
        assertEquals("0", EmberEventChase.softReady(EmberEventSoftPity.get(pd)));
        pd.addPeriodCount(EmberEventSoftPity.C_DRY, "all", EmberEventSoftPity.NEED);
        assertEquals("1", EmberEventChase.softReady(EmberEventSoftPity.get(pd)));
        // NEED pin unchanged
        assertEquals(1, EmberEventSoftPity.NEED);
    }

    @Test
    public void chaseOkEmptyAndPresent() {
        assertEquals("0", EmberEventChase.chaseOk(""));
        assertEquals("0", EmberEventChase.chaseOk(null));
        assertEquals("1", EmberEventChase.chaseOk("q01"));
        assertEquals("1", EmberEventChase.chaseOk("q07"));
        // same as live resolve: empty chaseKey → 0
        assertEquals("0", EmberEventChase.chaseOk(EmberEventChase.chaseKey("q03", clearedOf())));
        assertEquals("1", EmberEventChase.chaseOk(EmberEventChase.chaseKey("q03", clearedOf("q01", "q03"))));
    }

    @Test
    public void mainKeysAreQ01ThroughQ07() {
        assertEquals(7, EmberEventChase.MAIN_KEYS.length);
        assertEquals("q01", EmberEventChase.MAIN_KEYS[0]);
        assertEquals("q07", EmberEventChase.MAIN_KEYS[6]);
    }
}
