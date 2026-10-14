package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DF D13337 W1a: ChallengePath.offerPick / applyAndReply / glance / enterCue 选打菜单化 —
 * 开薄页 / 禁借 settle ConfirmTokens 当选打主 UI / 零改掉落体力价硬本语义.
 */
public class EmberChallengePathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareChallengepathOrSettle() {
        assertEquals("/ember_p1_challenge_path", EmberChallengePath.PATH_PICK_CMD);
        assertEquals("[硬本路径]", EmberChallengePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberChallengePath.PATH_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberChallengePath.PATH_FORGE_CMD);
        assertEquals("/ember_p1_challenge", EmberChallengePath.PATH_CHALLENGE_CMD);
        assertFalse(EmberChallengePath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberChallengePath.PATH_PICK_CMD.contains("challengepath"));
        assertFalse(EmberChallengePath.PATH_PICK_CMD.contains("challenge_settle"));
        assertFalse(EmberChallengePath.PATH_ADVENTURE_CMD.contains("/corerpg p1 runs"));
        assertFalse(EmberChallengePath.PATH_FORGE_CMD.contains("/corerpg p1 forge"));
    }

    @Test
    public void glance_noTeachBareChallengepathOrSettleNextLoop() {
        String locked = EmberChallengePath.glance(EmberChallengePath.NONE, false);
        assertTrue(locked.contains("Q07"));
        assertFalse(locked.contains("/corerpg p1 challengepath"));
        String none = EmberChallengePath.glance(EmberChallengePath.NONE, true);
        assertTrue(none.contains("点硬本路径页"));
        assertFalse(none.contains("/corerpg p1 challengepath"));
        assertFalse(none.contains("点下一环"));
        String prefer = EmberChallengePath.glance(EmberChallengePath.PREFER, true);
        assertTrue(prefer.contains("偏挑战"));
        assertFalse(prefer.contains("/corerpg p1 challengepath"));
    }

    @Test
    public void menuLine_labelTipReuse() {
        assertEquals("硬本路径 · 未选", EmberChallengePath.menuLine(EmberChallengePath.NONE));
        assertEquals("硬本路径 · 硬本·偏挑战", EmberChallengePath.menuLine(EmberChallengePath.PREFER));
        assertEquals("硬本路径 · 硬本·偏普通", EmberChallengePath.menuLine(EmberChallengePath.EASY));
        assertEquals("硬本路径 · 硬本·随令", EmberChallengePath.menuLine(EmberChallengePath.FOLLOW));
        assertEquals("硬本·偏挑战", EmberChallengePath.label(EmberChallengePath.PREFER));
        assertEquals("硬本·偏普通", EmberChallengePath.label(EmberChallengePath.EASY));
        assertEquals("硬本·随令", EmberChallengePath.label(EmberChallengePath.FOLLOW));
        assertEquals("未选", EmberChallengePath.label(EmberChallengePath.NONE));
        assertTrue(EmberChallengePath.tip(EmberChallengePath.PREFER).contains("挑战"));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBorrowSettleConfirmTokens() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberChallengePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_challenge_path"));
        assertTrue(src.contains("TIP-DF"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("SETTLE_NEXT_LOOP_CMD"));
        assertFalse(offerBody.contains("ember_p1_challenge_settle"));
        assertFalse(offerBody.contains("\"/corerpg p1 challengepath"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("/** D767/D788: session enter", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("SETTLE_NEXT_LOOP_CMD"));
        assertFalse(applyBody.contains("ember_p1_challenge_settle"));
        assertFalse(applyBody.contains("\"/corerpg p1 challengepath"));

        int cue = src.indexOf("public static void enterCue(");
        assertTrue(cue > 0);
        int cueEnd = src.indexOf("public static void afterSettle", cue);
        assertTrue(cueEnd > cue);
        String cueBody = src.substring(cue, cueEnd);
        assertTrue(cueBody.contains("点硬本路径页"));
        assertFalse(cueBody.contains("/corerpg p1 challengepath"));
        assertFalse(cueBody.contains("点下一环选路径"));
    }

    @Test
    public void afterSettle_stillUsesSettleNextLoop_zeroChangeSettle() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberChallengePath.java")),
                StandardCharsets.UTF_8);
        int after = src.indexOf("public static void afterSettle(");
        assertTrue(after > 0);
        String afterBody = src.substring(after);
        assertTrue(afterBody.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(afterBody.contains("ember_p1_challenge_settle")
                || afterBody.contains("SETTLE_NEXT_LOOP"));
    }

    @Test
    public void dropStaminaUntouched_inChallengePath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberChallengePath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("lootFamily ="));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DF"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }
}
