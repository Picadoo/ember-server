package town.sunshine.corerpg.p1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** D306 hub daily-routing priority table. */
public final class EmberHubRouteTest {

    @Test public void lowStaminaPrefersAfkSupply() {
        String p = EmberHubRoute.primary("首通 Q03", 20, 90, 30, 30, 50, false, false, 2, "Q03 残誓");
        assertTrue(p.contains("挂机/补给"));
        assertTrue(p.contains("体力不足"));
        assertFalse(p.contains("精选"));
        assertFalse(p.contains("深渊"));
    }

    @Test public void afkFullPrefersAdventureEvenWithFeatured() {
        String p = EmberHubRoute.primary("主线已完结", 90, 90, 30, 30, 50, true, true, 2, "Q03 残誓");
        assertEquals("优先：去冒险（挂机今日已满）", p);
    }

    @Test public void featuredWhenQ07AndLeftAndStamina() {
        String p = EmberHubRoute.primary("主线已完结", 60, 90, 30, 30, 50, false, true, 2, "Q05 霜封哨所");
        assertTrue(p.startsWith("优先：本周精选"));
        assertTrue(p.contains("剩 2 次"));
    }

    @Test public void preQ07NeverFeaturedOrRaidPrimary() {
        String p = EmberHubRoute.primary("首通 Q04 锈轨", 90, 90, 30, 30, 50, false, false, 3, "Q03");
        assertTrue(p.contains("主线"));
        assertTrue(p.contains("还能打 3 局"));
        assertFalse(p.contains("精选"));
        assertFalse(p.contains("团本"));
        assertFalse(p.contains("深渊"));
        String s = EmberHubRoute.secondary(90, 30, 30, 50, false, false, 3);
        assertTrue(s.contains("Q07"));
        assertFalse(s.contains("团本（50"));
    }

    @Test public void secondaryRaidOnlyWhenUnlockedAndAffordable() {
        String s = EmberHubRoute.secondary(90, 30, 30, 50, false, true, 0);
        assertTrue(s.contains("团本") || s.contains("深渊"));
        String locked = EmberHubRoute.secondary(90, 30, 30, 50, false, false, 0);
        assertFalse(locked.contains("团本（50"));
        assertFalse(locked.contains("深渊（30"));
    }

    @Test public void stripColorCodesInNext() {
        String p = EmberHubRoute.primary("§a首通 §fQ02", 90, 90, 30, 30, 50, false, false, 0, "无");
        assertFalse(p.contains("§"));
        assertTrue(p.contains("首通 Q02"));
    }
}
