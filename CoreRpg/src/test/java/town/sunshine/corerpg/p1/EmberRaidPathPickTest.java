package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DG D13338 W1a: RaidPath.offerPick / applyAndReply / glance / enterCue 选打菜单化 —
 * 开薄页 / 禁借 settle ConfirmTokens 当选打主 UI / 零改掉落体力价周帽.
 */
public class EmberRaidPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareRaidpathOrSettle() {
        assertEquals("/ember_p1_raid_path", EmberRaidPath.PATH_PICK_CMD);
        assertEquals("[团本路径]", EmberRaidPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberRaidPath.PATH_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberRaidPath.PATH_FORGE_CMD);
        assertEquals("/ember_raid", EmberRaidPath.PATH_RAID_CMD);
        assertFalse(EmberRaidPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberRaidPath.PATH_PICK_CMD.contains("raidpath"));
        assertFalse(EmberRaidPath.PATH_PICK_CMD.contains("raid_settle"));
        assertFalse(EmberRaidPath.PATH_ADVENTURE_CMD.contains("/corerpg p1 runs"));
        assertFalse(EmberRaidPath.PATH_FORGE_CMD.contains("/corerpg p1 forge"));
    }

    @Test
    public void glance_noTeachBareRaidpathOrSettleNextLoop() {
        String locked = EmberRaidPath.glance(EmberRaidPath.NONE, false);
        assertTrue(locked.contains("Q07"));
        assertFalse(locked.contains("/corerpg p1 raidpath"));
        String none = EmberRaidPath.glance(EmberRaidPath.NONE, true);
        assertTrue(none.contains("点团本路径页"));
        assertFalse(none.contains("/corerpg p1 raidpath"));
        assertFalse(none.contains("点下一环"));
        String r01 = EmberRaidPath.glance(EmberRaidPath.R01, true);
        assertTrue(r01.contains("R01"));
        assertFalse(r01.contains("/corerpg p1 raidpath"));
    }

    @Test
    public void menuLine_labelTipReuse() {
        assertEquals("团本路径 · 未选", EmberRaidPath.menuLine(EmberRaidPath.NONE));
        assertEquals("团本路径 · R01 锈轨矿道", EmberRaidPath.menuLine(EmberRaidPath.R01));
        assertEquals("团本路径 · R02 霜封哨所", EmberRaidPath.menuLine(EmberRaidPath.R02));
        assertEquals("团本路径 · R03 断塔回廊", EmberRaidPath.menuLine(EmberRaidPath.R03));
        assertEquals("R01 锈轨矿道", EmberRaidPath.label(EmberRaidPath.R01));
        assertEquals("未选", EmberRaidPath.label(EmberRaidPath.NONE));
        assertTrue(EmberRaidPath.tip(EmberRaidPath.R01).contains("撞墙"));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBorrowSettleConfirmTokens() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRaidPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_raid_path"));
        assertTrue(src.contains("TIP-DG"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("SETTLE_NEXT_LOOP_CMD"));
        assertFalse(offerBody.contains("ember_p1_raid_settle"));
        assertFalse(offerBody.contains("\"/corerpg p1 raidpath"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("/** D752: enter/settle path clarity.", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_FORGE_CMD") || applyBody.contains("enterCmd"));
        assertFalse(applyBody.contains("SETTLE_NEXT_LOOP_CMD"));
        assertFalse(applyBody.contains("ember_p1_raid_settle"));
        assertFalse(applyBody.contains("\"/corerpg p1 raidpath"));

        int cue = src.indexOf("public static void enterCue(");
        assertTrue(cue > 0);
        int cueEnd = src.indexOf("public static void afterClearHit", cue);
        assertTrue(cueEnd > cue);
        String cueBody = src.substring(cue, cueEnd);
        assertTrue(cueBody.contains("点团本路径页"));
        assertFalse(cueBody.contains("/corerpg p1 raidpath"));
        assertFalse(cueBody.contains("点下一环选路径"));
    }

    @Test
    public void afterClearHit_stillUsesSettleNextLoop_zeroChangeSettle() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRaidPath.java")),
                StandardCharsets.UTF_8);
        int after = src.indexOf("public static void afterClearHit(");
        assertTrue(after > 0);
        String afterBody = src.substring(after);
        assertTrue(afterBody.contains("SETTLE_NEXT_LOOP_CMD"));
        assertTrue(afterBody.contains("ember_p1_raid_settle")
                || afterBody.contains("SETTLE_NEXT_LOOP"));
    }

    @Test
    public void dropStaminaWeeklyCapUntouched_inRaidPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRaidPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("lootFamily ="));
        assertFalse(src.contains("weeklyCap = 4") || src.contains("weeklyCap=4"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DG"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }
}
