package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DC D13334 W1a: 图身份首通结算「下一环」菜单化 — 开薄页 / 清裸指令 / 零改掉落体力价首通包.
 */
public class EmberIdentitySettleNextLoopTest {

    @Test
    public void settleNextLoopConstants_menuOpen_notBareForge() {
        assertEquals("/ember_p1_identity_settle", EmberIdentitySettle.SETTLE_NEXT_LOOP_CMD);
        assertEquals("[下一环]", EmberIdentitySettle.SETTLE_NEXT_LOOP_LABEL);
        assertEquals("/ember_p1_adventure", EmberIdentitySettle.SETTLE_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberIdentitySettle.SETTLE_FORGE_CMD);
        assertEquals("/ember_p1_gear", EmberIdentitySettle.SETTLE_GEAR_CMD);
        assertFalse(EmberIdentitySettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1 forge"));
        assertFalse(EmberIdentitySettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1"));
        assertFalse(EmberIdentitySettle.SETTLE_FORGE_CMD.contains("/corerpg p1 forge"));
    }

    @Test
    public void settleServiceD673_opensThinPage_forgeMenu_noTeachBareForge() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSettleService.java")),
                StandardCharsets.UTF_8);
        int a = src.indexOf("// D673");
        assertTrue(a > 0);
        String body = src.substring(a, a + 900);
        assertTrue(body.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(body.contains("SETTLE_FORGE_CMD") || body.contains("/ember_p1_forge"));
        assertTrue(body.contains("SETTLE_GEAR_CMD") || body.contains("/ember_p1_gear"));
        assertFalse(body.contains("\"/corerpg p1 forge\""));
        assertTrue(src.contains("EmberIdentitySettle.remember"));
    }

    @Test
    public void dropStaminaFirstClearConstantsUntouched_inIdentitySettle() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberIdentitySettle.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("fee ="));
        assertFalse(src.contains("DAILY_CAP"));
        assertFalse(src.contains("lootFamily ="));
        assertFalse(src.contains("firstClear ="));
        assertTrue(src.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(src.contains("C_LAST"));
        assertTrue(src.contains("C_FAM"));
        assertTrue(src.contains("C_SLOT"));
    }

    @Test
    public void economyBvUntouched() {
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void abyssChallengeRaidShortSettleUntouched_notIdentity() throws Exception {
        // 零改 短征·团本·挑战·深渊 settle 同题：本窗只钉 D673 identity
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberIdentitySettle.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("ember_p1_abyss_settle"));
        assertFalse(src.contains("ember_p1_challenge_settle"));
        assertFalse(src.contains("ember_p1_raid_settle"));
        assertFalse(src.contains("ember_p1_short_settle"));
        assertTrue(src.contains("ember_p1_identity_settle"));
    }
}
