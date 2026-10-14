package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DH D13339 W1a: ShortPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 shortpath/enter 主 UI / 零改日表掉落体力价 resolve.
 */
public class EmberShortPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareShortpathOrEnter() {
        assertEquals("/ember_p1_short_path", EmberShortPath.PATH_PICK_CMD);
        assertEquals("[路径追]", EmberShortPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberShortPath.PATH_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberShortPath.PATH_FORGE_CMD);
        assertEquals("/ember_p1_short", EmberShortPath.PATH_SHORT_CMD);
        assertEquals(EmberShortRules.SETTLE_PATH_CMD, EmberShortPath.PATH_PICK_CMD);
        assertFalse(EmberShortPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberShortPath.PATH_PICK_CMD.contains("shortpath"));
        assertFalse(EmberShortPath.PATH_PICK_CMD.contains("short_settle"));
        assertFalse(EmberShortPath.PATH_FORGE_CMD.contains("/corerpg p1 forge"));
    }

    @Test
    public void glance_noTeachBareShortpath() {
        String locked = EmberShortPath.glance(EmberShortPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 shortpath"));
        String none = EmberShortPath.glance(EmberShortPath.NONE, true);
        assertTrue(none.contains("点路径追"));
        assertFalse(none.contains("/corerpg p1 shortpath"));
        String fc = EmberShortPath.glance(EmberShortPath.FC, true);
        assertTrue(fc.contains("首通追"));
        assertFalse(fc.contains("/corerpg p1 shortpath"));
    }

    @Test
    public void menuLine_labelTipReuse() {
        assertEquals("短征路径 · 未选", EmberShortPath.menuLine(EmberShortPath.NONE));
        assertEquals("短征路径 · 短征·首通追", EmberShortPath.menuLine(EmberShortPath.FC));
        assertEquals("短征路径 · 短征·有奖追", EmberShortPath.menuLine(EmberShortPath.DAY));
        assertEquals("短征路径 · 短征·后段追", EmberShortPath.menuLine(EmberShortPath.LATE));
        assertEquals("短征·首通追", EmberShortPath.label(EmberShortPath.FC));
        assertEquals("未选", EmberShortPath.label(EmberShortPath.NONE));
        assertTrue(EmberShortPath.tip(EmberShortPath.FC).contains("首通"));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareShortpathOrEnterMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShortPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_short_path"));
        assertTrue(src.contains("TIP-DH"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 shortpath"));
        assertFalse(offerBody.contains("shortpath fc"));
        assertFalse(offerBody.contains("ember_p1_short_settle"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_SHORT_CMD") || applyBody.contains("PATH_FORGE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 shortpath"));
        assertFalse(applyBody.contains("\"/corerpg p1 enter "));
        assertFalse(applyBody.contains("ember_p1_short_settle"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShortPath.java")),
                StandardCharsets.UTF_8);
        int weekly = src.indexOf("public static boolean maybeOfferWeekly");
        assertTrue(weekly > 0);
        String weeklyBody = src.substring(weekly, src.indexOf("public static void scheduleOfferAfterQ01", weekly));
        assertTrue(weeklyBody.contains("offerPick(p)"));
        int sched = src.indexOf("public static void scheduleOfferAfterQ01");
        assertTrue(sched > 0);
        String schedBody = src.substring(sched, src.indexOf("public static void applyAndReply", sched));
        assertTrue(schedBody.contains("maybeOfferWeekly"));
    }

    @Test
    public void dropStaminaResolveUntouched_inShortPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShortPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("dailyCap ="));
        assertTrue(src.contains("public static EmberRunMaps.MapDef resolve("));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DH"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }
}
