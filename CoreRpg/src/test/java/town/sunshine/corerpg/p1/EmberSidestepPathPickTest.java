package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DP D13347 W1a: SidestepPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 sidesteppath 主 UI / 零改伤害CD掉落体力价/D572 冲锋侧移语义.
 */
public class EmberSidestepPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareSidesteppath() {
        assertEquals("/ember_p1_sidestep_path", EmberSidestepPath.PATH_PICK_CMD);
        assertEquals("[闪冲路径]", EmberSidestepPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberSidestepPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberSidestepPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberSidestepPath.PATH_PICK_CMD.contains("sidesteppath"));
        assertFalse(EmberSidestepPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareSidesteppath() {
        String locked = EmberSidestepPath.glance(EmberSidestepPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 sidesteppath"));
        String none = EmberSidestepPath.glance(EmberSidestepPath.NONE, true);
        assertTrue(none.contains("点闪冲路径页"));
        assertFalse(none.contains("/corerpg p1 sidesteppath"));
        String auto = EmberSidestepPath.glance(EmberSidestepPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 sidesteppath"));
        String ask = EmberSidestepPath.glance(EmberSidestepPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 sidesteppath"));
        String mute = EmberSidestepPath.glance(EmberSidestepPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 sidesteppath"));
    }

    @Test
    public void menuLine_labelTipReuse_sidestepSemanticsUnchanged() {
        assertEquals("闪冲路径 · 未选", EmberSidestepPath.menuLine(EmberSidestepPath.NONE));
        assertEquals("闪冲路径 · 闪冲·自动", EmberSidestepPath.menuLine(EmberSidestepPath.AUTO));
        assertEquals("闪冲路径 · 闪冲·提醒", EmberSidestepPath.menuLine(EmberSidestepPath.ASK));
        assertEquals("闪冲路径 · 闪冲·静默", EmberSidestepPath.menuLine(EmberSidestepPath.MUTE));
        assertEquals("闪冲·自动", EmberSidestepPath.label(EmberSidestepPath.AUTO));
        assertEquals("闪冲·提醒", EmberSidestepPath.label(EmberSidestepPath.ASK));
        assertEquals("闪冲·静默", EmberSidestepPath.label(EmberSidestepPath.MUTE));
        assertEquals("未选", EmberSidestepPath.label(EmberSidestepPath.NONE));
        assertTrue(EmberSidestepPath.tip(EmberSidestepPath.AUTO).contains("自动"));
        assertTrue(EmberSidestepPath.tip(EmberSidestepPath.ASK).contains("提醒"));
        assertTrue(EmberSidestepPath.tip(EmberSidestepPath.MUTE).contains("静默") || EmberSidestepPath.tip(EmberSidestepPath.MUTE).contains("提醒"));
        assertEquals(EmberSidestepPath.AUTO, EmberSidestepPath.parse("auto"));
        assertEquals(EmberSidestepPath.ASK, EmberSidestepPath.parse("ask"));
        assertEquals(EmberSidestepPath.MUTE, EmberSidestepPath.parse("mute"));
        assertEquals(EmberSidestepPath.NONE, EmberSidestepPath.parse("clear"));
        assertTrue(EmberSidestepPath.shouldAuto(EmberSidestepPath.AUTO));
        assertTrue(EmberSidestepPath.shouldMute(EmberSidestepPath.MUTE));
        assertFalse(EmberSidestepPath.shouldAuto(EmberSidestepPath.ASK));
        assertFalse(EmberSidestepPath.shouldMute(EmberSidestepPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareSidesteppathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSidestepPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_sidestep_path"));
        assertTrue(src.contains("TIP-DP"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 sidesteppath"));
        assertFalse(offerBody.contains("sidesteppath auto"));
        assertFalse(offerBody.contains("sidesteppath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 sidesteppath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D572 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSidestepPath.java")),
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
    public void combatButtons_flashSidestepUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSidestepPath.java")),
                StandardCharsets.UTF_8);
        // 局内「闪冲」战斗钮语义零改
        assertTrue(src.contains("\"[闪冲]\", \"/corerpg p1 sidestep\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 sidesteppath\""));
        // auto/ask/mute 真改侧移语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathSidestepCharge"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inSidestepPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSidestepPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DP"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareSidesteppath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean sidestepPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean sidestepGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点闪冲路径页"));
        assertFalse(body.contains("用法：/corerpg p1 sidesteppath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点闪冲路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 sidesteppath"));
    }
}
