package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

/** D1168 K2b / SlashReady shape — zero combat numeric. */
public class EmberForgeK2bTest {

    @Test
    public void floorGapUsesMatCostFloor() {
        String g = EmberRunPapi.forgeGapRollFloor(0, 0, 0);
        assertTrue(g.contains("胚差"));
        assertTrue(g.contains("骨差"));
        assertTrue(g.contains("币差"));
        EmberUpgradeRules.Cost c = EmberForgeRollRules.matCostFloor();
        assertEquals(12, c.blanks);
        assertEquals(8, c.bone);
        assertEquals(1300, c.coins);
    }

    @Test
    public void slashReadyShapeCopy() {
        assertEquals("§a充能 · §f烬斩 §a就绪", EmberSlashReady.readyActionBar());
        assertEquals("§a充能 · §f扇形烬斩 §a就绪", EmberSlashReady.readyActionBar("扇形"));
        assertEquals("直线烬斩充能就绪", EmberSlashReady.readyChat("直线"));
    }
}
