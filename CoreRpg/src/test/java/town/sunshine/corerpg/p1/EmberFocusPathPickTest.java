package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EQ D13374 W1a: FocusPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 focuspath 主 UI / 零改伤害CD掉落体力价/D597 集火语义.
 */
public class EmberFocusPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareFocuspath() {
        assertEquals("/ember_p1_focus_path", EmberFocusPath.PATH_PICK_CMD);
        assertEquals("[集火路径]", EmberFocusPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberFocusPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberFocusPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberFocusPath.PATH_PICK_CMD.contains("focuspath"));
        assertFalse(EmberFocusPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareFocuspath() {
        String locked = EmberFocusPath.glance(EmberFocusPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 focuspath"));
        String none = EmberFocusPath.glance(EmberFocusPath.NONE, true);
        assertTrue(none.contains("点集火路径页"));
        assertFalse(none.contains("/corerpg p1 focuspath"));
        String auto = EmberFocusPath.glance(EmberFocusPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 focuspath"));
        String ask = EmberFocusPath.glance(EmberFocusPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 focuspath"));
        String mute = EmberFocusPath.glance(EmberFocusPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 focuspath"));
    }

    @Test
    public void menuLine_labelTipReuse_focusSemanticsUnchanged() {
        assertEquals("集火路径 · 未选", EmberFocusPath.menuLine(EmberFocusPath.NONE));
        assertEquals("集火路径 · 集火·自动", EmberFocusPath.menuLine(EmberFocusPath.AUTO));
        assertEquals("集火路径 · 集火·提醒", EmberFocusPath.menuLine(EmberFocusPath.ASK));
        assertEquals("集火路径 · 集火·静默", EmberFocusPath.menuLine(EmberFocusPath.MUTE));
        assertEquals("集火·自动", EmberFocusPath.label(EmberFocusPath.AUTO));
        assertEquals("集火·提醒", EmberFocusPath.label(EmberFocusPath.ASK));
        assertEquals("集火·静默", EmberFocusPath.label(EmberFocusPath.MUTE));
        assertEquals("未选", EmberFocusPath.label(EmberFocusPath.NONE));
        assertTrue(EmberFocusPath.tip(EmberFocusPath.AUTO).contains("自动") || EmberFocusPath.tip(EmberFocusPath.AUTO).contains("词缀") || EmberFocusPath.tip(EmberFocusPath.AUTO).contains("集火"));
        assertTrue(EmberFocusPath.tip(EmberFocusPath.ASK).contains("提醒"));
        assertTrue(EmberFocusPath.tip(EmberFocusPath.MUTE).contains("提醒") || EmberFocusPath.tip(EmberFocusPath.MUTE).contains("词缀"));
        assertEquals(EmberFocusPath.AUTO, EmberFocusPath.parse("auto"));
        assertEquals(EmberFocusPath.ASK, EmberFocusPath.parse("ask"));
        assertEquals(EmberFocusPath.MUTE, EmberFocusPath.parse("mute"));
        assertEquals(EmberFocusPath.NONE, EmberFocusPath.parse("clear"));
        assertTrue(EmberFocusPath.shouldAuto(EmberFocusPath.AUTO));
        assertTrue(EmberFocusPath.shouldMute(EmberFocusPath.MUTE));
        assertFalse(EmberFocusPath.shouldAuto(EmberFocusPath.ASK));
        assertFalse(EmberFocusPath.shouldMute(EmberFocusPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareFocuspathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFocusPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_focus_path"));
        assertTrue(src.contains("TIP-EQ"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 focuspath"));
        assertFalse(offerBody.contains("focuspath auto"));
        assertFalse(offerBody.contains("focuspath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 focuspath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFocusPath.java")),
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
    public void combatButtons_focusUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFocusPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[集火]\", \"/corerpg p1 focus\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 focuspath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("maybeAfterFocus"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inFocusPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFocusPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EQ"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareFocuspath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean focusPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean focusGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点集火路径页"));
        assertFalse(body.contains("用法：/corerpg p1 focuspath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点集火路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 focuspath"));
    }
}
