package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DW D13354 W1a: StreakPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 streakpath 主 UI / 零改伤害CD掉落体力价/D577 连斩语义.
 */
public class EmberStreakPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareStreakpath() {
        assertEquals("/ember_p1_streak_path", EmberStreakPath.PATH_PICK_CMD);
        assertEquals("[连斩路径]", EmberStreakPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberStreakPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberStreakPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberStreakPath.PATH_PICK_CMD.contains("streakpath"));
        assertFalse(EmberStreakPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareStreakpath() {
        String locked = EmberStreakPath.glance(EmberStreakPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 streakpath"));
        String none = EmberStreakPath.glance(EmberStreakPath.NONE, true);
        assertTrue(none.contains("点连斩路径页"));
        assertFalse(none.contains("/corerpg p1 streakpath"));
        String auto = EmberStreakPath.glance(EmberStreakPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 streakpath"));
        String ask = EmberStreakPath.glance(EmberStreakPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 streakpath"));
        String mute = EmberStreakPath.glance(EmberStreakPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 streakpath"));
    }

    @Test
    public void menuLine_labelTipReuse_streakSemanticsUnchanged() {
        assertEquals("连斩路径 · 未选", EmberStreakPath.menuLine(EmberStreakPath.NONE));
        assertEquals("连斩路径 · 连斩·自动", EmberStreakPath.menuLine(EmberStreakPath.AUTO));
        assertEquals("连斩路径 · 连斩·提醒", EmberStreakPath.menuLine(EmberStreakPath.ASK));
        assertEquals("连斩路径 · 连斩·静默", EmberStreakPath.menuLine(EmberStreakPath.MUTE));
        assertEquals("连斩·自动", EmberStreakPath.label(EmberStreakPath.AUTO));
        assertEquals("连斩·提醒", EmberStreakPath.label(EmberStreakPath.ASK));
        assertEquals("连斩·静默", EmberStreakPath.label(EmberStreakPath.MUTE));
        assertEquals("未选", EmberStreakPath.label(EmberStreakPath.NONE));
        assertTrue(EmberStreakPath.tip(EmberStreakPath.AUTO).contains("自动"));
        assertTrue(EmberStreakPath.tip(EmberStreakPath.ASK).contains("提醒"));
        assertTrue(EmberStreakPath.tip(EmberStreakPath.MUTE).contains("静默") || EmberStreakPath.tip(EmberStreakPath.MUTE).contains("提醒"));
        assertEquals(EmberStreakPath.AUTO, EmberStreakPath.parse("auto"));
        assertEquals(EmberStreakPath.ASK, EmberStreakPath.parse("ask"));
        assertEquals(EmberStreakPath.MUTE, EmberStreakPath.parse("mute"));
        assertEquals(EmberStreakPath.NONE, EmberStreakPath.parse("clear"));
        assertTrue(EmberStreakPath.shouldAuto(EmberStreakPath.AUTO));
        assertTrue(EmberStreakPath.shouldMute(EmberStreakPath.MUTE));
        assertFalse(EmberStreakPath.shouldAuto(EmberStreakPath.ASK));
        assertFalse(EmberStreakPath.shouldMute(EmberStreakPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareStreakpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStreakPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_streak_path"));
        assertTrue(src.contains("TIP-DW"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 streakpath"));
        assertFalse(offerBody.contains("streakpath auto"));
        assertFalse(offerBody.contains("streakpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 streakpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D577 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStreakPath.java")),
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
    public void combatButtons_streakUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStreakPath.java")),
                StandardCharsets.UTF_8);
        // 局内「连斩」战斗钮语义零改
        assertTrue(src.contains("\"[连斩]\", \"/corerpg p1 streak\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 streakpath\""));
        // auto/ask/mute 真改连斩语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathStreakMob"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inStreakPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStreakPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DW"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareStreakpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean streakPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean streakGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点连斩路径页"));
        assertFalse(body.contains("用法：/corerpg p1 streakpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点连斩路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 streakpath"));
    }
}
