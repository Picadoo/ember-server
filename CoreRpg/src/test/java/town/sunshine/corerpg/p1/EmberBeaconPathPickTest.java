package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EW D13380 W1a: BeaconPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 beaconpath 主 UI / 零改伤害CD掉落体力价/D567 护灯语义.
 */
public class EmberBeaconPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareBeaconpath() {
        assertEquals("/ember_p1_beacon_path", EmberBeaconPath.PATH_PICK_CMD);
        assertEquals("[护灯路径]", EmberBeaconPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberBeaconPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberBeaconPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberBeaconPath.PATH_PICK_CMD.contains("beaconpath"));
        assertFalse(EmberBeaconPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareBeaconpath() {
        String locked = EmberBeaconPath.glance(EmberBeaconPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 beaconpath"));
        String none = EmberBeaconPath.glance(EmberBeaconPath.NONE, true);
        assertTrue(none.contains("点护灯路径页"));
        assertFalse(none.contains("/corerpg p1 beaconpath"));
        String auto = EmberBeaconPath.glance(EmberBeaconPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 beaconpath"));
        String ask = EmberBeaconPath.glance(EmberBeaconPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 beaconpath"));
        String mute = EmberBeaconPath.glance(EmberBeaconPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 beaconpath"));
    }

    @Test
    public void menuLine_labelTipReuse_beaconSemanticsUnchanged() {
        assertEquals("护灯路径 · 未选", EmberBeaconPath.menuLine(EmberBeaconPath.NONE));
        assertEquals("护灯路径 · 护灯·自动", EmberBeaconPath.menuLine(EmberBeaconPath.AUTO));
        assertEquals("护灯路径 · 护灯·提醒", EmberBeaconPath.menuLine(EmberBeaconPath.ASK));
        assertEquals("护灯路径 · 护灯·静默", EmberBeaconPath.menuLine(EmberBeaconPath.MUTE));
        assertEquals("护灯·自动", EmberBeaconPath.label(EmberBeaconPath.AUTO));
        assertEquals("护灯·提醒", EmberBeaconPath.label(EmberBeaconPath.ASK));
        assertEquals("护灯·静默", EmberBeaconPath.label(EmberBeaconPath.MUTE));
        assertEquals("未选", EmberBeaconPath.label(EmberBeaconPath.NONE));
        assertTrue(EmberBeaconPath.tip(EmberBeaconPath.AUTO).contains("灯旁") || EmberBeaconPath.tip(EmberBeaconPath.AUTO).contains("自动"));
        assertTrue(EmberBeaconPath.tip(EmberBeaconPath.ASK).contains("提醒"));
        assertTrue(EmberBeaconPath.tip(EmberBeaconPath.MUTE).contains("灯柱") || EmberBeaconPath.tip(EmberBeaconPath.MUTE).contains("静默"));
        assertEquals(EmberBeaconPath.AUTO, EmberBeaconPath.parse("auto"));
        assertEquals(EmberBeaconPath.ASK, EmberBeaconPath.parse("ask"));
        assertEquals(EmberBeaconPath.MUTE, EmberBeaconPath.parse("mute"));
        assertEquals(EmberBeaconPath.NONE, EmberBeaconPath.parse("clear"));
        assertTrue(EmberBeaconPath.shouldAuto(EmberBeaconPath.AUTO));
        assertTrue(EmberBeaconPath.shouldMute(EmberBeaconPath.MUTE));
        assertFalse(EmberBeaconPath.shouldAuto(EmberBeaconPath.ASK));
        assertFalse(EmberBeaconPath.shouldMute(EmberBeaconPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareBeaconpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBeaconPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_beacon_path"));
        assertTrue(src.contains("TIP-EW"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 beaconpath"));
        assertFalse(offerBody.contains("beaconpath auto"));
        assertFalse(offerBody.contains("beaconpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 beaconpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBeaconPath.java")),
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
    public void combatButtons_beaconUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBeaconPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[护灯]\", \"/corerpg p1 beacon\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 beaconpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("maybeAfterBeacon"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inBeaconPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBeaconPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EW"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareBeaconpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean beaconPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean beaconGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点护灯路径页"));
        assertFalse(body.contains("用法：/corerpg p1 beaconpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点护灯路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 beaconpath"));
    }
}
