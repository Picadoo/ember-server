package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EA D13358 W1a: PressPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 presspath 主 UI / 零改伤害CD掉落体力价/D581 压伤语义.
 */
public class EmberPressPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBarePresspath() {
        assertEquals("/ember_p1_press_path", EmberPressPath.PATH_PICK_CMD);
        assertEquals("[压伤路径]", EmberPressPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberPressPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberPressPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberPressPath.PATH_PICK_CMD.contains("presspath"));
        assertFalse(EmberPressPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBarePresspath() {
        String locked = EmberPressPath.glance(EmberPressPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 presspath"));
        String none = EmberPressPath.glance(EmberPressPath.NONE, true);
        assertTrue(none.contains("点压伤路径页"));
        assertFalse(none.contains("/corerpg p1 presspath"));
        String auto = EmberPressPath.glance(EmberPressPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 presspath"));
        String ask = EmberPressPath.glance(EmberPressPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 presspath"));
        String mute = EmberPressPath.glance(EmberPressPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 presspath"));
    }

    @Test
    public void menuLine_labelTipReuse_pressSemanticsUnchanged() {
        assertEquals("压伤路径 · 未选", EmberPressPath.menuLine(EmberPressPath.NONE));
        assertEquals("压伤路径 · 压伤·自动", EmberPressPath.menuLine(EmberPressPath.AUTO));
        assertEquals("压伤路径 · 压伤·提醒", EmberPressPath.menuLine(EmberPressPath.ASK));
        assertEquals("压伤路径 · 压伤·静默", EmberPressPath.menuLine(EmberPressPath.MUTE));
        assertEquals("压伤·自动", EmberPressPath.label(EmberPressPath.AUTO));
        assertEquals("压伤·提醒", EmberPressPath.label(EmberPressPath.ASK));
        assertEquals("压伤·静默", EmberPressPath.label(EmberPressPath.MUTE));
        assertEquals("未选", EmberPressPath.label(EmberPressPath.NONE));
        assertTrue(EmberPressPath.tip(EmberPressPath.AUTO).contains("自动"));
        assertTrue(EmberPressPath.tip(EmberPressPath.ASK).contains("提醒"));
        assertTrue(EmberPressPath.tip(EmberPressPath.MUTE).contains("静默") || EmberPressPath.tip(EmberPressPath.MUTE).contains("提醒"));
        assertEquals(EmberPressPath.AUTO, EmberPressPath.parse("auto"));
        assertEquals(EmberPressPath.ASK, EmberPressPath.parse("ask"));
        assertEquals(EmberPressPath.MUTE, EmberPressPath.parse("mute"));
        assertEquals(EmberPressPath.NONE, EmberPressPath.parse("clear"));
        assertTrue(EmberPressPath.shouldAuto(EmberPressPath.AUTO));
        assertTrue(EmberPressPath.shouldMute(EmberPressPath.MUTE));
        assertFalse(EmberPressPath.shouldAuto(EmberPressPath.ASK));
        assertFalse(EmberPressPath.shouldMute(EmberPressPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBarePresspathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberPressPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_press_path"));
        assertTrue(src.contains("TIP-EA"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 presspath"));
        assertFalse(offerBody.contains("presspath auto"));
        assertFalse(offerBody.contains("presspath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 presspath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D581 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberPressPath.java")),
                StandardCharsets.UTF_8);
        int weekly = src.indexOf("public static boolean maybeOfferWeekly");
        assertTrue(weekly > 0);
        String weeklyBody = src.substring(weekly, src.indexOf("public static int pressBossSlowTicks", weekly));
        assertTrue(weeklyBody.contains("offerPick(p)"));
        int sched = src.indexOf("public static void scheduleOfferAfterQ01");
        assertTrue(sched > 0);
        String schedBody = src.substring(sched, src.indexOf("public static void applyAndReply", sched));
        assertTrue(schedBody.contains("maybeOfferWeekly"));
    }

    @Test
    public void combatButtons_pressUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberPressPath.java")),
                StandardCharsets.UTF_8);
        // 局内「压伤」战斗钮语义零改
        assertTrue(src.contains("\"[压伤]\", \"/corerpg p1 press\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 presspath\""));
        // auto/ask/mute 真改压伤语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathPressBoss"));
        assertTrue(src.contains("applyPressLand"));
        assertTrue(src.contains("pressBossSlowTicks"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inPressPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberPressPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EA"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBarePresspath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean pressPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean pressGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点压伤路径页"));
        assertFalse(body.contains("用法：/corerpg p1 presspath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点压伤路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 presspath"));
    }
}
