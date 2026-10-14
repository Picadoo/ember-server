package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DT D13351 W1a: SnapPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 snappath 主 UI / 零改伤害CD掉落体力价/D574 火链断链语义.
 */
public class EmberSnapPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareSnappath() {
        assertEquals("/ember_p1_snap_path", EmberSnapPath.PATH_PICK_CMD);
        assertEquals("[断链路径]", EmberSnapPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberSnapPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberSnapPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberSnapPath.PATH_PICK_CMD.contains("snappath"));
        assertFalse(EmberSnapPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareSnappath() {
        String locked = EmberSnapPath.glance(EmberSnapPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 snappath"));
        String none = EmberSnapPath.glance(EmberSnapPath.NONE, true);
        assertTrue(none.contains("点断链路径页"));
        assertFalse(none.contains("/corerpg p1 snappath"));
        String auto = EmberSnapPath.glance(EmberSnapPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 snappath"));
        String ask = EmberSnapPath.glance(EmberSnapPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 snappath"));
        String mute = EmberSnapPath.glance(EmberSnapPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 snappath"));
    }

    @Test
    public void menuLine_labelTipReuse_snapSemanticsUnchanged() {
        assertEquals("断链路径 · 未选", EmberSnapPath.menuLine(EmberSnapPath.NONE));
        assertEquals("断链路径 · 断链·自动", EmberSnapPath.menuLine(EmberSnapPath.AUTO));
        assertEquals("断链路径 · 断链·提醒", EmberSnapPath.menuLine(EmberSnapPath.ASK));
        assertEquals("断链路径 · 断链·静默", EmberSnapPath.menuLine(EmberSnapPath.MUTE));
        assertEquals("断链·自动", EmberSnapPath.label(EmberSnapPath.AUTO));
        assertEquals("断链·提醒", EmberSnapPath.label(EmberSnapPath.ASK));
        assertEquals("断链·静默", EmberSnapPath.label(EmberSnapPath.MUTE));
        assertEquals("未选", EmberSnapPath.label(EmberSnapPath.NONE));
        assertTrue(EmberSnapPath.tip(EmberSnapPath.AUTO).contains("自动"));
        assertTrue(EmberSnapPath.tip(EmberSnapPath.ASK).contains("提醒"));
        assertTrue(EmberSnapPath.tip(EmberSnapPath.MUTE).contains("静默") || EmberSnapPath.tip(EmberSnapPath.MUTE).contains("提醒"));
        assertEquals(EmberSnapPath.AUTO, EmberSnapPath.parse("auto"));
        assertEquals(EmberSnapPath.ASK, EmberSnapPath.parse("ask"));
        assertEquals(EmberSnapPath.MUTE, EmberSnapPath.parse("mute"));
        assertEquals(EmberSnapPath.NONE, EmberSnapPath.parse("clear"));
        assertTrue(EmberSnapPath.shouldAuto(EmberSnapPath.AUTO));
        assertTrue(EmberSnapPath.shouldMute(EmberSnapPath.MUTE));
        assertFalse(EmberSnapPath.shouldAuto(EmberSnapPath.ASK));
        assertFalse(EmberSnapPath.shouldMute(EmberSnapPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareSnappathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSnapPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_snap_path"));
        assertTrue(src.contains("TIP-DT"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 snappath"));
        assertFalse(offerBody.contains("snappath auto"));
        assertFalse(offerBody.contains("snappath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 snappath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D574 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSnapPath.java")),
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
    public void combatButtons_snapUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSnapPath.java")),
                StandardCharsets.UTF_8);
        // 局内「断链」战斗钮语义零改
        assertTrue(src.contains("\"[断链]\", \"/corerpg p1 snap\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 snappath\""));
        // auto/ask/mute 真改断链语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathSnapChain"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inSnapPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSnapPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DT"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareSnappath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean snapPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean snapGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点断链路径页"));
        assertFalse(body.contains("用法：/corerpg p1 snappath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点断链路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 snappath"));
    }
}
