package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DQ D13348 W1a: DodgePath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 dodgepath 主 UI / 零改伤害CD掉落体力价/D571 榴弹拉开语义.
 */
public class EmberDodgePathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareDodgepath() {
        assertEquals("/ember_p1_dodge_path", EmberDodgePath.PATH_PICK_CMD);
        assertEquals("[闪榴路径]", EmberDodgePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberDodgePath.PATH_ADVENTURE_CMD);
        assertFalse(EmberDodgePath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberDodgePath.PATH_PICK_CMD.contains("dodgepath"));
        assertFalse(EmberDodgePath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareDodgepath() {
        String locked = EmberDodgePath.glance(EmberDodgePath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 dodgepath"));
        String none = EmberDodgePath.glance(EmberDodgePath.NONE, true);
        assertTrue(none.contains("点闪榴路径页"));
        assertFalse(none.contains("/corerpg p1 dodgepath"));
        String auto = EmberDodgePath.glance(EmberDodgePath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 dodgepath"));
        String ask = EmberDodgePath.glance(EmberDodgePath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 dodgepath"));
        String mute = EmberDodgePath.glance(EmberDodgePath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 dodgepath"));
    }

    @Test
    public void menuLine_labelTipReuse_dodgeSemanticsUnchanged() {
        assertEquals("闪榴路径 · 未选", EmberDodgePath.menuLine(EmberDodgePath.NONE));
        assertEquals("闪榴路径 · 闪榴·自动", EmberDodgePath.menuLine(EmberDodgePath.AUTO));
        assertEquals("闪榴路径 · 闪榴·提醒", EmberDodgePath.menuLine(EmberDodgePath.ASK));
        assertEquals("闪榴路径 · 闪榴·静默", EmberDodgePath.menuLine(EmberDodgePath.MUTE));
        assertEquals("闪榴·自动", EmberDodgePath.label(EmberDodgePath.AUTO));
        assertEquals("闪榴·提醒", EmberDodgePath.label(EmberDodgePath.ASK));
        assertEquals("闪榴·静默", EmberDodgePath.label(EmberDodgePath.MUTE));
        assertEquals("未选", EmberDodgePath.label(EmberDodgePath.NONE));
        assertTrue(EmberDodgePath.tip(EmberDodgePath.AUTO).contains("自动"));
        assertTrue(EmberDodgePath.tip(EmberDodgePath.ASK).contains("提醒"));
        assertTrue(EmberDodgePath.tip(EmberDodgePath.MUTE).contains("静默") || EmberDodgePath.tip(EmberDodgePath.MUTE).contains("提醒"));
        assertEquals(EmberDodgePath.AUTO, EmberDodgePath.parse("auto"));
        assertEquals(EmberDodgePath.ASK, EmberDodgePath.parse("ask"));
        assertEquals(EmberDodgePath.MUTE, EmberDodgePath.parse("mute"));
        assertEquals(EmberDodgePath.NONE, EmberDodgePath.parse("clear"));
        assertTrue(EmberDodgePath.shouldAuto(EmberDodgePath.AUTO));
        assertTrue(EmberDodgePath.shouldMute(EmberDodgePath.MUTE));
        assertFalse(EmberDodgePath.shouldAuto(EmberDodgePath.ASK));
        assertFalse(EmberDodgePath.shouldMute(EmberDodgePath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareDodgepathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberDodgePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_dodge_path"));
        assertTrue(src.contains("TIP-DQ"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 dodgepath"));
        assertFalse(offerBody.contains("dodgepath auto"));
        assertFalse(offerBody.contains("dodgepath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 dodgepath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D571 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberDodgePath.java")),
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
    public void combatButtons_flashDodgeUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberDodgePath.java")),
                StandardCharsets.UTF_8);
        // 局内「闪榴」战斗钮语义零改
        assertTrue(src.contains("\"[闪榴]\", \"/corerpg p1 dodge\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 dodgepath\""));
        // auto/ask/mute 真改拉开语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathDodgeMortar"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inDodgePath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberDodgePath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DQ"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareDodgepath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean dodgePathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean dodgeGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点闪榴路径页"));
        assertFalse(body.contains("用法：/corerpg p1 dodgepath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点闪榴路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 dodgepath"));
    }
}
