package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DD D13335 W1a: 精选结算「下一环」菜单化 — 开薄页 / 清裸指令 / 零改掉落体力价周帽.
 */
public class EmberFeaturedSettleNextLoopTest {

    @Test
    public void settleNextLoopConstants_menuOpen_notBareEnterForge() {
        assertEquals("/ember_p1_featured_settle", EmberFeaturedSettle.SETTLE_NEXT_LOOP_CMD);
        assertEquals("[下一环]", EmberFeaturedSettle.SETTLE_NEXT_LOOP_LABEL);
        assertEquals("/ember_p1_adventure", EmberFeaturedSettle.SETTLE_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberFeaturedSettle.SETTLE_FORGE_CMD);
        assertFalse(EmberFeaturedSettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1 enter"));
        assertFalse(EmberFeaturedSettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1"));
        assertFalse(EmberFeaturedSettle.SETTLE_FORGE_CMD.contains("/corerpg p1 forge"));
        assertFalse(EmberFeaturedSettle.SETTLE_NEXT_LOOP_CMD.contains("mapfarm"));
        assertFalse(EmberFeaturedSettle.SETTLE_NEXT_LOOP_CMD.contains("featurepath"));
    }

    @Test
    public void afterSettleSource_opensThinPage_noTeachBareEnterForgeMapfarm() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFeaturedPath.java")),
                StandardCharsets.UTF_8);
        int a = src.indexOf("void afterSettle(Player p, PlayerData d, boolean featured");
        assertTrue(a > 0);
        // take from first afterSettle through end of class methods around afterSettle overload
        String body = src.substring(a);
        assertTrue(body.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(body.contains("EmberFeaturedSettle.remember"));
        // afterSettle body must not teach bare enter/forge/mapfarm as ConfirmTokens
        int end = body.indexOf("private static String featuredLabelLive");
        assertTrue(end > 0);
        String afterBody = body.substring(0, end);
        assertFalse(afterBody.contains("\"/corerpg p1 enter "));
        assertFalse(afterBody.contains("\"/corerpg p1 forge\""));
        assertFalse(afterBody.contains("\"/corerpg p1 mapfarm\""));
        assertFalse(afterBody.contains("\"/corerpg p1 featurepath\""));
        assertTrue(afterBody.contains("可设精选路径 · 点下一环选路径"));
        assertFalse(afterBody.contains("可设精选路径：/corerpg p1 featurepath"));
    }

    @Test
    public void settleServiceD794_opensThinPage_noTeachBareFeaturepathChallengepath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSettleService.java")),
                StandardCharsets.UTF_8);
        int a = src.indexOf("// D794");
        assertTrue(a > 0);
        String body = src.substring(a, a + 900);
        assertTrue(body.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(body.contains("SETTLE_ADVENTURE_CMD") || body.contains("/ember_p1_adventure"));
        assertTrue(body.contains("SETTLE_FORGE_CMD") || body.contains("/ember_p1_forge"));
        assertFalse(body.contains("\"/corerpg p1 featurepath\""));
        assertFalse(body.contains("\"/corerpg p1 challengepath\""));
        assertTrue(src.contains("EmberFeaturedSettle.remember"));
        assertTrue(src.contains("weekCapFull"));
    }

    @Test
    public void dropStaminaCapConstantsUntouched_inFeaturedSettle() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFeaturedSettle.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("fee ="));
        assertFalse(src.contains("rotationWeeklyCap"));
        assertFalse(src.contains("lootFamily ="));
        assertTrue(src.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(src.contains("C_LAST"));
        assertTrue(src.contains("C_PATH"));
        assertTrue(src.contains("C_CAP"));
    }

    @Test
    public void economyBvUntouched() {
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void otherSettlePagesUntouched_notFeatured() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFeaturedSettle.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("ember_p1_abyss_settle"));
        assertFalse(src.contains("ember_p1_challenge_settle"));
        assertFalse(src.contains("ember_p1_raid_settle"));
        assertFalse(src.contains("ember_p1_short_settle"));
        assertFalse(src.contains("ember_p1_identity_settle"));
        assertTrue(src.contains("ember_p1_featured_settle"));
    }
}
