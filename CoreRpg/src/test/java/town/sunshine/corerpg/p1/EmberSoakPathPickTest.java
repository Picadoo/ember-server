package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-ED D13361 W1a: SoakPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 soakpath 主 UI / 零改伤害CD掉落体力价/D584 分摊语义.
 */
public class EmberSoakPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareSoakpath() {
        assertEquals("/ember_p1_soak_path", EmberSoakPath.PATH_PICK_CMD);
        assertEquals("[分摊路径]", EmberSoakPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberSoakPath.PATH_ADVENTURE_CMD);
        assertFalse(EmberSoakPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberSoakPath.PATH_PICK_CMD.contains("soakpath"));
        assertFalse(EmberSoakPath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareSoakpath() {
        String locked = EmberSoakPath.glance(EmberSoakPath.NONE, false);
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 soakpath"));
        String none = EmberSoakPath.glance(EmberSoakPath.NONE, true);
        assertTrue(none.contains("点分摊路径页"));
        assertFalse(none.contains("/corerpg p1 soakpath"));
        String auto = EmberSoakPath.glance(EmberSoakPath.AUTO, true);
        assertTrue(auto.contains("自动"));
        assertFalse(auto.contains("/corerpg p1 soakpath"));
        String ask = EmberSoakPath.glance(EmberSoakPath.ASK, true);
        assertTrue(ask.contains("提醒"));
        assertFalse(ask.contains("/corerpg p1 soakpath"));
        String mute = EmberSoakPath.glance(EmberSoakPath.MUTE, true);
        assertTrue(mute.contains("静默"));
        assertFalse(mute.contains("/corerpg p1 soakpath"));
    }

    @Test
    public void menuLine_labelTipReuse_soakSemanticsUnchanged() {
        assertEquals("分摊路径 · 未选", EmberSoakPath.menuLine(EmberSoakPath.NONE));
        assertEquals("分摊路径 · 分摊·自动", EmberSoakPath.menuLine(EmberSoakPath.AUTO));
        assertEquals("分摊路径 · 分摊·提醒", EmberSoakPath.menuLine(EmberSoakPath.ASK));
        assertEquals("分摊路径 · 分摊·静默", EmberSoakPath.menuLine(EmberSoakPath.MUTE));
        assertEquals("分摊·自动", EmberSoakPath.label(EmberSoakPath.AUTO));
        assertEquals("分摊·提醒", EmberSoakPath.label(EmberSoakPath.ASK));
        assertEquals("分摊·静默", EmberSoakPath.label(EmberSoakPath.MUTE));
        assertEquals("未选", EmberSoakPath.label(EmberSoakPath.NONE));
        assertTrue(EmberSoakPath.tip(EmberSoakPath.AUTO).contains("自动") || EmberSoakPath.tip(EmberSoakPath.AUTO).contains("分摊"));
        assertTrue(EmberSoakPath.tip(EmberSoakPath.ASK).contains("提醒"));
        assertTrue(EmberSoakPath.tip(EmberSoakPath.MUTE).contains("静默") || EmberSoakPath.tip(EmberSoakPath.MUTE).contains("提醒"));
        assertEquals(EmberSoakPath.AUTO, EmberSoakPath.parse("auto"));
        assertEquals(EmberSoakPath.ASK, EmberSoakPath.parse("ask"));
        assertEquals(EmberSoakPath.MUTE, EmberSoakPath.parse("mute"));
        assertEquals(EmberSoakPath.NONE, EmberSoakPath.parse("clear"));
        assertTrue(EmberSoakPath.shouldAuto(EmberSoakPath.AUTO));
        assertTrue(EmberSoakPath.shouldMute(EmberSoakPath.MUTE));
        assertFalse(EmberSoakPath.shouldAuto(EmberSoakPath.ASK));
        assertFalse(EmberSoakPath.shouldMute(EmberSoakPath.ASK));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareSoakpathMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSoakPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_soak_path"));
        assertTrue(src.contains("TIP-ED"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 soakpath"));
        assertFalse(offerBody.contains("soakpath auto"));
        assertFalse(offerBody.contains("soakpath ask"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 soakpath"));
        assertFalse(applyBody.contains("\"/corerpg p1\""));
        // D584 auto/ask/mute semantics still present
        assertTrue(applyBody.contains("AUTO") || applyBody.contains("maybeProbe"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSoakPath.java")),
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
    public void combatButtons_soakUnchanged_pathPickOpensThin() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSoakPath.java")),
                StandardCharsets.UTF_8);
        // 局内「进圈」战斗钮语义零改
        assertTrue(src.contains("\"[进圈]\", \"/corerpg p1 soak\""));
        // 「改路径」随 offerPick 开薄页
        assertTrue(src.contains("\"[改路径]\", PATH_PICK_CMD"));
        assertFalse(src.contains("\"[改路径]\", \"/corerpg p1 soakpath\""));
        // auto/ask/mute 真改分摊语义零改
        assertTrue(src.contains("shouldAuto"));
        assertTrue(src.contains("shouldMute"));
        assertTrue(src.contains("pathSoakIn"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inSoakPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSoakPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-ED"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareSoakpath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("private boolean soakPathCmd");
        assertTrue(block > 0);
        int end = src.indexOf("private boolean soakGoCmd", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点分摊路径页"));
        assertFalse(body.contains("用法：/corerpg p1 soakpath <auto|ask|mute|clear>"));
        assertTrue(body.contains("用法：点分摊路径页选自动/提醒/静默/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 soakpath"));
    }
}
