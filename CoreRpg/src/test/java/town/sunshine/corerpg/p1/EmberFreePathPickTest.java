package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DR D13349 W1a: FreePath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 freepath 主 UI / 零改伤害CD掉落体力价/D570 禁锢解锢语义.
 */
public class EmberFreePathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareFreepath() {
        assertEquals("/ember_p1_free_path", EmberFreePath.PATH_PICK_CMD);
        assertEquals("[解锢路径]", EmberFreePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberFreePath.PATH_ADVENTURE_CMD);
        assertFalse(EmberFreePath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberFreePath.PATH_PICK_CMD.contains("freepath"));
        assertFalse(EmberFreePath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareFreepath() {
        String locked = EmberFreePath.glance(EmberFreePath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 freepath"));
        String none = EmberFreePath.glance(EmberFreePath.NONE, true);
        assertTrue(none.contains("点解锢路径页"));
        assertFalse(none.contains("/corerpg p1 freepath"));
        String auto = EmberFreePath.glance(EmberFreePath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 freepath"));
        String ask = EmberFreePath.glance(EmberFreePath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 freepath"));
        String mute = EmberFreePath.glance(EmberFreePath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 freepath"));
    }

    @Test
    public void menuLine_labelTipReuse_freeSemanticsUnchanged() {
        assertEquals("解锢路径 · 未选", EmberFreePath.menuLine(EmberFreePath.NONE));
        assertEquals("解锢路径 · 解锢·自动", EmberFreePath.menuLine(EmberFreePath.AUTO));
        assertEquals("解锢路径 · 解锢·提醒", EmberFreePath.menuLine(EmberFreePath.ASK));
        assertEquals("解锢路径 · 解锢·静默", EmberFreePath.menuLine(EmberFreePath.MUTE));
        assertEquals("解锢·自动", EmberFreePath.label(EmberFreePath.AUTO));
        assertEquals("解锢·提醒", EmberFreePath.label(EmberFreePath.ASK));
        assertEquals("解锢·静默", EmberFreePath.label(EmberFreePath.MUTE));
        assertEquals("未选", EmberFreePath.label(EmberFreePath.NONE));
        assertTrue(EmberFreePath.tip(EmberFreePath.AUTO).contains("自动"));
        assertTrue(EmberFreePath.tip(EmberFreePath.ASK).contains("提醒"));
        assertTrue(EmberFreePath.tip(EmberFreePath.MUTE).contains("静默") || EmberFreePath.tip(EmberFreePath.MUTE).contains("提醒"));
        assertEquals(EmberFreePath.AUTO, EmberFreePath.parse("auto"));
        assertEquals(EmberFreePath.ASK, EmberFreePath.parse("ask"));
        assertEquals(EmberFreePath.MUTE, EmberFreePath.parse("mute"));
        assertEquals(EmberFreePath.NONE, EmberFreePath.parse("clear"));
        assertTrue(EmberFreePath.shouldAuto(EmberFreePath.AUTO));
        assertTrue(EmberFreePath.shouldMute(EmberFreePath.MUTE));
        assertFalse(EmberFreePath.shouldAuto(EmberFreePath.ASK));
        assertFalse(EmberFreePath.shouldMute(EmberFreePath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareFreepathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFreePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_free_path"));
        assertTrue(src.contains("TIP-DR"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 freepath"));
        assertFalse(offerBody.contains("freepath auto"));
        assertFalse(offerBody.contains("freepath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 freepath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D570 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFreePath.java")),
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
    public void combatButtons_freeUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFreePath.java")),
                StandardCharsets.UTF_8);
        // 局内「解锢」战斗钮语义零改
        assertTrue(src.contains("\"[解锢]\", \"/corerpg p1 free\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 freepath\""));
        // auto/ask/mute 真改解锢语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathFreeJailer") || src.contains("freeLocal"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inFreePath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFreePath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DR"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareFreepath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean freePathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean freeGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点解锢路径页"));
        assertFalse(body.contains("用法：/corerpg p1 freepath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点解锢路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 freepath"));
    }
}
