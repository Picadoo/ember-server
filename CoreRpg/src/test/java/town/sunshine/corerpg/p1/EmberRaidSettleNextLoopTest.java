package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-CZ D13331 W1a: 团本结算「下一环」菜单化 — 开薄页 / 清裸指令 / 零改 weeklyCap.
 */
public class EmberRaidSettleNextLoopTest {

    @Test
    public void settleNextLoopConstants_menuOpen_notBareRaidpath() {
        assertEquals("/ember_p1_raid_settle", EmberRaidSettle.SETTLE_NEXT_LOOP_CMD);
        assertEquals("[下一环]", EmberRaidSettle.SETTLE_NEXT_LOOP_LABEL);
        assertEquals("/ember_p1_adventure", EmberRaidSettle.SETTLE_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberRaidSettle.SETTLE_FORGE_CMD);
        assertFalse(EmberRaidSettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1 raidpath"));
        assertFalse(EmberRaidSettle.SETTLE_NEXT_LOOP_CMD.contains("recruit"));
    }

    @Test
    public void weekHelpers_zeroCapSafe_noEconomyInvent() {
        assertEquals(0, EmberRaidSettle.weekLeft(0, 0));
        assertEquals(1, EmberRaidSettle.weekLeft(2, 3));
        assertEquals(0, EmberRaidSettle.weekLeft(3, 3));
        assertEquals(0, EmberRaidSettle.weekLeft(4, 3));
        assertEquals("2/3", EmberRaidSettle.weekLine(2, 3));
        assertEquals("", EmberRaidSettle.weekLine(0, 0));
        assertTrue(EmberRaidSettle.reenterGrey(3, 3));
        assertFalse(EmberRaidSettle.reenterGrey(2, 3));
    }

    @Test
    public void raidLoopSource_opensThinPage_noTeachRaidpathRecruit() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRaidLoop.java")),
                StandardCharsets.UTF_8);
        int a = src.indexOf("void afterWeeklyClear(");
        assertTrue(a > 0);
        String body = src.substring(a);
        assertTrue(body.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(body.contains("EmberRaidSettle.SETTLE_NEXT_LOOP_CMD")
                || body.contains("SETTLE_NEXT_LOOP_CMD"));
        // 满/未满两态皆开薄页常量
        assertEquals(2, count(body, "SETTLE_NEXT_LOOP_CMD"));
        assertFalse(body.contains("/corerpg p1 raidpath"));
        assertFalse(body.contains("/corerpg p1 recruit"));
        // 不把 enter 裸指令当主 UI（两态主钮应是 settle）
        assertFalse(body.contains("EmberRaidPath.enterCmd"));
    }

    @Test
    public void raidPathSource_menuFirst_noTeachHandTypeRaidpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRaidPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("未选 · /corerpg p1 raidpath"));
        assertTrue(src.contains("未选 · 点下一环选路径"));
        assertFalse(src.contains("未选团本路径 · /corerpg p1 raidpath"));
        assertTrue(src.contains("SETTLE_NEXT_LOOP_CMD"));
        // offerPick / applyAndReply / afterClearHit 不以裸 raidpath|recruit 为主钮
        assertFalse(src.contains("\"/corerpg p1 raidpath\""));
        assertFalse(src.contains("\"/corerpg p1 raidpath r01\""));
        assertFalse(src.contains("\"/corerpg p1 recruit "));
        assertFalse(src.contains("\"/corerpg p1 forge\""));
        assertFalse(src.contains("\"/corerpg p1 setchase\""));
    }

    @Test
    public void weeklyCapConstantsUntouched_inRaidService() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRaidService.java")),
                StandardCharsets.UTF_8);
        // 零改 cap 语义：仍用 MapDef.weeklyCap / weekCount；无硬编码抬帽
        assertTrue(src.contains("m.weeklyCap"));
        assertTrue(src.contains("weekCount"));
        assertFalse(src.contains("weeklyCap = 4") || src.contains("weeklyCap=4"));
        assertTrue(src.contains("settlePapi") || src.contains("settle_week"));
    }

    private static int count(String hay, String needle) {
        int n = 0, i = 0;
        while ((i = hay.indexOf(needle, i)) >= 0) { n++; i += needle.length(); }
        return n;
    }
}
