package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EX D13381 W1a: HoldPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 holdpath 主 UI / 零改伤害CD掉落体力价/D566 占点语义.
 */
public class EmberHoldPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareHoldpath() {
        assertEquals("/ember_p1_hold_path", EmberHoldPath.PATH_PICK_CMD);
        assertEquals("[占点路径]", EmberHoldPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberHoldPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberHoldPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberHoldPath.PATH_PICK_CMD.contains("holdpath"));
        assertFalse(EmberHoldPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareHoldpath() {
        String locked = EmberHoldPath.glance(EmberHoldPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 holdpath"));
        String none = EmberHoldPath.glance(EmberHoldPath.NONE, true);
        assertTrue(none.contains("点占点路径页"));
        assertFalse(none.contains("/corerpg p1 holdpath"));
        String auto = EmberHoldPath.glance(EmberHoldPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 holdpath"));
        String ask = EmberHoldPath.glance(EmberHoldPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 holdpath"));
        String mute = EmberHoldPath.glance(EmberHoldPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 holdpath"));
    }

    @Test
    public void menuLine_labelTipReuse_holdSemanticsUnchanged() {
        assertEquals("占点路径 · 未选", EmberHoldPath.menuLine(EmberHoldPath.NONE));
        assertEquals("占点路径 · 占点·自动", EmberHoldPath.menuLine(EmberHoldPath.AUTO));
        assertEquals("占点路径 · 占点·提醒", EmberHoldPath.menuLine(EmberHoldPath.ASK));
        assertEquals("占点路径 · 占点·静默", EmberHoldPath.menuLine(EmberHoldPath.MUTE));
        assertEquals("占点·自动", EmberHoldPath.label(EmberHoldPath.AUTO));
        assertEquals("占点·提醒", EmberHoldPath.label(EmberHoldPath.ASK));
        assertEquals("占点·静默", EmberHoldPath.label(EmberHoldPath.MUTE));
        assertEquals("未选", EmberHoldPath.label(EmberHoldPath.NONE));
        assertTrue(EmberHoldPath.tip(EmberHoldPath.AUTO).contains("圈") || EmberHoldPath.tip(EmberHoldPath.AUTO).contains("自动"));
        assertTrue(EmberHoldPath.tip(EmberHoldPath.ASK).contains("提醒"));
        assertTrue(EmberHoldPath.tip(EmberHoldPath.MUTE).contains("圈") || EmberHoldPath.tip(EmberHoldPath.MUTE).contains("静默"));
        assertEquals(EmberHoldPath.AUTO, EmberHoldPath.parse("auto"));
        assertEquals(EmberHoldPath.ASK, EmberHoldPath.parse("ask"));
        assertEquals(EmberHoldPath.MUTE, EmberHoldPath.parse("mute"));
        assertEquals(EmberHoldPath.NONE, EmberHoldPath.parse("clear"));
        assertTrue(EmberHoldPath.shouldAuto(EmberHoldPath.AUTO));
        assertTrue(EmberHoldPath.shouldMute(EmberHoldPath.MUTE));
        assertFalse(EmberHoldPath.shouldAuto(EmberHoldPath.ASK));
        assertFalse(EmberHoldPath.shouldMute(EmberHoldPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareHoldpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberHoldPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_hold_path"));
        assertTrue(src.contains("TIP-EX"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 holdpath"));
        assertFalse(offerBody.contains("holdpath auto"));
        assertFalse(offerBody.contains("holdpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 holdpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberHoldPath.java")),
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
    public void combatButtons_holdUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberHoldPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[占点]\", \"/corerpg p1 hold\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 holdpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("maybeAfterHold"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inHoldPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberHoldPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EX"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareHoldpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean holdPathCmd");
        assertTrue(block > 0);
        // next private method after holdPathCmd
        int end = src.indexOf("private boolean holdGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点占点路径页"));
        assertFalse(body.contains("用法：/corerpg p1 holdpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点占点路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 holdpath"));
    }
}
