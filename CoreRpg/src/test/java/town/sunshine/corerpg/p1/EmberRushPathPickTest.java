package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DY D13356 W1a: RushPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 rushpath 主 UI / 零改伤害CD掉落体力价/D579 限时语义.
 */
public class EmberRushPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareRushpath() {
        assertEquals("/ember_p1_rush_path", EmberRushPath.PATH_PICK_CMD);
        assertEquals("[限时路径]", EmberRushPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberRushPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberRushPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberRushPath.PATH_PICK_CMD.contains("rushpath"));
        assertFalse(EmberRushPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareRushpath() {
        String locked = EmberRushPath.glance(EmberRushPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 rushpath"));
        String none = EmberRushPath.glance(EmberRushPath.NONE, true);
        assertTrue(none.contains("点限时路径页"));
        assertFalse(none.contains("/corerpg p1 rushpath"));
        String auto = EmberRushPath.glance(EmberRushPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 rushpath"));
        String ask = EmberRushPath.glance(EmberRushPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 rushpath"));
        String mute = EmberRushPath.glance(EmberRushPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 rushpath"));
    }

    @Test
    public void menuLine_labelTipReuse_rushSemanticsUnchanged() {
        assertEquals("限时路径 · 未选", EmberRushPath.menuLine(EmberRushPath.NONE));
        assertEquals("限时路径 · 限时·自动", EmberRushPath.menuLine(EmberRushPath.AUTO));
        assertEquals("限时路径 · 限时·提醒", EmberRushPath.menuLine(EmberRushPath.ASK));
        assertEquals("限时路径 · 限时·静默", EmberRushPath.menuLine(EmberRushPath.MUTE));
        assertEquals("限时·自动", EmberRushPath.label(EmberRushPath.AUTO));
        assertEquals("限时·提醒", EmberRushPath.label(EmberRushPath.ASK));
        assertEquals("限时·静默", EmberRushPath.label(EmberRushPath.MUTE));
        assertEquals("未选", EmberRushPath.label(EmberRushPath.NONE));
        assertTrue(EmberRushPath.tip(EmberRushPath.AUTO).contains("自动"));
        assertTrue(EmberRushPath.tip(EmberRushPath.ASK).contains("提醒"));
        assertTrue(EmberRushPath.tip(EmberRushPath.MUTE).contains("静默") || EmberRushPath.tip(EmberRushPath.MUTE).contains("提醒"));
        assertEquals(EmberRushPath.AUTO, EmberRushPath.parse("auto"));
        assertEquals(EmberRushPath.ASK, EmberRushPath.parse("ask"));
        assertEquals(EmberRushPath.MUTE, EmberRushPath.parse("mute"));
        assertEquals(EmberRushPath.NONE, EmberRushPath.parse("clear"));
        assertTrue(EmberRushPath.shouldAuto(EmberRushPath.AUTO));
        assertTrue(EmberRushPath.shouldMute(EmberRushPath.MUTE));
        assertFalse(EmberRushPath.shouldAuto(EmberRushPath.ASK));
        assertFalse(EmberRushPath.shouldMute(EmberRushPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareRushpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRushPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_rush_path"));
        assertTrue(src.contains("TIP-DY"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 rushpath"));
        assertFalse(offerBody.contains("rushpath auto"));
        assertFalse(offerBody.contains("rushpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 rushpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D579 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRushPath.java")),
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
    public void combatButtons_rushUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRushPath.java")),
                StandardCharsets.UTF_8);
        // 局内「冲」战斗钮语义零改
        assertTrue(src.contains("\"[冲]\", \"/corerpg p1 rush\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 rushpath\""));
        // auto/ask/mute 真改限时语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathRushMob"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inRushPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRushPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DY"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareRushpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean rushPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean rushGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点限时路径页"));
        assertFalse(body.contains("用法：/corerpg p1 rushpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点限时路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 rushpath"));
    }
}
