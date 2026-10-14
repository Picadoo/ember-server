package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EO D13372 W1a: NestPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 nestpath 主 UI / 零改伤害CD掉落体力价/D595 卡点语义.
 */
public class EmberNestPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareNestpath() {
        assertEquals("/ember_p1_nest_path", EmberNestPath.PATH_PICK_CMD);
        assertEquals("[卡点路径]", EmberNestPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberNestPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberNestPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberNestPath.PATH_PICK_CMD.contains("nestpath"));
        assertFalse(EmberNestPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareNestpath() {
        String locked = EmberNestPath.glance(EmberNestPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 nestpath"));
        String none = EmberNestPath.glance(EmberNestPath.NONE, true);
        assertTrue(none.contains("点卡点路径页"));
        assertFalse(none.contains("/corerpg p1 nestpath"));
        String auto = EmberNestPath.glance(EmberNestPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 nestpath"));
        String ask = EmberNestPath.glance(EmberNestPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 nestpath"));
        String mute = EmberNestPath.glance(EmberNestPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 nestpath"));
    }

    @Test
    public void menuLine_labelTipReuse_nestSemanticsUnchanged() {
        assertEquals("卡点路径 · 未选", EmberNestPath.menuLine(EmberNestPath.NONE));
        assertEquals("卡点路径 · 卡点·自动", EmberNestPath.menuLine(EmberNestPath.AUTO));
        assertEquals("卡点路径 · 卡点·提醒", EmberNestPath.menuLine(EmberNestPath.ASK));
        assertEquals("卡点路径 · 卡点·静默", EmberNestPath.menuLine(EmberNestPath.MUTE));
        assertEquals("卡点·自动", EmberNestPath.label(EmberNestPath.AUTO));
        assertEquals("卡点·提醒", EmberNestPath.label(EmberNestPath.ASK));
        assertEquals("卡点·静默", EmberNestPath.label(EmberNestPath.MUTE));
        assertEquals("未选", EmberNestPath.label(EmberNestPath.NONE));
        assertTrue(EmberNestPath.tip(EmberNestPath.AUTO).contains("自动") || EmberNestPath.tip(EmberNestPath.AUTO).contains("卡点") || EmberNestPath.tip(EmberNestPath.AUTO).contains("生成点"));
        assertTrue(EmberNestPath.tip(EmberNestPath.ASK).contains("提醒"));
        assertTrue(EmberNestPath.tip(EmberNestPath.MUTE).contains("静默") || EmberNestPath.tip(EmberNestPath.MUTE).contains("生成点"));
        assertEquals(EmberNestPath.AUTO, EmberNestPath.parse("auto"));
        assertEquals(EmberNestPath.ASK, EmberNestPath.parse("ask"));
        assertEquals(EmberNestPath.MUTE, EmberNestPath.parse("mute"));
        assertEquals(EmberNestPath.NONE, EmberNestPath.parse("clear"));
        assertTrue(EmberNestPath.shouldAuto(EmberNestPath.AUTO));
        assertTrue(EmberNestPath.shouldMute(EmberNestPath.MUTE));
        assertFalse(EmberNestPath.shouldAuto(EmberNestPath.ASK));
        assertFalse(EmberNestPath.shouldMute(EmberNestPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareNestpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberNestPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_nest_path"));
        assertTrue(src.contains("TIP-EO"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 nestpath"));
        assertFalse(offerBody.contains("nestpath auto"));
        assertFalse(offerBody.contains("nestpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 nestpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberNestPath.java")),
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
    public void combatButtons_nestUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberNestPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[卡点]\", \"/corerpg p1 nest\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 nestpath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathNestSpot"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inNestPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberNestPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EO"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareNestpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean nestPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean nestGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点卡点路径页"));
        assertFalse(body.contains("用法：/corerpg p1 nestpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点卡点路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 nestpath"));
    }
}
