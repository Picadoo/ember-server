package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DB D13333 W1a: 深渊结算「下一环」菜单化 — 开薄页 / 清裸指令 / 零改掉落体力价层表.
 */
public class EmberAbyssSettleNextLoopTest {

    @Test
    public void settleNextLoopConstants_menuOpen_notBareAbysstargetForge() {
        assertEquals("/ember_p1_abyss_settle", EmberAbyssSettle.SETTLE_NEXT_LOOP_CMD);
        assertEquals("[下一环]", EmberAbyssSettle.SETTLE_NEXT_LOOP_LABEL);
        assertEquals("/ember_p1_adventure", EmberAbyssSettle.SETTLE_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberAbyssSettle.SETTLE_FORGE_CMD);
        assertFalse(EmberAbyssSettle.SETTLE_NEXT_LOOP_CMD.contains("abysstarget"));
        assertFalse(EmberAbyssSettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1"));
        assertFalse(EmberAbyssSettle.SETTLE_FORGE_CMD.contains("/corerpg p1 forge"));
    }

    @Test
    public void settleServiceNewBest_opensThinPage_forgeMenu_noTeachAbysstarget() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSettleService.java")),
                StandardCharsets.UTF_8);
        int a = src.indexOf("// D675");
        assertTrue(a > 0);
        String body = src.substring(a, a + 900);
        assertTrue(body.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(body.contains("SETTLE_FORGE_CMD") || body.contains("/ember_p1_forge"));
        assertFalse(body.contains("\"/corerpg p1 forge\""));
        assertFalse(body.contains("可设深渊层目标：/corerpg p1 abysstarget"));
        assertTrue(src.contains("EmberAbyssSettle.remember"));
    }

    @Test
    public void abyssTargetSource_menuFirst_noTeachHandTypeAbysstarget() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberAbyssTarget.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("未设 · /corerpg p1 abysstarget"));
        assertTrue(src.contains("未设 · 点下一环设目标"));
        assertFalse(src.contains("可改 /corerpg p1 abysstarget"));
        assertFalse(src.contains("下一目标可设：/corerpg p1 abysstarget"));
        assertFalse(src.contains("\"/corerpg p1 abysstarget\""));
        assertFalse(src.contains("\"/corerpg p1 abysstarget "));
        assertFalse(src.contains("\"/corerpg p1 forge\""));
        assertTrue(src.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(src.contains("void afterBest("));
    }

    @Test
    public void dropFeeFloorConstantsUntouched_inAbyssSettle() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberAbyssSettle.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("fee ="));
        assertFalse(src.contains("DAILY_CAP"));
        assertFalse(src.contains("lootFamily"));
        assertTrue(src.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(src.contains("C_LAST"));
    }

    @Test
    public void economyBvUntouched() {
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }
}
