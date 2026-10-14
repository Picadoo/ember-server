package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DA D13332 W1a: 挑战结算「下一环」菜单化 — 开薄页 / 清裸指令 / 零改掉落体力价.
 */
public class EmberChallengeSettleNextLoopTest {

    @Test
    public void settleNextLoopConstants_menuOpen_notBareEnterForge() {
        assertEquals("/ember_p1_challenge_settle", EmberChallengeSettle.SETTLE_NEXT_LOOP_CMD);
        assertEquals("[下一环]", EmberChallengeSettle.SETTLE_NEXT_LOOP_LABEL);
        assertEquals("/ember_p1_adventure", EmberChallengeSettle.SETTLE_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberChallengeSettle.SETTLE_FORGE_CMD);
        assertFalse(EmberChallengeSettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1 enter"));
        assertFalse(EmberChallengeSettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1"));
        assertFalse(EmberChallengeSettle.SETTLE_FORGE_CMD.contains("/corerpg p1 forge"));
        assertFalse(EmberChallengeSettle.SETTLE_NEXT_LOOP_CMD.contains("mapfarm"));
    }

    @Test
    public void afterSettleSource_opensThinPage_noTeachBareEnterForgeMapfarm() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberChallengePath.java")),
                StandardCharsets.UTF_8);
        int a = src.indexOf("void afterSettle(");
        assertTrue(a > 0);
        String body = src.substring(a);
        assertTrue(body.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(body.contains("EmberChallengeSettle.remember"));
        assertFalse(body.contains("/corerpg p1 enter "));
        assertFalse(body.contains("/corerpg p1 forge"));
        assertFalse(body.contains("/corerpg p1 mapfarm"));
        assertFalse(body.contains("\"/corerpg p1 challengepath\""));
    }

    @Test
    public void challengePathSource_menuFirst_noTeachHandTypeChallengepath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberChallengePath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("未选 · /corerpg p1 challengepath"));
        assertTrue(src.contains("未选 · 点下一环选路径"));
        assertFalse(src.contains("可设硬本路径：/corerpg p1 challengepath"));
        assertTrue(src.contains("可设硬本路径 · 点下一环选路径"));
        assertFalse(src.contains("\"/corerpg p1 challengepath prefer\""));
        assertFalse(src.contains("\"/corerpg p1 challengepath easy\""));
        assertFalse(src.contains("\"/corerpg p1 challengepath follow\""));
        assertFalse(src.contains("\"/corerpg p1 challengepath clear\""));
        assertFalse(src.contains("\"/corerpg p1 forge\""));
        assertFalse(src.contains("\"/corerpg p1 mapfarm\""));
        assertTrue(src.contains("SETTLE_NEXT_LOOP_CMD"));
    }

    @Test
    public void settleServiceD671_forgeGoesMenu_notBareForge() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSettleService.java")),
                StandardCharsets.UTF_8);
        int a = src.indexOf("// D671");
        assertTrue(a > 0);
        String body = src.substring(a, a + 500);
        assertTrue(body.contains("SETTLE_NEXT_LOOP_CMD") || body.contains("SETTLE_FORGE_CMD"));
        assertFalse(body.contains("\"/corerpg p1 forge\""));
        assertTrue(body.contains("SETTLE_FORGE_CMD") || body.contains("/ember_p1_forge"));
    }

    @Test
    public void dropStaminaConstantsUntouched_inChallengePath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberChallengePath.java")),
                StandardCharsets.UTF_8);
        // 零改掉落/体力价：本文件无 stamina/loot 表常量改写
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("lootFamily ="));
        assertFalse(src.contains("DAILY_CAP"));
    }
}
