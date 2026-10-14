package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DJ D13341 W1a: TrialPath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 trialpath/pledgepath/counterpath 主 UI / 零改伤害CD掉落体力价.
 */
public class EmberTrialPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareTrialpathOrSettle() {
        assertEquals("/ember_p1_trial_path", EmberTrialPath.PATH_PICK_CMD);
        assertEquals("[试炼路径]", EmberTrialPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberTrialPath.PATH_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberTrialPath.PATH_FORGE_CMD);
        assertFalse(EmberTrialPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberTrialPath.PATH_PICK_CMD.contains("trialpath"));
        assertFalse(EmberTrialPath.PATH_PICK_CMD.contains("pledgepath"));
        assertFalse(EmberTrialPath.PATH_PICK_CMD.contains("counterpath"));
        assertFalse(EmberTrialPath.PATH_FORGE_CMD.contains("/corerpg p1 forge"));
    }

    @Test
    public void glance_noTeachBareTrialpath() {
        String locked = EmberTrialPath.glance(EmberTrialPath.NONE, false);
        assertTrue(locked.contains("Q06"));
        assertFalse(locked.contains("/corerpg p1 trialpath"));
        String none = EmberTrialPath.glance(EmberTrialPath.NONE, true);
        assertTrue(none.contains("点试炼路径页"));
        assertFalse(none.contains("/corerpg p1 trialpath"));
        String c = EmberTrialPath.glance(EmberTrialPath.CAUTIOUS, true);
        assertTrue(c.contains("稳慎"));
        assertFalse(c.contains("/corerpg p1 trialpath"));
        String a = EmberTrialPath.glance(EmberTrialPath.AGGRESSIVE, true);
        assertTrue(a.contains("猛开"));
        assertFalse(a.contains("/corerpg p1 trialpath"));
        String pr = EmberTrialPath.glance(EmberTrialPath.PRESSURE, true);
        assertTrue(pr.contains("加压"));
        assertFalse(pr.contains("/corerpg p1 trialpath"));
    }

    @Test
    public void menuLine_labelTipReuse() {
        assertEquals("试炼路径 · 未选", EmberTrialPath.menuLine(EmberTrialPath.NONE));
        assertEquals("试炼路径 · 稳慎试炼", EmberTrialPath.menuLine(EmberTrialPath.CAUTIOUS));
        assertEquals("试炼路径 · 猛开试炼", EmberTrialPath.menuLine(EmberTrialPath.AGGRESSIVE));
        assertEquals("试炼路径 · 加压试炼", EmberTrialPath.menuLine(EmberTrialPath.PRESSURE));
        assertEquals("稳慎试炼", EmberTrialPath.label(EmberTrialPath.CAUTIOUS));
        assertEquals("猛开试炼", EmberTrialPath.label(EmberTrialPath.AGGRESSIVE));
        assertEquals("加压试炼", EmberTrialPath.label(EmberTrialPath.PRESSURE));
        assertEquals("未选", EmberTrialPath.label(EmberTrialPath.NONE));
        assertTrue(EmberTrialPath.tip(EmberTrialPath.CAUTIOUS).contains("限药"));
        assertTrue(EmberTrialPath.tip(EmberTrialPath.AGGRESSIVE).contains("逆行"));
        assertTrue(EmberTrialPath.tip(EmberTrialPath.PRESSURE).contains("破招"));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareTrialPledgeCounterMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberTrialPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_trial_path"));
        assertTrue(src.contains("TIP-DJ"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 trialpath"));
        assertFalse(offerBody.contains("trialpath cautious"));
        assertFalse(offerBody.contains("trialpath aggressive"));
        assertFalse(offerBody.contains("trialpath pressure"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD") || applyBody.contains("PATH_FORGE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 trialpath"));
        assertFalse(applyBody.contains("\"/corerpg p1 pledgepath"));
        assertFalse(applyBody.contains("\"/corerpg p1 counterpath"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberTrialPath.java")),
                StandardCharsets.UTF_8);
        int weekly = src.indexOf("public static boolean maybeOfferWeekly");
        assertTrue(weekly > 0);
        String weeklyBody = src.substring(weekly, src.indexOf("public static void scheduleOfferAfterQ06", weekly));
        assertTrue(weeklyBody.contains("offerPick(p)"));
        int sched = src.indexOf("public static void scheduleOfferAfterQ06");
        assertTrue(sched > 0);
        String schedBody = src.substring(sched, src.indexOf("public static void applyAndReply", sched));
        assertTrue(schedBody.contains("maybeOfferWeekly"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inTrialPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberTrialPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DJ"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }
}
