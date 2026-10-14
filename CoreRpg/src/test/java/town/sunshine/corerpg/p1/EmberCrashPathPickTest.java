package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EJ D13367 W1a: CrashPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 crashpath 主 UI / 零改伤害CD掉落体力价/D590 撞墙语义.
 */
public class EmberCrashPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareCrashpath() {
        assertEquals("/ember_p1_crash_path", EmberCrashPath.PATH_PICK_CMD);
        assertEquals("[撞墙路径]", EmberCrashPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberCrashPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberCrashPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberCrashPath.PATH_PICK_CMD.contains("crashpath"));
        assertFalse(EmberCrashPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareCrashpath() {
        String locked = EmberCrashPath.glance(EmberCrashPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 crashpath"));
        String none = EmberCrashPath.glance(EmberCrashPath.NONE, true);
        assertTrue(none.contains("点撞墙路径页"));
        assertFalse(none.contains("/corerpg p1 crashpath"));
        String auto = EmberCrashPath.glance(EmberCrashPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 crashpath"));
        String ask = EmberCrashPath.glance(EmberCrashPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 crashpath"));
        String mute = EmberCrashPath.glance(EmberCrashPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 crashpath"));
    }

    @Test
    public void menuLine_labelTipReuse_crashSemanticsUnchanged() {
        assertEquals("撞墙路径 · 未选", EmberCrashPath.menuLine(EmberCrashPath.NONE));
        assertEquals("撞墙路径 · 撞墙·自动", EmberCrashPath.menuLine(EmberCrashPath.AUTO));
        assertEquals("撞墙路径 · 撞墙·提醒", EmberCrashPath.menuLine(EmberCrashPath.ASK));
        assertEquals("撞墙路径 · 撞墙·静默", EmberCrashPath.menuLine(EmberCrashPath.MUTE));
        assertEquals("撞墙·自动", EmberCrashPath.label(EmberCrashPath.AUTO));
        assertEquals("撞墙·提醒", EmberCrashPath.label(EmberCrashPath.ASK));
        assertEquals("撞墙·静默", EmberCrashPath.label(EmberCrashPath.MUTE));
        assertEquals("未选", EmberCrashPath.label(EmberCrashPath.NONE));
        assertTrue(EmberCrashPath.tip(EmberCrashPath.AUTO).contains("自动") || EmberCrashPath.tip(EmberCrashPath.AUTO).contains("首领旁"));
        assertTrue(EmberCrashPath.tip(EmberCrashPath.ASK).contains("提醒"));
        assertTrue(EmberCrashPath.tip(EmberCrashPath.MUTE).contains("静默") || EmberCrashPath.tip(EmberCrashPath.MUTE).contains("撞墙"));
        assertEquals(EmberCrashPath.AUTO, EmberCrashPath.parse("auto"));
        assertEquals(EmberCrashPath.ASK, EmberCrashPath.parse("ask"));
        assertEquals(EmberCrashPath.MUTE, EmberCrashPath.parse("mute"));
        assertEquals(EmberCrashPath.NONE, EmberCrashPath.parse("clear"));
        assertTrue(EmberCrashPath.shouldAuto(EmberCrashPath.AUTO));
        assertTrue(EmberCrashPath.shouldMute(EmberCrashPath.MUTE));
        assertFalse(EmberCrashPath.shouldAuto(EmberCrashPath.ASK));
        assertFalse(EmberCrashPath.shouldMute(EmberCrashPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareCrashpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCrashPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_crash_path"));
        assertTrue(src.contains("TIP-EJ"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 crashpath"));
        assertFalse(offerBody.contains("crashpath auto"));
        assertFalse(offerBody.contains("crashpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 crashpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCrashPath.java")),
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
    public void combatButtons_crashUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCrashPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[备破]\", \"/corerpg p1 crash\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 crashpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathCrashReady"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inCrashPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCrashPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EJ"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareCrashpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean crashPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean crashGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点撞墙路径页"));
        assertFalse(body.contains("用法：/corerpg p1 crashpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点撞墙路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 crashpath"));
    }
}
