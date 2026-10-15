package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-ET D13377 W1a: AlertPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 alertpath 主 UI / 零改伤害CD掉落体力价/D600 预警语义.
 */
public class EmberAlertPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareAlertpath() {
        assertEquals("/ember_p1_alert_path", EmberAlertPath.PATH_PICK_CMD);
        assertEquals("[预警路径]", EmberAlertPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberAlertPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberAlertPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberAlertPath.PATH_PICK_CMD.contains("alertpath"));
        assertFalse(EmberAlertPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareAlertpath() {
        String locked = EmberAlertPath.glance(EmberAlertPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 alertpath"));
        String none = EmberAlertPath.glance(EmberAlertPath.NONE, true);
        assertTrue(none.contains("点预警路径页"));
        assertFalse(none.contains("/corerpg p1 alertpath"));
        String auto = EmberAlertPath.glance(EmberAlertPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 alertpath"));
        String ask = EmberAlertPath.glance(EmberAlertPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 alertpath"));
        String mute = EmberAlertPath.glance(EmberAlertPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 alertpath"));
    }

    @Test
    public void menuLine_labelTipReuse_alertSemanticsUnchanged() {
        assertEquals("预警路径 · 未选", EmberAlertPath.menuLine(EmberAlertPath.NONE));
        assertEquals("预警路径 · 预警·自动", EmberAlertPath.menuLine(EmberAlertPath.AUTO));
        assertEquals("预警路径 · 预警·提醒", EmberAlertPath.menuLine(EmberAlertPath.ASK));
        assertEquals("预警路径 · 预警·静默", EmberAlertPath.menuLine(EmberAlertPath.MUTE));
        assertEquals("预警·自动", EmberAlertPath.label(EmberAlertPath.AUTO));
        assertEquals("预警·提醒", EmberAlertPath.label(EmberAlertPath.ASK));
        assertEquals("预警·静默", EmberAlertPath.label(EmberAlertPath.MUTE));
        assertEquals("未选", EmberAlertPath.label(EmberAlertPath.NONE));
        assertTrue(EmberAlertPath.tip(EmberAlertPath.AUTO).contains("祭坛") || EmberAlertPath.tip(EmberAlertPath.AUTO).contains("自动"));
        assertTrue(EmberAlertPath.tip(EmberAlertPath.ASK).contains("提醒"));
        assertTrue(EmberAlertPath.tip(EmberAlertPath.MUTE).contains("预警") || EmberAlertPath.tip(EmberAlertPath.MUTE).contains("预告"));
        assertEquals(EmberAlertPath.AUTO, EmberAlertPath.parse("auto"));
        assertEquals(EmberAlertPath.ASK, EmberAlertPath.parse("ask"));
        assertEquals(EmberAlertPath.MUTE, EmberAlertPath.parse("mute"));
        assertEquals(EmberAlertPath.NONE, EmberAlertPath.parse("clear"));
        assertTrue(EmberAlertPath.shouldAuto(EmberAlertPath.AUTO));
        assertTrue(EmberAlertPath.shouldMute(EmberAlertPath.MUTE));
        assertFalse(EmberAlertPath.shouldAuto(EmberAlertPath.ASK));
        assertFalse(EmberAlertPath.shouldMute(EmberAlertPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareAlertpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberAlertPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_alert_path"));
        assertTrue(src.contains("TIP-ET"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 alertpath"));
        assertFalse(offerBody.contains("alertpath auto"));
        assertFalse(offerBody.contains("alertpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 alertpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberAlertPath.java")),
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
    public void combatButtons_alertUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberAlertPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[就位]\", \"/corerpg p1 alert\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 alertpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("maybeAfterAlert"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inAlertPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberAlertPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-ET"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareAlertpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean alertPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean alertGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点预警路径页"));
        assertFalse(body.contains("用法：/corerpg p1 alertpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点预警路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 alertpath"));
    }
}
