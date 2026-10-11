package town.sunshine.corerpg.p1;

import org.bukkit.ChatColor;
import org.junit.Test;

import static org.junit.Assert.*;

/** D12129: menu-star name / vanilla-world helpers (no Bukkit server). */
public class EmberMenuStarTest {

    @Test
    public void menuStarNameMatchesStripped() {
        assertTrue(EmberMenuStar.isMenuStarName(ChatColor.GOLD + "余烬星"));
        assertTrue(EmberMenuStar.isMenuStarName("§6余烬星"));
        assertTrue(EmberMenuStar.isMenuStarName("余烬星"));
        assertFalse(EmberMenuStar.isMenuStarName("余烬星愿"));
        assertFalse(EmberMenuStar.isMenuStarName(null));
        assertFalse(EmberMenuStar.isMenuStarName(""));
    }

    @Test
    public void vanillaWorldsOnly() {
        assertTrue(EmberMenuStar.isVanillaWorld("world"));
        assertTrue(EmberMenuStar.isVanillaWorld("world_nether"));
        assertTrue(EmberMenuStar.isVanillaWorld("world_the_end"));
        assertFalse(EmberMenuStar.isVanillaWorld("ember_hub"));
        assertFalse(EmberMenuStar.isVanillaWorld("ember_afk"));
        assertFalse(EmberMenuStar.isVanillaWorld(null));
    }

    @Test
    public void hubCoordsNearGuide() {
        assertEquals(-16.5, EmberMenuStar.HUB_X, 0.01);
        assertEquals(58.0, EmberMenuStar.HUB_Y, 0.01);
        assertEquals(110.0, EmberMenuStar.HUB_Z, 0.01);
        assertEquals("ember_hub", EmberMenuStar.HUB);
        assertEquals(8, EmberMenuStar.HOTBAR_SLOT);
    }
}
