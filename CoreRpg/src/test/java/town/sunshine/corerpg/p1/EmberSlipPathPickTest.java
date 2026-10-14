package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EK D13368 W1a: SlipPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 slippath 主 UI / 零改伤害CD掉落体力价/D591 闪招语义.
 */
public class EmberSlipPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareSlippath() {
        assertEquals("/ember_p1_slip_path", EmberSlipPath.PATH_PICK_CMD);
        assertEquals("[闪招路径]", EmberSlipPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberSlipPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberSlipPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberSlipPath.PATH_PICK_CMD.contains("slippath"));
        assertFalse(EmberSlipPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareSlippath() {
        String locked = EmberSlipPath.glance(EmberSlipPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 slippath"));
        String none = EmberSlipPath.glance(EmberSlipPath.NONE, true);
        assertTrue(none.contains("点闪招路径页"));
        assertFalse(none.contains("/corerpg p1 slippath"));
        String auto = EmberSlipPath.glance(EmberSlipPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 slippath"));
        String ask = EmberSlipPath.glance(EmberSlipPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 slippath"));
        String mute = EmberSlipPath.glance(EmberSlipPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 slippath"));
    }

    @Test
    public void menuLine_labelTipReuse_slipSemanticsUnchanged() {
        assertEquals("闪招路径 · 未选", EmberSlipPath.menuLine(EmberSlipPath.NONE));
        assertEquals("闪招路径 · 闪招·自动", EmberSlipPath.menuLine(EmberSlipPath.AUTO));
        assertEquals("闪招路径 · 闪招·提醒", EmberSlipPath.menuLine(EmberSlipPath.ASK));
        assertEquals("闪招路径 · 闪招·静默", EmberSlipPath.menuLine(EmberSlipPath.MUTE));
        assertEquals("闪招·自动", EmberSlipPath.label(EmberSlipPath.AUTO));
        assertEquals("闪招·提醒", EmberSlipPath.label(EmberSlipPath.ASK));
        assertEquals("闪招·静默", EmberSlipPath.label(EmberSlipPath.MUTE));
        assertEquals("未选", EmberSlipPath.label(EmberSlipPath.NONE));
        assertTrue(EmberSlipPath.tip(EmberSlipPath.AUTO).contains("自动") || EmberSlipPath.tip(EmberSlipPath.AUTO).contains("侧移"));
        assertTrue(EmberSlipPath.tip(EmberSlipPath.ASK).contains("提醒"));
        assertTrue(EmberSlipPath.tip(EmberSlipPath.MUTE).contains("静默") || EmberSlipPath.tip(EmberSlipPath.MUTE).contains("轻招"));
        assertEquals(EmberSlipPath.AUTO, EmberSlipPath.parse("auto"));
        assertEquals(EmberSlipPath.ASK, EmberSlipPath.parse("ask"));
        assertEquals(EmberSlipPath.MUTE, EmberSlipPath.parse("mute"));
        assertEquals(EmberSlipPath.NONE, EmberSlipPath.parse("clear"));
        assertTrue(EmberSlipPath.shouldAuto(EmberSlipPath.AUTO));
        assertTrue(EmberSlipPath.shouldMute(EmberSlipPath.MUTE));
        assertFalse(EmberSlipPath.shouldAuto(EmberSlipPath.ASK));
        assertFalse(EmberSlipPath.shouldMute(EmberSlipPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareSlippathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSlipPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_slip_path"));
        assertTrue(src.contains("TIP-EK"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 slippath"));
        assertFalse(offerBody.contains("slippath auto"));
        assertFalse(offerBody.contains("slippath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 slippath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSlipPath.java")),
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
    public void combatButtons_slipUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSlipPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[闪招]\", \"/corerpg p1 slip\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 slippath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathSlipOut"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inSlipPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSlipPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EK"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareSlippath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean slipPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean slipGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点闪招路径页"));
        assertFalse(body.contains("用法：/corerpg p1 slippath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点闪招路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 slippath"));
    }
}
