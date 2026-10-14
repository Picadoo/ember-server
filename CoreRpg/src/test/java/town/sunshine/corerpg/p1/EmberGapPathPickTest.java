package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EG D13364 W1a: GapPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 gappath 主 UI / 零改伤害CD掉落体力价/D587 收招语义.
 */
public class EmberGapPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareGappath() {
        assertEquals("/ember_p1_gap_path", EmberGapPath.PATH_PICK_CMD);
        assertEquals("[收招路径]", EmberGapPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberGapPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberGapPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberGapPath.PATH_PICK_CMD.contains("gappath"));
        assertFalse(EmberGapPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareGappath() {
        String locked = EmberGapPath.glance(EmberGapPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 gappath"));
        String none = EmberGapPath.glance(EmberGapPath.NONE, true);
        assertTrue(none.contains("点收招路径页"));
        assertFalse(none.contains("/corerpg p1 gappath"));
        String auto = EmberGapPath.glance(EmberGapPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 gappath"));
        String ask = EmberGapPath.glance(EmberGapPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 gappath"));
        String mute = EmberGapPath.glance(EmberGapPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 gappath"));
    }

    @Test
    public void menuLine_labelTipReuse_gapSemanticsUnchanged() {
        assertEquals("收招路径 · 未选", EmberGapPath.menuLine(EmberGapPath.NONE));
        assertEquals("收招路径 · 收招·自动", EmberGapPath.menuLine(EmberGapPath.AUTO));
        assertEquals("收招路径 · 收招·提醒", EmberGapPath.menuLine(EmberGapPath.ASK));
        assertEquals("收招路径 · 收招·静默", EmberGapPath.menuLine(EmberGapPath.MUTE));
        assertEquals("收招·自动", EmberGapPath.label(EmberGapPath.AUTO));
        assertEquals("收招·提醒", EmberGapPath.label(EmberGapPath.ASK));
        assertEquals("收招·静默", EmberGapPath.label(EmberGapPath.MUTE));
        assertEquals("未选", EmberGapPath.label(EmberGapPath.NONE));
        assertTrue(EmberGapPath.tip(EmberGapPath.AUTO).contains("自动") || EmberGapPath.tip(EmberGapPath.AUTO).contains("收招"));
        assertTrue(EmberGapPath.tip(EmberGapPath.ASK).contains("提醒"));
        assertTrue(EmberGapPath.tip(EmberGapPath.MUTE).contains("静默") || EmberGapPath.tip(EmberGapPath.MUTE).contains("提醒"));
        assertEquals(EmberGapPath.AUTO, EmberGapPath.parse("auto"));
        assertEquals(EmberGapPath.ASK, EmberGapPath.parse("ask"));
        assertEquals(EmberGapPath.MUTE, EmberGapPath.parse("mute"));
        assertEquals(EmberGapPath.NONE, EmberGapPath.parse("clear"));
        assertTrue(EmberGapPath.shouldAuto(EmberGapPath.AUTO));
        assertTrue(EmberGapPath.shouldMute(EmberGapPath.MUTE));
        assertFalse(EmberGapPath.shouldAuto(EmberGapPath.ASK));
        assertFalse(EmberGapPath.shouldMute(EmberGapPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareGappathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberGapPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_gap_path"));
        assertTrue(src.contains("TIP-EG"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 gappath"));
        assertFalse(offerBody.contains("gappath auto"));
        assertFalse(offerBody.contains("gappath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 gappath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberGapPath.java")),
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
    public void combatButtons_gapUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberGapPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[抓空窗]\", \"/corerpg p1 gap\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 gappath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathGapBoss"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inGapPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberGapPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EG"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareGappath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean gapPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean gapGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点收招路径页"));
        assertFalse(body.contains("用法：/corerpg p1 gappath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点收招路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 gappath"));
    }
}
