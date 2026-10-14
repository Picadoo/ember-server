package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EN D13371 W1a: SavePath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 savepath 主 UI / 零改伤害CD掉落体力价/D594 终救靠拢语义.
 */
public class EmberSavePathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareSavepath() {
        assertEquals("/ember_p1_save_path", EmberSavePath.PATH_PICK_CMD);
        assertEquals("[终救路径]", EmberSavePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberSavePath.PATH_ADVENTURE_CMD);
        assertFalse(EmberSavePath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberSavePath.PATH_PICK_CMD.contains("savepath"));
        assertFalse(EmberSavePath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareSavepath() {
        String locked = EmberSavePath.glance(EmberSavePath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 savepath"));
        String none = EmberSavePath.glance(EmberSavePath.NONE, true);
        assertTrue(none.contains("点终救路径页"));
        assertFalse(none.contains("/corerpg p1 savepath"));
        String auto = EmberSavePath.glance(EmberSavePath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 savepath"));
        String ask = EmberSavePath.glance(EmberSavePath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 savepath"));
        String mute = EmberSavePath.glance(EmberSavePath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 savepath"));
    }

    @Test
    public void menuLine_labelTipReuse_saveSemanticsUnchanged() {
        assertEquals("终救路径 · 未选", EmberSavePath.menuLine(EmberSavePath.NONE));
        assertEquals("终救路径 · 终救·自动", EmberSavePath.menuLine(EmberSavePath.AUTO));
        assertEquals("终救路径 · 终救·提醒", EmberSavePath.menuLine(EmberSavePath.ASK));
        assertEquals("终救路径 · 终救·静默", EmberSavePath.menuLine(EmberSavePath.MUTE));
        assertEquals("终救·自动", EmberSavePath.label(EmberSavePath.AUTO));
        assertEquals("终救·提醒", EmberSavePath.label(EmberSavePath.ASK));
        assertEquals("终救·静默", EmberSavePath.label(EmberSavePath.MUTE));
        assertEquals("未选", EmberSavePath.label(EmberSavePath.NONE));
        assertTrue(EmberSavePath.tip(EmberSavePath.AUTO).contains("自动") || EmberSavePath.tip(EmberSavePath.AUTO).contains("靠拢"));
        assertTrue(EmberSavePath.tip(EmberSavePath.ASK).contains("提醒"));
        assertTrue(EmberSavePath.tip(EmberSavePath.MUTE).contains("静默") || EmberSavePath.tip(EmberSavePath.MUTE).contains("靠拢"));
        assertEquals(EmberSavePath.AUTO, EmberSavePath.parse("auto"));
        assertEquals(EmberSavePath.ASK, EmberSavePath.parse("ask"));
        assertEquals(EmberSavePath.MUTE, EmberSavePath.parse("mute"));
        assertEquals(EmberSavePath.NONE, EmberSavePath.parse("clear"));
        assertTrue(EmberSavePath.shouldAuto(EmberSavePath.AUTO));
        assertTrue(EmberSavePath.shouldMute(EmberSavePath.MUTE));
        assertFalse(EmberSavePath.shouldAuto(EmberSavePath.ASK));
        assertFalse(EmberSavePath.shouldMute(EmberSavePath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareSavepathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSavePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_save_path"));
        assertTrue(src.contains("TIP-EN"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 savepath"));
        assertFalse(offerBody.contains("savepath auto"));
        assertFalse(offerBody.contains("savepath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 savepath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSavePath.java")),
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
    public void combatButtons_gatherUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSavePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("\"[靠拢]\", \"/corerpg p1 save\""));
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 savepath\""));
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathSaveReady"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inSavePath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSavePath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EN"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareSavepath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean savePathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean saveGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点终救路径页"));
        assertFalse(body.contains("用法：/corerpg p1 savepath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点终救路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 savepath"));
    }
}
