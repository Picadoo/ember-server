package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EY D13382 W1a: GuardPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 guardpath 主 UI / 零改伤害CD掉落体力价/D565 护兔语义.
 */
public class EmberGuardPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareGuardpath() {
        assertEquals("/ember_p1_guard_path", EmberGuardPath.PATH_PICK_CMD);
        assertEquals("[护兔路径]", EmberGuardPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberGuardPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberGuardPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberGuardPath.PATH_PICK_CMD.contains("guardpath"));
        assertFalse(EmberGuardPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareGuardpath() {
        String locked = EmberGuardPath.glance(EmberGuardPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 guardpath"));
        String none = EmberGuardPath.glance(EmberGuardPath.NONE, true);
        assertTrue(none.contains("点护兔路径页"));
        assertFalse(none.contains("/corerpg p1 guardpath"));
        String auto = EmberGuardPath.glance(EmberGuardPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 guardpath"));
        String ask = EmberGuardPath.glance(EmberGuardPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 guardpath"));
        String mute = EmberGuardPath.glance(EmberGuardPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 guardpath"));
    }

    @Test
    public void menuLine_labelTipReuse_guardSemanticsUnchanged() {
        assertEquals("护兔路径 · 未选", EmberGuardPath.menuLine(EmberGuardPath.NONE));
        assertEquals("护兔路径 · 护兔·自动", EmberGuardPath.menuLine(EmberGuardPath.AUTO));
        assertEquals("护兔路径 · 护兔·提醒", EmberGuardPath.menuLine(EmberGuardPath.ASK));
        assertEquals("护兔路径 · 护兔·静默", EmberGuardPath.menuLine(EmberGuardPath.MUTE));
        assertEquals("护兔·自动", EmberGuardPath.label(EmberGuardPath.AUTO));
        assertEquals("护兔·提醒", EmberGuardPath.label(EmberGuardPath.ASK));
        assertEquals("护兔·静默", EmberGuardPath.label(EmberGuardPath.MUTE));
        assertEquals("未选", EmberGuardPath.label(EmberGuardPath.NONE));
        assertTrue(EmberGuardPath.tip(EmberGuardPath.AUTO).contains("圈") || EmberGuardPath.tip(EmberGuardPath.AUTO).contains("自动"));
        assertTrue(EmberGuardPath.tip(EmberGuardPath.ASK).contains("提醒"));
        assertTrue(EmberGuardPath.tip(EmberGuardPath.MUTE).contains("小兔") || EmberGuardPath.tip(EmberGuardPath.MUTE).contains("静默"));
        assertEquals(EmberGuardPath.AUTO, EmberGuardPath.parse("auto"));
        assertEquals(EmberGuardPath.ASK, EmberGuardPath.parse("ask"));
        assertEquals(EmberGuardPath.MUTE, EmberGuardPath.parse("mute"));
        assertEquals(EmberGuardPath.NONE, EmberGuardPath.parse("clear"));
        assertTrue(EmberGuardPath.shouldAuto(EmberGuardPath.AUTO));
        assertTrue(EmberGuardPath.shouldMute(EmberGuardPath.MUTE));
        assertFalse(EmberGuardPath.shouldAuto(EmberGuardPath.ASK));
        assertFalse(EmberGuardPath.shouldMute(EmberGuardPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareGuardpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberGuardPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_guard_path"));
        assertTrue(src.contains("TIP-EY"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 guardpath"));
        assertFalse(offerBody.contains("guardpath auto"));
        assertFalse(offerBody.contains("guardpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 guardpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberGuardPath.java")),
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
    public void combatButtons_guardUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberGuardPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[护兔]\", \"/corerpg p1 guard\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 guardpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("maybeAfterEscort"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inGuardPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberGuardPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EY"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareGuardpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean guardPathCmd");
        assertTrue(block > 0);
        // next private method after guardPathCmd
        int end = src.indexOf("private boolean guardGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点护兔路径页"));
        assertFalse(body.contains("用法：/corerpg p1 guardpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点护兔路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 guardpath"));
    }
}
