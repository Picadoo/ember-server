package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-EB D13359 W1a: ClonePath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 clonepath 主 UI / 零改伤害CD掉落体力价/D582 分身语义.
 */
public class EmberClonePathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareClonepath() {
        assertEquals("/ember_p1_clone_path", EmberClonePath.PATH_PICK_CMD);
        assertEquals("[分身路径]", EmberClonePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberClonePath.PATH_ADVENTURE_CMD);
        assertFalse(EmberClonePath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberClonePath.PATH_PICK_CMD.contains("clonepath"));
        assertFalse(EmberClonePath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareClonepath() {
        String locked = EmberClonePath.glance(EmberClonePath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 clonepath"));
        String none = EmberClonePath.glance(EmberClonePath.NONE, true);
        assertTrue(none.contains("点分身路径页"));
        assertFalse(none.contains("/corerpg p1 clonepath"));
        String auto = EmberClonePath.glance(EmberClonePath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 clonepath"));
        String ask = EmberClonePath.glance(EmberClonePath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 clonepath"));
        String mute = EmberClonePath.glance(EmberClonePath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 clonepath"));
    }

    @Test
    public void menuLine_labelTipReuse_cloneSemanticsUnchanged() {
        assertEquals("分身路径 · 未选", EmberClonePath.menuLine(EmberClonePath.NONE));
        assertEquals("分身路径 · 分身·自动", EmberClonePath.menuLine(EmberClonePath.AUTO));
        assertEquals("分身路径 · 分身·提醒", EmberClonePath.menuLine(EmberClonePath.ASK));
        assertEquals("分身路径 · 分身·静默", EmberClonePath.menuLine(EmberClonePath.MUTE));
        assertEquals("分身·自动", EmberClonePath.label(EmberClonePath.AUTO));
        assertEquals("分身·提醒", EmberClonePath.label(EmberClonePath.ASK));
        assertEquals("分身·静默", EmberClonePath.label(EmberClonePath.MUTE));
        assertEquals("未选", EmberClonePath.label(EmberClonePath.NONE));
        assertTrue(EmberClonePath.tip(EmberClonePath.AUTO).contains("自动") || EmberClonePath.tip(EmberClonePath.AUTO).contains("分身"));
        assertTrue(EmberClonePath.tip(EmberClonePath.ASK).contains("提醒"));
        assertTrue(EmberClonePath.tip(EmberClonePath.MUTE).contains("静默") || EmberClonePath.tip(EmberClonePath.MUTE).contains("提醒"));
        assertEquals(EmberClonePath.AUTO, EmberClonePath.parse("auto"));
        assertEquals(EmberClonePath.ASK, EmberClonePath.parse("ask"));
        assertEquals(EmberClonePath.MUTE, EmberClonePath.parse("mute"));
        assertEquals(EmberClonePath.NONE, EmberClonePath.parse("clear"));
        assertTrue(EmberClonePath.shouldAuto(EmberClonePath.AUTO));
        assertTrue(EmberClonePath.shouldMute(EmberClonePath.MUTE));
        assertFalse(EmberClonePath.shouldAuto(EmberClonePath.ASK));
        assertFalse(EmberClonePath.shouldMute(EmberClonePath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareClonepathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberClonePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_clone_path"));
        assertTrue(src.contains("TIP-EB"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 clonepath"));
        assertFalse(offerBody.contains("clonepath auto"));
        assertFalse(offerBody.contains("clonepath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 clonepath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D582 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberClonePath.java")),
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
    public void combatButtons_cloneUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberClonePath.java")),
                StandardCharsets.UTF_8);
        // 局内「清分身」战斗钮语义零改
        assertTrue(src.contains("\"[清分身]\", \"/corerpg p1 clone\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 clonepath\""));
        // auto/ask/mute 真改分身语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathCloneAdd"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inClonePath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberClonePath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-EB"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareClonepath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean clonePathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean cloneGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点分身路径页"));
        assertFalse(body.contains("用法：/corerpg p1 clonepath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点分身路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 clonepath"));
    }
}
