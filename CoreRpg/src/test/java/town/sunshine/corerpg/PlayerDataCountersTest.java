package town.sunshine.corerpg;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/** B2.142: a counter brought back to 0 must stay 0 after a save/load round trip (used to reload as 1). */
public class PlayerDataCountersTest {

    @Test
    public void zeroCounterSurvivesRoundTrip() throws Exception {
        PlayerData d = new PlayerData();
        d.addPeriodCount("p1_mark_t1", "all", 8);
        d.addPeriodCount("p1_mark_t1", "all", -8);
        d.addPeriodCount("abyss_run_floor", "all", 6);
        d.addPeriodCount("abyss_run_floor", "all", -6);
        d.addPeriodCount("afk_coin", "2026-10-02", 3);
        assertFalse(d.getCounters().containsKey("p1_mark_t1@all"));
        YamlConfiguration y = new YamlConfiguration();
        y.set("counters", new LinkedHashMap<String, Integer>(d.getCounters()));
        YamlConfiguration back = new YamlConfiguration();
        back.loadFromString(y.saveToString());
        Map<String, Integer> m = PlayerDataStore.counterMap(back, "counters");
        PlayerData e = new PlayerData();
        e.setCounters(m);
        assertEquals(0, e.periodCount("p1_mark_t1", "all"));
        assertEquals(0, e.periodCount("abyss_run_floor", "all"));
        assertEquals(3, e.periodCount("afk_coin", "2026-10-02"));
    }

    @Test
    public void legacyZeroRowsLoadAsZero() throws Exception {
        YamlConfiguration y = new YamlConfiguration();
        y.loadFromString("counters:\n  p1_target@all: 0\n  ever_forge@all: 1\n");
        Map<String, Integer> m = PlayerDataStore.counterMap(y, "counters");
        assertFalse(m.containsKey("p1_target@all"));
        assertEquals(Integer.valueOf(1), m.get("ever_forge@all"));
    }
}
