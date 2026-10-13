package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-CW D13328 W1a: settle「下一环」菜单化 — 开薄页 / 清裸指令 / spendChase→forge 菜单.
 */
public class EmberShortSettleNextLoopTest {

    @Test
    public void settleNextLoopConstants_menuOpen_notBareShortpath() {
        assertEquals("/ember_p1_short_settle", EmberShortRules.SETTLE_NEXT_LOOP_CMD);
        assertEquals("[下一环]", EmberShortRules.SETTLE_NEXT_LOOP_LABEL);
        assertEquals("/ember_p1_short_path", EmberShortRules.SETTLE_PATH_CMD);
        assertEquals("/ember_p1_forge", EmberShortRules.SETTLE_FORGE_CMD);
        assertEquals("/ember_p1_adventure", EmberShortRules.SETTLE_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberShortRules.spendChaseButtonCmd());
        assertFalse(EmberShortRules.spendChaseButtonCmd().contains("corerpg p1 forge"));
        assertFalse(EmberShortRules.FC_CLEARED_HINT.contains("/corerpg p1 shortpath"));
        assertTrue(EmberShortRules.FC_CLEARED_HINT.contains("路径追"));
    }

    @Test
    public void settleSource_opensThinPage_noTeachShortpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShortService.java")),
                StandardCharsets.UTF_8);
        int a = src.indexOf("void settle(");
        assertTrue(a > 0);
        String body = src.substring(a, src.indexOf("String enteringLine(", a));
        assertTrue(body.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(body.contains("SETTLE_PATH_CMD"));
        assertTrue(body.contains("FC_CLEARED_HINT"));
        assertFalse(body.contains("/corerpg p1 shortpath"));
        assertFalse(body.contains("/corerpg p1 forge"));
        assertTrue(body.contains("spendChaseButtonCmd()"));
        // re-enter keep true enter action
        assertTrue(body.contains("/corerpg p1 enter "));
    }

    @Test
    public void shortPathSource_noTeachHandTypeShortpathDay() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShortPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("可改 /corerpg p1 shortpath day"));
        assertTrue(src.contains("FC_CLEARED_HINT") || src.contains("点路径追"));
        assertTrue(src.contains("SETTLE_PATH_CMD"));
        // glance: no bare shortpath teach
        assertFalse(src.contains("未选 · /corerpg p1 shortpath"));
        assertTrue(src.contains("未选 · 点路径追"));
    }

    @Test
    public void todayRotSettleCue_keepsEnterAlias_noTeachShortpath() {
        assertTrue(EmberTodayRotFeel.REENTER_CMD.contains("enter short-today"));
        assertFalse(EmberTodayRotFeel.REENTER_CMD.contains("shortpath"));
        assertFalse(EmberTodayRotFeel.SETTLE_DONE.contains("shortpath"));
    }
}
