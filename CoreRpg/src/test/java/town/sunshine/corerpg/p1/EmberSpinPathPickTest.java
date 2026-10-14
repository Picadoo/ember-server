package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DU D13352 W1a: SpinPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 spinpath 主 UI / 零改伤害CD掉落体力价/D575 旋光避旋语义.
 */
public class EmberSpinPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareSpinpath() {
        assertEquals("/ember_p1_spin_path", EmberSpinPath.PATH_PICK_CMD);
        assertEquals("[避旋路径]", EmberSpinPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberSpinPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberSpinPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberSpinPath.PATH_PICK_CMD.contains("spinpath"));
        assertFalse(EmberSpinPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareSpinpath() {
        String locked = EmberSpinPath.glance(EmberSpinPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 spinpath"));
        String none = EmberSpinPath.glance(EmberSpinPath.NONE, true);
        assertTrue(none.contains("点避旋路径页"));
        assertFalse(none.contains("/corerpg p1 spinpath"));
        String auto = EmberSpinPath.glance(EmberSpinPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 spinpath"));
        String ask = EmberSpinPath.glance(EmberSpinPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 spinpath"));
        String mute = EmberSpinPath.glance(EmberSpinPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 spinpath"));
    }

    @Test
    public void menuLine_labelTipReuse_spinSemanticsUnchanged() {
        assertEquals("避旋路径 · 未选", EmberSpinPath.menuLine(EmberSpinPath.NONE));
        assertEquals("避旋路径 · 避旋·自动", EmberSpinPath.menuLine(EmberSpinPath.AUTO));
        assertEquals("避旋路径 · 避旋·提醒", EmberSpinPath.menuLine(EmberSpinPath.ASK));
        assertEquals("避旋路径 · 避旋·静默", EmberSpinPath.menuLine(EmberSpinPath.MUTE));
        assertEquals("避旋·自动", EmberSpinPath.label(EmberSpinPath.AUTO));
        assertEquals("避旋·提醒", EmberSpinPath.label(EmberSpinPath.ASK));
        assertEquals("避旋·静默", EmberSpinPath.label(EmberSpinPath.MUTE));
        assertEquals("未选", EmberSpinPath.label(EmberSpinPath.NONE));
        assertTrue(EmberSpinPath.tip(EmberSpinPath.AUTO).contains("自动"));
        assertTrue(EmberSpinPath.tip(EmberSpinPath.ASK).contains("提醒"));
        assertTrue(EmberSpinPath.tip(EmberSpinPath.MUTE).contains("静默") || EmberSpinPath.tip(EmberSpinPath.MUTE).contains("提醒"));
        assertEquals(EmberSpinPath.AUTO, EmberSpinPath.parse("auto"));
        assertEquals(EmberSpinPath.ASK, EmberSpinPath.parse("ask"));
        assertEquals(EmberSpinPath.MUTE, EmberSpinPath.parse("mute"));
        assertEquals(EmberSpinPath.NONE, EmberSpinPath.parse("clear"));
        assertTrue(EmberSpinPath.shouldAuto(EmberSpinPath.AUTO));
        assertTrue(EmberSpinPath.shouldMute(EmberSpinPath.MUTE));
        assertFalse(EmberSpinPath.shouldAuto(EmberSpinPath.ASK));
        assertFalse(EmberSpinPath.shouldMute(EmberSpinPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareSpinpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSpinPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_spin_path"));
        assertTrue(src.contains("TIP-DU"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 spinpath"));
        assertFalse(offerBody.contains("spinpath auto"));
        assertFalse(offerBody.contains("spinpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 spinpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D575 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSpinPath.java")),
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
    public void combatButtons_spinUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSpinPath.java")),
                StandardCharsets.UTF_8);
        // 局内「避旋」战斗钮语义零改
        assertTrue(src.contains("\"[避旋]\", \"/corerpg p1 spin\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 spinpath\""));
        // auto/ask/mute 真改避旋语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathSpinOut"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inSpinPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSpinPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DU"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareSpinpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean spinPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean spinGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点避旋路径页"));
        assertFalse(body.contains("用法：/corerpg p1 spinpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点避旋路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 spinpath"));
    }
}
