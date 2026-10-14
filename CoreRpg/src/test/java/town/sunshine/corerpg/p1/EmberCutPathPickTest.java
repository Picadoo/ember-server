package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DZ D13357 W1a: CutPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 cutpath 主 UI / 零改伤害CD掉落体力价/D580 打断语义.
 */
public class EmberCutPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareCutpath() {
        assertEquals("/ember_p1_cut_path", EmberCutPath.PATH_PICK_CMD);
        assertEquals("[打断路径]", EmberCutPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberCutPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberCutPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberCutPath.PATH_PICK_CMD.contains("cutpath"));
        assertFalse(EmberCutPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareCutpath() {
        String locked = EmberCutPath.glance(EmberCutPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 cutpath"));
        String none = EmberCutPath.glance(EmberCutPath.NONE, true);
        assertTrue(none.contains("点打断路径页"));
        assertFalse(none.contains("/corerpg p1 cutpath"));
        String auto = EmberCutPath.glance(EmberCutPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 cutpath"));
        String ask = EmberCutPath.glance(EmberCutPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 cutpath"));
        String mute = EmberCutPath.glance(EmberCutPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 cutpath"));
    }

    @Test
    public void menuLine_labelTipReuse_cutSemanticsUnchanged() {
        assertEquals("打断路径 · 未选", EmberCutPath.menuLine(EmberCutPath.NONE));
        assertEquals("打断路径 · 打断·自动", EmberCutPath.menuLine(EmberCutPath.AUTO));
        assertEquals("打断路径 · 打断·提醒", EmberCutPath.menuLine(EmberCutPath.ASK));
        assertEquals("打断路径 · 打断·静默", EmberCutPath.menuLine(EmberCutPath.MUTE));
        assertEquals("打断·自动", EmberCutPath.label(EmberCutPath.AUTO));
        assertEquals("打断·提醒", EmberCutPath.label(EmberCutPath.ASK));
        assertEquals("打断·静默", EmberCutPath.label(EmberCutPath.MUTE));
        assertEquals("未选", EmberCutPath.label(EmberCutPath.NONE));
        assertTrue(EmberCutPath.tip(EmberCutPath.AUTO).contains("自动"));
        assertTrue(EmberCutPath.tip(EmberCutPath.ASK).contains("提醒"));
        assertTrue(EmberCutPath.tip(EmberCutPath.MUTE).contains("静默") || EmberCutPath.tip(EmberCutPath.MUTE).contains("提醒"));
        assertEquals(EmberCutPath.AUTO, EmberCutPath.parse("auto"));
        assertEquals(EmberCutPath.ASK, EmberCutPath.parse("ask"));
        assertEquals(EmberCutPath.MUTE, EmberCutPath.parse("mute"));
        assertEquals(EmberCutPath.NONE, EmberCutPath.parse("clear"));
        assertTrue(EmberCutPath.shouldAuto(EmberCutPath.AUTO));
        assertTrue(EmberCutPath.shouldMute(EmberCutPath.MUTE));
        assertFalse(EmberCutPath.shouldAuto(EmberCutPath.ASK));
        assertFalse(EmberCutPath.shouldMute(EmberCutPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareCutpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCutPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_cut_path"));
        assertTrue(src.contains("TIP-DZ"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 cutpath"));
        assertFalse(offerBody.contains("cutpath auto"));
        assertFalse(offerBody.contains("cutpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 cutpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D580 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCutPath.java")),
                StandardCharsets.UTF_8);
        int weekly = src.indexOf("public static boolean maybeOfferWeekly");
        assertTrue(weekly > 0);
        String weeklyBody = src.substring(weekly, src.indexOf("public static void applyCutLand", weekly));
        assertTrue(weeklyBody.contains("offerPick(p)"));
        int sched = src.indexOf("public static void scheduleOfferAfterQ01");
        assertTrue(sched > 0);
        String schedBody = src.substring(sched, src.indexOf("public static void applyAndReply", sched));
        assertTrue(schedBody.contains("maybeOfferWeekly"));
    }

    @Test
    public void combatButtons_cutUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCutPath.java")),
                StandardCharsets.UTF_8);
        // 局内「打断」战斗钮语义零改
        assertTrue(src.contains("\"[打断]\", \"/corerpg p1 cut\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 cutpath\""));
        // auto/ask/mute 真改打断语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathCutRegen"));
        assertTrue(src.contains("applyCutLand"));
        assertTrue(src.contains("cutRegenSlowTicks"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inCutPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCutPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DZ"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareCutpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean cutPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean cutGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点打断路径页"));
        assertFalse(body.contains("用法：/corerpg p1 cutpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点打断路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 cutpath"));
    }
}
