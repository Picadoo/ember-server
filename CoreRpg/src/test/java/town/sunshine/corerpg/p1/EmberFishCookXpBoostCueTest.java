package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;
import town.sunshine.corerpg.LifeService;
import town.sunshine.corerpg.PlayerData;

/**
 * TIP-BB DESIGN §2.3: fish/cook XP boost ready cues on LifeService.show;
 * opening the list never latches; consumeXp only on successful fish/cook.
 */
public class EmberFishCookXpBoostCueTest {

    /** Simulate show head + cook lines (no Bukkit). */
    private static String[] showLines(PlayerData d, boolean cookCoinSoftReady) {
        String head = "—— 枢纽补给 · 生活 ——  生活 Lv.1（经验 0/10）  余烬币 0";
        head = head + LifeService.fishXpBoostShowTail(d);
        String cook = " cook — 代烤鱼：鳕鱼/鲑鱼 → 炭烤余烬鱼，每条 1 币";
        if (cookCoinSoftReady) cook = cook + " · 今日减负待用（×0.5）";
        cook = cook + LifeService.cookXpBoostShowTail(d);
        return new String[] { head, cook };
    }

    @Test
    public void fishReady_showContainsCue_noConsume() {
        PlayerData d = new PlayerData();
        assertTrue(EmberFishXpBoost.ready(d));
        String[] lines = showLines(d, false);
        assertTrue(lines[0].contains("首钓加验×2待用"));
        assertTrue(lines[0].contains(EmberFishXpBoost.CUE));
        assertFalse(EmberFishXpBoost.usedToday(d));
        assertTrue(EmberFishXpBoost.ready(d));
    }

    @Test
    public void cookReady_cookLineContainsCue_noConsume() {
        PlayerData d = new PlayerData();
        assertTrue(EmberCookXpBoost.ready(d));
        String[] lines = showLines(d, true); // coin soft may coexist
        assertTrue(lines[1].contains("烹饪加验×2待用"));
        assertTrue(lines[1].contains(EmberCookXpBoost.CUE));
        assertTrue(lines[1].contains("今日减负待用"));
        assertFalse(EmberCookXpBoost.usedToday(d));
        assertTrue(EmberCookXpBoost.ready(d));
    }

    @Test
    public void fishUsed_showNoFishCue() {
        PlayerData d = new PlayerData();
        EmberFishXpBoost.consumeXp(d, 2);
        assertFalse(EmberFishXpBoost.ready(d));
        String[] lines = showLines(d, false);
        assertFalse(lines[0].contains("首钓加验"));
        assertTrue(LifeService.fishXpBoostShowTail(d).isEmpty());
    }

    @Test
    public void cookUsed_cookLineNoCookXpCue_coinSoftMayRemain() {
        PlayerData d = new PlayerData();
        EmberCookXpBoost.consumeXp(d, 2);
        assertFalse(EmberCookXpBoost.ready(d));
        String[] lines = showLines(d, true);
        assertFalse(lines[1].contains("烹饪加验"));
        assertTrue(lines[1].contains("今日减负待用")); // AY coin soft still shown when ready flag forced
        assertTrue(LifeService.cookXpBoostShowTail(d).isEmpty());
    }

    @Test
    public void multipleShow_doesNotLatch() {
        PlayerData d = new PlayerData();
        for (int i = 0; i < 5; i++) {
            showLines(d, true);
            assertTrue(EmberFishXpBoost.ready(d));
            assertTrue(EmberCookXpBoost.ready(d));
            assertFalse(EmberFishXpBoost.usedToday(d));
            assertFalse(EmberCookXpBoost.usedToday(d));
            assertFalse(LifeService.fishXpBoostShowTail(d).isEmpty());
            assertFalse(LifeService.cookXpBoostShowTail(d).isEmpty());
        }
    }

    @Test
    public void cueConstantsStable() {
        assertEquals("今日首钓加验×2待用", EmberFishXpBoost.CUE);
        assertEquals("今日烹饪加验×2待用", EmberCookXpBoost.CUE);
        assertEquals(2, EmberFishXpBoost.MULT);
        assertEquals(2, EmberCookXpBoost.MULT);
        assertEquals("p1_fish_xp_boost", EmberFishXpBoost.C_USED);
        assertEquals("p1_cook_xp_boost", EmberCookXpBoost.C_USED);
    }
}
