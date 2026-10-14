package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EH D13365 W1a: ShiftPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 shiftpath 主 UI / 零改伤害CD掉落体力价/D588 侧安语义.
 */
public class EmberShiftPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareShiftpath() {
        assertEquals("/ember_p1_shift_path", EmberShiftPath.PATH_PICK_CMD);
        assertEquals("[侧安路径]", EmberShiftPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberShiftPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberShiftPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberShiftPath.PATH_PICK_CMD.contains("shiftpath"));
        assertFalse(EmberShiftPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareShiftpath() {
        String locked = EmberShiftPath.glance(EmberShiftPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 shiftpath"));
        String none = EmberShiftPath.glance(EmberShiftPath.NONE, true);
        assertTrue(none.contains("点侧安路径页"));
        assertFalse(none.contains("/corerpg p1 shiftpath"));
        String auto = EmberShiftPath.glance(EmberShiftPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 shiftpath"));
        String ask = EmberShiftPath.glance(EmberShiftPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 shiftpath"));
        String mute = EmberShiftPath.glance(EmberShiftPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 shiftpath"));
    }

    @Test
    public void menuLine_labelTipReuse_shiftSemanticsUnchanged() {
        assertEquals("侧安路径 · 未选", EmberShiftPath.menuLine(EmberShiftPath.NONE));
        assertEquals("侧安路径 · 侧安·自动", EmberShiftPath.menuLine(EmberShiftPath.AUTO));
        assertEquals("侧安路径 · 侧安·提醒", EmberShiftPath.menuLine(EmberShiftPath.ASK));
        assertEquals("侧安路径 · 侧安·静默", EmberShiftPath.menuLine(EmberShiftPath.MUTE));
        assertEquals("侧安·自动", EmberShiftPath.label(EmberShiftPath.AUTO));
        assertEquals("侧安·提醒", EmberShiftPath.label(EmberShiftPath.ASK));
        assertEquals("侧安·静默", EmberShiftPath.label(EmberShiftPath.MUTE));
        assertEquals("未选", EmberShiftPath.label(EmberShiftPath.NONE));
        assertTrue(EmberShiftPath.tip(EmberShiftPath.AUTO).contains("自动") || EmberShiftPath.tip(EmberShiftPath.AUTO).contains("左侧"));
        assertTrue(EmberShiftPath.tip(EmberShiftPath.ASK).contains("提醒"));
        assertTrue(EmberShiftPath.tip(EmberShiftPath.MUTE).contains("静默") || EmberShiftPath.tip(EmberShiftPath.MUTE).contains("左侧"));
        assertEquals(EmberShiftPath.AUTO, EmberShiftPath.parse("auto"));
        assertEquals(EmberShiftPath.ASK, EmberShiftPath.parse("ask"));
        assertEquals(EmberShiftPath.MUTE, EmberShiftPath.parse("mute"));
        assertEquals(EmberShiftPath.NONE, EmberShiftPath.parse("clear"));
        assertTrue(EmberShiftPath.shouldAuto(EmberShiftPath.AUTO));
        assertTrue(EmberShiftPath.shouldMute(EmberShiftPath.MUTE));
        assertFalse(EmberShiftPath.shouldAuto(EmberShiftPath.ASK));
        assertFalse(EmberShiftPath.shouldMute(EmberShiftPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareShiftpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShiftPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_shift_path"));
        assertTrue(src.contains("TIP-EH"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 shiftpath"));
        assertFalse(offerBody.contains("shiftpath auto"));
        assertFalse(offerBody.contains("shiftpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 shiftpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShiftPath.java")),
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
    public void combatButtons_shiftUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShiftPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[左侧]\", \"/corerpg p1 shift\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 shiftpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathShiftSafe"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inShiftPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShiftPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EH"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareShiftpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean shiftPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean shiftGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点侧安路径页"));
        assertFalse(body.contains("用法：/corerpg p1 shiftpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点侧安路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 shiftpath"));
    }
}
