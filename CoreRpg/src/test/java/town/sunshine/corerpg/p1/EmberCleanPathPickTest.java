package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DX D13355 W1a: CleanPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 cleanpath 主 UI / 零改伤害CD掉落体力价/D578 净伤语义.
 */
public class EmberCleanPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareCleanpath() {
        assertEquals("/ember_p1_clean_path", EmberCleanPath.PATH_PICK_CMD);
        assertEquals("[无伤路径]", EmberCleanPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberCleanPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberCleanPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberCleanPath.PATH_PICK_CMD.contains("cleanpath"));
        assertFalse(EmberCleanPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareCleanpath() {
        String locked = EmberCleanPath.glance(EmberCleanPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 cleanpath"));
        String none = EmberCleanPath.glance(EmberCleanPath.NONE, true);
        assertTrue(none.contains("点无伤路径页"));
        assertFalse(none.contains("/corerpg p1 cleanpath"));
        String auto = EmberCleanPath.glance(EmberCleanPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 cleanpath"));
        String ask = EmberCleanPath.glance(EmberCleanPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 cleanpath"));
        String mute = EmberCleanPath.glance(EmberCleanPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 cleanpath"));
    }

    @Test
    public void menuLine_labelTipReuse_cleanSemanticsUnchanged() {
        assertEquals("无伤路径 · 未选", EmberCleanPath.menuLine(EmberCleanPath.NONE));
        assertEquals("无伤路径 · 无伤·自动", EmberCleanPath.menuLine(EmberCleanPath.AUTO));
        assertEquals("无伤路径 · 无伤·提醒", EmberCleanPath.menuLine(EmberCleanPath.ASK));
        assertEquals("无伤路径 · 无伤·静默", EmberCleanPath.menuLine(EmberCleanPath.MUTE));
        assertEquals("无伤·自动", EmberCleanPath.label(EmberCleanPath.AUTO));
        assertEquals("无伤·提醒", EmberCleanPath.label(EmberCleanPath.ASK));
        assertEquals("无伤·静默", EmberCleanPath.label(EmberCleanPath.MUTE));
        assertEquals("未选", EmberCleanPath.label(EmberCleanPath.NONE));
        assertTrue(EmberCleanPath.tip(EmberCleanPath.AUTO).contains("自动"));
        assertTrue(EmberCleanPath.tip(EmberCleanPath.ASK).contains("提醒"));
        assertTrue(EmberCleanPath.tip(EmberCleanPath.MUTE).contains("静默") || EmberCleanPath.tip(EmberCleanPath.MUTE).contains("提醒"));
        assertEquals(EmberCleanPath.AUTO, EmberCleanPath.parse("auto"));
        assertEquals(EmberCleanPath.ASK, EmberCleanPath.parse("ask"));
        assertEquals(EmberCleanPath.MUTE, EmberCleanPath.parse("mute"));
        assertEquals(EmberCleanPath.NONE, EmberCleanPath.parse("clear"));
        assertTrue(EmberCleanPath.shouldAuto(EmberCleanPath.AUTO));
        assertTrue(EmberCleanPath.shouldMute(EmberCleanPath.MUTE));
        assertFalse(EmberCleanPath.shouldAuto(EmberCleanPath.ASK));
        assertFalse(EmberCleanPath.shouldMute(EmberCleanPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareCleanpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCleanPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_clean_path"));
        assertTrue(src.contains("TIP-DX"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 cleanpath"));
        assertFalse(offerBody.contains("cleanpath auto"));
        assertFalse(offerBody.contains("cleanpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 cleanpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D578 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCleanPath.java")),
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
    public void combatButtons_cleanUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCleanPath.java")),
                StandardCharsets.UTF_8);
        // 局内「无伤」战斗钮语义零改
        assertTrue(src.contains("\"[无伤]\", \"/corerpg p1 clean\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 cleanpath\""));
        // auto/ask/mute 真改净伤语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathCleanSpace"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inCleanPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCleanPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DX"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareCleanpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean cleanPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean cleanGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点无伤路径页"));
        assertFalse(body.contains("用法：/corerpg p1 cleanpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点无伤路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 cleanpath"));
    }
}
