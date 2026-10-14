package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EC D13360 W1a: ShellPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 shellpath 主 UI / 零改伤害CD掉落体力价/D583 铁壁语义.
 */
public class EmberShellPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareShellpath() {
        assertEquals("/ember_p1_shell_path", EmberShellPath.PATH_PICK_CMD);
        assertEquals("[铁壁路径]", EmberShellPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberShellPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberShellPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberShellPath.PATH_PICK_CMD.contains("shellpath"));
        assertFalse(EmberShellPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareShellpath() {
        String locked = EmberShellPath.glance(EmberShellPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 shellpath"));
        String none = EmberShellPath.glance(EmberShellPath.NONE, true);
        assertTrue(none.contains("点铁壁路径页"));
        assertFalse(none.contains("/corerpg p1 shellpath"));
        String auto = EmberShellPath.glance(EmberShellPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 shellpath"));
        String ask = EmberShellPath.glance(EmberShellPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 shellpath"));
        String mute = EmberShellPath.glance(EmberShellPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 shellpath"));
    }

    @Test
    public void menuLine_labelTipReuse_shellSemanticsUnchanged() {
        assertEquals("铁壁路径 · 未选", EmberShellPath.menuLine(EmberShellPath.NONE));
        assertEquals("铁壁路径 · 铁壁·自动", EmberShellPath.menuLine(EmberShellPath.AUTO));
        assertEquals("铁壁路径 · 铁壁·提醒", EmberShellPath.menuLine(EmberShellPath.ASK));
        assertEquals("铁壁路径 · 铁壁·静默", EmberShellPath.menuLine(EmberShellPath.MUTE));
        assertEquals("铁壁·自动", EmberShellPath.label(EmberShellPath.AUTO));
        assertEquals("铁壁·提醒", EmberShellPath.label(EmberShellPath.ASK));
        assertEquals("铁壁·静默", EmberShellPath.label(EmberShellPath.MUTE));
        assertEquals("未选", EmberShellPath.label(EmberShellPath.NONE));
        assertTrue(EmberShellPath.tip(EmberShellPath.AUTO).contains("自动") || EmberShellPath.tip(EmberShellPath.AUTO).contains("铁壁"));
        assertTrue(EmberShellPath.tip(EmberShellPath.ASK).contains("提醒"));
        assertTrue(EmberShellPath.tip(EmberShellPath.MUTE).contains("静默") || EmberShellPath.tip(EmberShellPath.MUTE).contains("提醒"));
        assertEquals(EmberShellPath.AUTO, EmberShellPath.parse("auto"));
        assertEquals(EmberShellPath.ASK, EmberShellPath.parse("ask"));
        assertEquals(EmberShellPath.MUTE, EmberShellPath.parse("mute"));
        assertEquals(EmberShellPath.NONE, EmberShellPath.parse("clear"));
        assertTrue(EmberShellPath.shouldAuto(EmberShellPath.AUTO));
        assertTrue(EmberShellPath.shouldMute(EmberShellPath.MUTE));
        assertFalse(EmberShellPath.shouldAuto(EmberShellPath.ASK));
        assertFalse(EmberShellPath.shouldMute(EmberShellPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareShellpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShellPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_shell_path"));
        assertTrue(src.contains("TIP-EC"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 shellpath"));
        assertFalse(offerBody.contains("shellpath auto"));
        assertFalse(offerBody.contains("shellpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 shellpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D583 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShellPath.java")),
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
    public void combatButtons_shellUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShellPath.java")),
                StandardCharsets.UTF_8);
        // 局内「集火」战斗钮语义零改
        assertTrue(src.contains("\"[集火]\", \"/corerpg p1 shell\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 shellpath\""));
        // auto/ask/mute 真改铁壁语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathShellElite"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inShellPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberShellPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EC"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareShellpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean shellPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean shellGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点铁壁路径页"));
        assertFalse(body.contains("用法：/corerpg p1 shellpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点铁壁路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 shellpath"));
    }
}
