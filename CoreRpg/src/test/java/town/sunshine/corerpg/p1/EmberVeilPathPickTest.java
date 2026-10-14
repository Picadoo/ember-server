package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DS D13350 W1a: VeilPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 veilpath 主 UI / 零改伤害CD掉落体力价/D573 毒十字避毒语义.
 */
public class EmberVeilPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareVeilpath() {
        assertEquals("/ember_p1_veil_path", EmberVeilPath.PATH_PICK_CMD);
        assertEquals("[避毒路径]", EmberVeilPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberVeilPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberVeilPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberVeilPath.PATH_PICK_CMD.contains("veilpath"));
        assertFalse(EmberVeilPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareVeilpath() {
        String locked = EmberVeilPath.glance(EmberVeilPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 veilpath"));
        String none = EmberVeilPath.glance(EmberVeilPath.NONE, true);
        assertTrue(none.contains("点避毒路径页"));
        assertFalse(none.contains("/corerpg p1 veilpath"));
        String auto = EmberVeilPath.glance(EmberVeilPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 veilpath"));
        String ask = EmberVeilPath.glance(EmberVeilPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 veilpath"));
        String mute = EmberVeilPath.glance(EmberVeilPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 veilpath"));
    }

    @Test
    public void menuLine_labelTipReuse_veilSemanticsUnchanged() {
        assertEquals("避毒路径 · 未选", EmberVeilPath.menuLine(EmberVeilPath.NONE));
        assertEquals("避毒路径 · 避毒·自动", EmberVeilPath.menuLine(EmberVeilPath.AUTO));
        assertEquals("避毒路径 · 避毒·提醒", EmberVeilPath.menuLine(EmberVeilPath.ASK));
        assertEquals("避毒路径 · 避毒·静默", EmberVeilPath.menuLine(EmberVeilPath.MUTE));
        assertEquals("避毒·自动", EmberVeilPath.label(EmberVeilPath.AUTO));
        assertEquals("避毒·提醒", EmberVeilPath.label(EmberVeilPath.ASK));
        assertEquals("避毒·静默", EmberVeilPath.label(EmberVeilPath.MUTE));
        assertEquals("未选", EmberVeilPath.label(EmberVeilPath.NONE));
        assertTrue(EmberVeilPath.tip(EmberVeilPath.AUTO).contains("自动"));
        assertTrue(EmberVeilPath.tip(EmberVeilPath.ASK).contains("提醒"));
        assertTrue(EmberVeilPath.tip(EmberVeilPath.MUTE).contains("静默") || EmberVeilPath.tip(EmberVeilPath.MUTE).contains("提醒"));
        assertEquals(EmberVeilPath.AUTO, EmberVeilPath.parse("auto"));
        assertEquals(EmberVeilPath.ASK, EmberVeilPath.parse("ask"));
        assertEquals(EmberVeilPath.MUTE, EmberVeilPath.parse("mute"));
        assertEquals(EmberVeilPath.NONE, EmberVeilPath.parse("clear"));
        assertTrue(EmberVeilPath.shouldAuto(EmberVeilPath.AUTO));
        assertTrue(EmberVeilPath.shouldMute(EmberVeilPath.MUTE));
        assertFalse(EmberVeilPath.shouldAuto(EmberVeilPath.ASK));
        assertFalse(EmberVeilPath.shouldMute(EmberVeilPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareVeilpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberVeilPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_veil_path"));
        assertTrue(src.contains("TIP-DS"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 veilpath"));
        assertFalse(offerBody.contains("veilpath auto"));
        assertFalse(offerBody.contains("veilpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 veilpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D573 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberVeilPath.java")),
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
    public void combatButtons_veilUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberVeilPath.java")),
                StandardCharsets.UTF_8);
        // 局内「避毒」战斗钮语义零改
        assertTrue(src.contains("\"[避毒]\", \"/corerpg p1 veil\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 veilpath\""));
        // auto/ask/mute 真改避毒语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathVeilVenom"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inVeilPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberVeilPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DS"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareVeilpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean veilPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean veilGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点避毒路径页"));
        assertFalse(body.contains("用法：/corerpg p1 veilpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点避毒路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 veilpath"));
    }
}
