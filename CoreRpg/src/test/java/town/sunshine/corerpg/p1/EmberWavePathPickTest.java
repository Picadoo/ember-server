package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EE D13362 W1a: WavePath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 wavepath 主 UI / 零改伤害CD掉落体力价/D585 增援语义.
 */
public class EmberWavePathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareWavepath() {
        assertEquals("/ember_p1_wave_path", EmberWavePath.PATH_PICK_CMD);
        assertEquals("[增援路径]", EmberWavePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberWavePath.PATH_ADVENTURE_CMD);
        assertFalse(EmberWavePath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberWavePath.PATH_PICK_CMD.contains("wavepath"));
        assertFalse(EmberWavePath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareWavepath() {
        String locked = EmberWavePath.glance(EmberWavePath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 wavepath"));
        String none = EmberWavePath.glance(EmberWavePath.NONE, true);
        assertTrue(none.contains("点增援路径页"));
        assertFalse(none.contains("/corerpg p1 wavepath"));
        String auto = EmberWavePath.glance(EmberWavePath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 wavepath"));
        String ask = EmberWavePath.glance(EmberWavePath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 wavepath"));
        String mute = EmberWavePath.glance(EmberWavePath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 wavepath"));
    }

    @Test
    public void menuLine_labelTipReuse_waveSemanticsUnchanged() {
        assertEquals("增援路径 · 未选", EmberWavePath.menuLine(EmberWavePath.NONE));
        assertEquals("增援路径 · 增援·自动", EmberWavePath.menuLine(EmberWavePath.AUTO));
        assertEquals("增援路径 · 增援·提醒", EmberWavePath.menuLine(EmberWavePath.ASK));
        assertEquals("增援路径 · 增援·静默", EmberWavePath.menuLine(EmberWavePath.MUTE));
        assertEquals("增援·自动", EmberWavePath.label(EmberWavePath.AUTO));
        assertEquals("增援·提醒", EmberWavePath.label(EmberWavePath.ASK));
        assertEquals("增援·静默", EmberWavePath.label(EmberWavePath.MUTE));
        assertEquals("未选", EmberWavePath.label(EmberWavePath.NONE));
        assertTrue(EmberWavePath.tip(EmberWavePath.AUTO).contains("自动") || EmberWavePath.tip(EmberWavePath.AUTO).contains("增援"));
        assertTrue(EmberWavePath.tip(EmberWavePath.ASK).contains("提醒"));
        assertTrue(EmberWavePath.tip(EmberWavePath.MUTE).contains("静默") || EmberWavePath.tip(EmberWavePath.MUTE).contains("提醒"));
        assertEquals(EmberWavePath.AUTO, EmberWavePath.parse("auto"));
        assertEquals(EmberWavePath.ASK, EmberWavePath.parse("ask"));
        assertEquals(EmberWavePath.MUTE, EmberWavePath.parse("mute"));
        assertEquals(EmberWavePath.NONE, EmberWavePath.parse("clear"));
        assertTrue(EmberWavePath.shouldAuto(EmberWavePath.AUTO));
        assertTrue(EmberWavePath.shouldMute(EmberWavePath.MUTE));
        assertFalse(EmberWavePath.shouldAuto(EmberWavePath.ASK));
        assertFalse(EmberWavePath.shouldMute(EmberWavePath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareWavepathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberWavePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_wave_path"));
        assertTrue(src.contains("TIP-EE"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 wavepath"));
        assertFalse(offerBody.contains("wavepath auto"));
        assertFalse(offerBody.contains("wavepath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 wavepath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberWavePath.java")),
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
    public void combatButtons_waveUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberWavePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[清援]\", \"/corerpg p1 wave\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 wavepath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathWaveAdd"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inWavePath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberWavePath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EE"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareWavepath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean wavePathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean waveGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点增援路径页"));
        assertFalse(body.contains("用法：/corerpg p1 wavepath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点增援路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 wavepath"));
    }
}
