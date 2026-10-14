package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EF D13363 W1a: PhasePath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 phasepath 主 UI / 零改伤害CD掉落体力价/D586 半血语义.
 */
public class EmberPhasePathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBarePhasepath() {
        assertEquals("/ember_p1_phase_path", EmberPhasePath.PATH_PICK_CMD);
        assertEquals("[半血路径]", EmberPhasePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberPhasePath.PATH_ADVENTURE_CMD);
        assertFalse(EmberPhasePath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberPhasePath.PATH_PICK_CMD.contains("phasepath"));
        assertFalse(EmberPhasePath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBarePhasepath() {
        String locked = EmberPhasePath.glance(EmberPhasePath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 phasepath"));
        String none = EmberPhasePath.glance(EmberPhasePath.NONE, true);
        assertTrue(none.contains("点半血路径页"));
        assertFalse(none.contains("/corerpg p1 phasepath"));
        String auto = EmberPhasePath.glance(EmberPhasePath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 phasepath"));
        String ask = EmberPhasePath.glance(EmberPhasePath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 phasepath"));
        String mute = EmberPhasePath.glance(EmberPhasePath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 phasepath"));
    }

    @Test
    public void menuLine_labelTipReuse_phaseSemanticsUnchanged() {
        assertEquals("半血路径 · 未选", EmberPhasePath.menuLine(EmberPhasePath.NONE));
        assertEquals("半血路径 · 半血·自动", EmberPhasePath.menuLine(EmberPhasePath.AUTO));
        assertEquals("半血路径 · 半血·提醒", EmberPhasePath.menuLine(EmberPhasePath.ASK));
        assertEquals("半血路径 · 半血·静默", EmberPhasePath.menuLine(EmberPhasePath.MUTE));
        assertEquals("半血·自动", EmberPhasePath.label(EmberPhasePath.AUTO));
        assertEquals("半血·提醒", EmberPhasePath.label(EmberPhasePath.ASK));
        assertEquals("半血·静默", EmberPhasePath.label(EmberPhasePath.MUTE));
        assertEquals("未选", EmberPhasePath.label(EmberPhasePath.NONE));
        assertTrue(EmberPhasePath.tip(EmberPhasePath.AUTO).contains("自动") || EmberPhasePath.tip(EmberPhasePath.AUTO).contains("半血"));
        assertTrue(EmberPhasePath.tip(EmberPhasePath.ASK).contains("提醒"));
        assertTrue(EmberPhasePath.tip(EmberPhasePath.MUTE).contains("静默") || EmberPhasePath.tip(EmberPhasePath.MUTE).contains("提醒"));
        assertEquals(EmberPhasePath.AUTO, EmberPhasePath.parse("auto"));
        assertEquals(EmberPhasePath.ASK, EmberPhasePath.parse("ask"));
        assertEquals(EmberPhasePath.MUTE, EmberPhasePath.parse("mute"));
        assertEquals(EmberPhasePath.NONE, EmberPhasePath.parse("clear"));
        assertTrue(EmberPhasePath.shouldAuto(EmberPhasePath.AUTO));
        assertTrue(EmberPhasePath.shouldMute(EmberPhasePath.MUTE));
        assertFalse(EmberPhasePath.shouldAuto(EmberPhasePath.ASK));
        assertFalse(EmberPhasePath.shouldMute(EmberPhasePath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBarePhasepathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberPhasePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_phase_path"));
        assertTrue(src.contains("TIP-EF"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 phasepath"));
        assertFalse(offerBody.contains("phasepath auto"));
        assertFalse(offerBody.contains("phasepath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 phasepath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberPhasePath.java")),
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
    public void combatButtons_phaseUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberPhasePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[加压]\", \"/corerpg p1 phase\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 phasepath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathPhaseBoss"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inPhasePath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberPhasePath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EF"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBarePhasepath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean phasePathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean phaseGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点半血路径页"));
        assertFalse(body.contains("用法：/corerpg p1 phasepath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点半血路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 phasepath"));
    }
}
