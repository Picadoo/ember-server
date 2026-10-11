package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

public class EmberParryMissBreakTest {

    @Test
    public void streak_and_consume() {
        PlayerData d = new PlayerData();
        assertFalse(EmberParryMissBreak.ready(d));
        EmberParryMissBreak.onMiss(d);
        EmberParryMissBreak.onMiss(d);
        assertFalse(EmberParryMissBreak.ready(d));
        EmberParryMissBreak.onMiss(d);
        assertTrue(EmberParryMissBreak.ready(d));
        assertEquals(EmberParryMissBreak.BONUS_MS, EmberParryMissBreak.consumeBonus(d));
        assertEquals(0, EmberParryMissBreak.streak(d));
        assertTrue(EmberParryMissBreak.usedToday(d));
        assertEquals(0L, EmberParryMissBreak.consumeBonus(d));
    }

    @Test
    public void hit_clears() {
        PlayerData d = new PlayerData();
        EmberParryMissBreak.onMiss(d);
        EmberParryMissBreak.onMiss(d);
        EmberParryMissBreak.onMiss(d);
        EmberParryMissBreak.onHit(d);
        assertEquals(0, EmberParryMissBreak.streak(d));
        assertFalse(EmberParryMissBreak.ready(d));
    }
}
