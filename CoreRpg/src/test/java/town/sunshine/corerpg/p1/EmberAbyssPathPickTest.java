package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DI D13340 W1a: AbyssPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 abysspath/abyss 主 UI / 零改层费强度掉落体力价.
 */
public class EmberAbyssPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareAbysspathOrSettle() {
        assertEquals("/ember_p1_abyss_path", EmberAbyssPath.PATH_PICK_CMD);
        assertEquals("[深渊路径]", EmberAbyssPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberAbyssPath.PATH_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberAbyssPath.PATH_FORGE_CMD);
        assertEquals("/ember_p1_abyss", EmberAbyssPath.PATH_ABYSS_CMD);
        assertFalse(EmberAbyssPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberAbyssPath.PATH_PICK_CMD.contains("abysspath"));
        assertFalse(EmberAbyssPath.PATH_PICK_CMD.contains("abyss_settle"));
        assertFalse(EmberAbyssPath.PATH_ABYSS_CMD.contains("/corerpg p1 abyss"));
        assertFalse(EmberAbyssPath.PATH_FORGE_CMD.contains("/corerpg p1 forge"));
    }

    @Test
    public void glance_noTeachBareAbysspath() {
        String locked = EmberAbyssPath.glance(EmberAbyssPath.NONE, false);
        assertTrue(locked.contains("Q07"));
        assertFalse(locked.contains("/corerpg p1 abysspath"));
        String none = EmberAbyssPath.glance(EmberAbyssPath.NONE, true);
        assertTrue(none.contains("点深渊路径页"));
        assertFalse(none.contains("/corerpg p1 abysspath"));
        String push = EmberAbyssPath.glance(EmberAbyssPath.PUSH, true);
        assertTrue(push.contains("冲层"));
        assertFalse(push.contains("/corerpg p1 abysspath"));
        String farm = EmberAbyssPath.glance(EmberAbyssPath.FARM, true);
        assertTrue(farm.contains("刷层"));
        assertFalse(farm.contains("/corerpg p1 abysspath"));
    }

    @Test
    public void menuLine_labelTipReuse() {
        assertEquals("深渊路径 · 未选", EmberAbyssPath.menuLine(EmberAbyssPath.NONE));
        assertEquals("深渊路径 · 冲层", EmberAbyssPath.menuLine(EmberAbyssPath.PUSH));
        assertEquals("深渊路径 · 刷层", EmberAbyssPath.menuLine(EmberAbyssPath.FARM));
        assertEquals("冲层", EmberAbyssPath.label(EmberAbyssPath.PUSH));
        assertEquals("刷层", EmberAbyssPath.label(EmberAbyssPath.FARM));
        assertEquals("未选", EmberAbyssPath.label(EmberAbyssPath.NONE));
        assertTrue(EmberAbyssPath.tip(EmberAbyssPath.PUSH).contains("最高可开"));
        assertTrue(EmberAbyssPath.tip(EmberAbyssPath.FARM).contains("成色"));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareAbysspathOrAbyssMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberAbyssPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_abyss_path"));
        assertTrue(src.contains("TIP-DI"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 abysspath"));
        assertFalse(offerBody.contains("abysspath push"));
        assertFalse(offerBody.contains("ember_p1_abyss_settle"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ABYSS_CMD") || applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 abysspath"));
        assertFalse(applyBody.contains("\"/corerpg p1 abyss\""));
        assertFalse(applyBody.contains("\"/corerpg p1 abyss "));
        assertFalse(applyBody.contains("ember_p1_abyss_settle"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberAbyssPath.java")),
                StandardCharsets.UTF_8);
        int weekly = src.indexOf("public static boolean maybeOfferWeekly");
        assertTrue(weekly > 0);
        String weeklyBody = src.substring(weekly, src.indexOf("public static void scheduleOfferAfterQ07", weekly));
        assertTrue(weeklyBody.contains("offerPick(p)"));
        int sched = src.indexOf("public static void scheduleOfferAfterQ07");
        assertTrue(sched > 0);
        String schedBody = src.substring(sched, src.indexOf("public static void applyAndReply", sched));
        assertTrue(schedBody.contains("maybeOfferWeekly"));
    }

    @Test
    public void feeStrengthDropStaminaUntouched_inAbyssPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberAbyssPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("fee ="));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DI"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }
}
