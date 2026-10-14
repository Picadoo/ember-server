package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DV D13353 W1a: BlazePath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 blazepath 主 UI / 零改伤害CD掉落体力价/D576 焰圈避焰语义.
 */
public class EmberBlazePathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareBlazepath() {
        assertEquals("/ember_p1_blaze_path", EmberBlazePath.PATH_PICK_CMD);
        assertEquals("[避焰路径]", EmberBlazePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberBlazePath.PATH_ADVENTURE_CMD);
        assertFalse(EmberBlazePath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberBlazePath.PATH_PICK_CMD.contains("blazepath"));
        assertFalse(EmberBlazePath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareBlazepath() {
        String locked = EmberBlazePath.glance(EmberBlazePath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 blazepath"));
        String none = EmberBlazePath.glance(EmberBlazePath.NONE, true);
        assertTrue(none.contains("点避焰路径页"));
        assertFalse(none.contains("/corerpg p1 blazepath"));
        String auto = EmberBlazePath.glance(EmberBlazePath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 blazepath"));
        String ask = EmberBlazePath.glance(EmberBlazePath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 blazepath"));
        String mute = EmberBlazePath.glance(EmberBlazePath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 blazepath"));
    }

    @Test
    public void menuLine_labelTipReuse_blazeSemanticsUnchanged() {
        assertEquals("避焰路径 · 未选", EmberBlazePath.menuLine(EmberBlazePath.NONE));
        assertEquals("避焰路径 · 避焰·自动", EmberBlazePath.menuLine(EmberBlazePath.AUTO));
        assertEquals("避焰路径 · 避焰·提醒", EmberBlazePath.menuLine(EmberBlazePath.ASK));
        assertEquals("避焰路径 · 避焰·静默", EmberBlazePath.menuLine(EmberBlazePath.MUTE));
        assertEquals("避焰·自动", EmberBlazePath.label(EmberBlazePath.AUTO));
        assertEquals("避焰·提醒", EmberBlazePath.label(EmberBlazePath.ASK));
        assertEquals("避焰·静默", EmberBlazePath.label(EmberBlazePath.MUTE));
        assertEquals("未选", EmberBlazePath.label(EmberBlazePath.NONE));
        assertTrue(EmberBlazePath.tip(EmberBlazePath.AUTO).contains("自动"));
        assertTrue(EmberBlazePath.tip(EmberBlazePath.ASK).contains("提醒"));
        assertTrue(EmberBlazePath.tip(EmberBlazePath.MUTE).contains("静默") || EmberBlazePath.tip(EmberBlazePath.MUTE).contains("提醒"));
        assertEquals(EmberBlazePath.AUTO, EmberBlazePath.parse("auto"));
        assertEquals(EmberBlazePath.ASK, EmberBlazePath.parse("ask"));
        assertEquals(EmberBlazePath.MUTE, EmberBlazePath.parse("mute"));
        assertEquals(EmberBlazePath.NONE, EmberBlazePath.parse("clear"));
        assertTrue(EmberBlazePath.shouldAuto(EmberBlazePath.AUTO));
        assertTrue(EmberBlazePath.shouldMute(EmberBlazePath.MUTE));
        assertFalse(EmberBlazePath.shouldAuto(EmberBlazePath.ASK));
        assertFalse(EmberBlazePath.shouldMute(EmberBlazePath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareBlazepathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBlazePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_blaze_path"));
        assertTrue(src.contains("TIP-DV"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 blazepath"));
        assertFalse(offerBody.contains("blazepath auto"));
        assertFalse(offerBody.contains("blazepath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 blazepath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D576 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBlazePath.java")),
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
    public void combatButtons_blazeUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBlazePath.java")),
                StandardCharsets.UTF_8);
        // 局内「避焰」战斗钮语义零改
        assertTrue(src.contains("\"[避焰]\", \"/corerpg p1 blaze\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 blazepath\""));
        // auto/ask/mute 真改避焰语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathBlazeOut"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inBlazePath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberBlazePath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DV"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareBlazepath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean blazePathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean blazeGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点避焰路径页"));
        assertFalse(body.contains("用法：/corerpg p1 blazepath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点避焰路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 blazepath"));
    }
}
