package town.sunshine.corerpg.p1;

import org.junit.After;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * D325 Stage2 档 C：四件套触发边界 / 掉落阶 vs 跟随阶 / 不进 Formula / 开关关=无减伤。
 */
public class EmberSixSetBonusTest {

    static final EmberTables T = EmberTables.defaults();

    @After public void reset() {
        EmberSixSlot.testEnabled = null;
        EmberSixSlot.testMigrate = null;
        EmberSixSlot.testSetBonus = null;
    }

    static EmberItemData piece(String fam, String slot, int tier, int q, int f, int e) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId(tier == 0 ? "none" : fam, slot, tier),
                tier == 0 ? "none" : fam, slot, tier, q, f, e, 0, true, "drop", EmberItemData.DATA_VERSION, 0);
    }

    static EmberItemData[] worn(EmberItemData... a) {
        EmberItemData[] w = new EmberItemData[4];
        for (int i = 0; i < a.length && i < 4; i++) w[i] = a[i];
        return w;
    }

    @Test public void setBonusSwitchDefaultsOffAndNeedsMaster() {
        assertFalse(EmberSixSlot.setBonusEnabled());
        EmberSixSlot.testSetBonus = true;
        assertFalse("set_bonus alone never runs without master", EmberSixSlot.setBonusEnabled());
        EmberSixSlot.testEnabled = true;
        EmberSixSlot.testSetBonus = true;
        assertTrue(EmberSixSlot.setBonusEnabled());
        EmberSixSlot.testSetBonus = false;
        assertFalse(EmberSixSlot.setBonusEnabled());
    }

    @Test public void triggerNeedsTwoPiecePlusTwoSameFamilyDropTierT2() {
        EmberItemData blade = piece("scorch", "blade", 2, 1, 1, 3);
        EmberItemData charm = piece("scorch", "charm", 2, 1, 1, 3);
        // 0 qualifying
        Object[] s0 = EmberSixRank.setProgress(blade, charm, worn());
        assertEquals("scorch", s0[0]);
        assertEquals(0, s0[1]);
        assertEquals(2, s0[2]);
        assertEquals(false, s0[3]);
        // 1× T2 same family → not active
        Object[] s1 = EmberSixRank.setProgress(blade, charm, worn(piece("scorch", "head", 2, 3, 3, 0)));
        assertEquals(1, s1[1]);
        assertEquals(false, s1[3]);
        // 2× T2 same family → active
        Object[] s2 = EmberSixRank.setProgress(blade, charm, worn(
                piece("scorch", "head", 2, 0, 0, 0),
                piece("scorch", "chest", 2, 0, 0, 0)));
        assertEquals(2, s2[1]);
        assertEquals(true, s2[3]);
        // other 2 slots can be different family / high quality — still active
        Object[] s3 = EmberSixRank.setProgress(blade, charm, worn(
                piece("scorch", "head", 2, 0, 0, 0),
                piece("burst", "chest", 3, 3, 3, 0),
                piece("sustain", "legs", 3, 3, 3, 0),
                piece("scorch", "boots", 3, 0, 0, 0)));
        assertEquals(2, s3[1]);
        assertEquals(true, s3[3]);
    }

    @Test public void noTwoPieceMeansInactiveEvenWithFourSameFamilyArmor() {
        EmberItemData blade = piece("scorch", "blade", 3, 1, 1, 9);
        EmberItemData charm = piece("burst", "charm", 3, 1, 1, 9); // different family
        EmberItemData[] all = worn(
                piece("scorch", "head", 3, 3, 3, 0),
                piece("scorch", "chest", 3, 3, 3, 0),
                piece("scorch", "legs", 3, 3, 3, 0),
                piece("scorch", "boots", 3, 3, 3, 0));
        Object[] s = EmberSixRank.setProgress(blade, charm, all);
        assertEquals("", s[0]);
        assertEquals(0, s[1]);
        assertEquals(false, s[3]);
        assertEquals(1.0, EmberSixRank.setBonusTakenMult(true, blade, charm, all), 0);
    }

    /** G-S1: follow tier T3 but drop tier T1 must NOT count */
    @Test public void dropTierOnly_followEnhanceIgnored() {
        EmberItemData blade = piece("burst", "blade", 3, 2, 2, 9);
        EmberItemData charm = piece("burst", "charm", 3, 2, 2, 9);
        // drop tier 1, but enhance 9 (would be "follow T3" in F) — still does not qualify
        EmberItemData lowDrop = piece("burst", "head", 1, 3, 3, 9);
        EmberItemData t2 = piece("burst", "chest", 2, 0, 0, 0);
        Object[] s = EmberSixRank.setProgress(blade, charm, worn(lowDrop, t2));
        assertEquals(1, s[1]);
        assertEquals(false, s[3]);
        // T0 / empty / none family ignored
        Object[] s2 = EmberSixRank.setProgress(blade, charm, worn(
                piece("none", "head", 0, 0, 0, 0),
                null,
                piece("burst", "legs", 2, 1, 1, 0),
                piece("burst", "boots", 2, 1, 1, 0)));
        assertEquals(2, s2[1]);
        assertEquals(true, s2[3]);
    }

    @Test public void takenMultIs097OnlyWhenActiveAndSwitchOn() {
        EmberItemData blade = piece("sustain", "blade", 2, 1, 1, 0);
        EmberItemData charm = piece("sustain", "charm", 2, 1, 1, 0);
        EmberItemData[] arm = worn(piece("sustain", "head", 2, 0, 0, 0), piece("sustain", "chest", 2, 0, 0, 0));
        assertEquals(1.0, EmberSixRank.setBonusTakenMult(false, blade, charm, arm), 0);
        assertEquals(0.97, EmberSixRank.setBonusTakenMult(true, blade, charm, arm), 0);
        assertEquals(97.0, EmberSixRank.applySetBonusTaken(true, blade, charm, arm, 100.0), 1e-9);
        // inactive
        assertEquals(1.0, EmberSixRank.setBonusTakenMult(true, blade, charm, worn(piece("sustain", "head", 2, 0, 0, 0))), 0);
        assertEquals(100.0, EmberSixRank.applySetBonusTaken(true, blade, charm, worn(), 100.0), 0);
    }

    /** G-S2 / G-S6: Formula B/H/D/M unchanged by set-bonus (runtime-only) */
    @Test public void setBonusDoesNotEnterFormula() {
        EmberItemData blade = piece("scorch", "blade", 2, 2, 2, 5);
        EmberItemData charm = piece("scorch", "charm", 2, 2, 2, 5);
        EmberItemData[] arm = worn(
                piece("scorch", "head", 2, 1, 1, 0),
                piece("scorch", "chest", 2, 1, 1, 0),
                piece("burst", "legs", 3, 3, 3, 0),
                piece("sustain", "boots", 3, 3, 3, 0));
        EmberLoadout l = EmberLoadout.compute(T, blade, charm, 25, 0, 0, arm);
        EmberLoadout two = EmberLoadout.compute(T, blade, charm, 25, 0, 0);
        // B/D/M identical to 2-slot; H may differ from armor q/f — but set-bonus flag is not an input
        assertEquals(Double.doubleToLongBits(two.b), Double.doubleToLongBits(l.b));
        assertEquals(Double.doubleToLongBits(two.d), Double.doubleToLongBits(l.d));
        assertEquals(Double.doubleToLongBits(two.m), Double.doubleToLongBits(l.m));
        // activating set bonus must not change any loadout field (pure runtime mult)
        assertTrue(Boolean.TRUE.equals(EmberSixRank.setProgress(blade, charm, arm)[3]));
        EmberLoadout again = EmberLoadout.compute(T, blade, charm, 25, 0, 0, arm);
        assertEquals(Double.doubleToLongBits(l.b), Double.doubleToLongBits(again.b));
        assertEquals(Double.doubleToLongBits(l.h0), Double.doubleToLongBits(again.h0));
        assertEquals(Double.doubleToLongBits(l.h), Double.doubleToLongBits(again.h));
        assertEquals(Double.doubleToLongBits(l.d), Double.doubleToLongBits(again.d));
        assertEquals(Double.doubleToLongBits(l.m), Double.doubleToLongBits(again.m));
    }

    @Test public void papiStateMachineAndFamProgressLines() {
        EmberItemData blade = piece("scorch", "blade", 2, 2, 2, 5);
        EmberItemData charm = piece("scorch", "charm", 2, 2, 2, 5);
        // in progress: 1 qualifying
        EmberItemData[] worn1 = worn(piece("scorch", "head", 2, 2, 2, 0), piece("burst", "chest", 2, 3, 3, 0));
        EmberSixRank.View v1 = EmberSixRank.view(T, blade, charm, 25, 0, 0, worn1,
                Arrays.asList(piece("scorch", "chest", 2, 0, 0, 0)));
        assertEquals("0", EmberSixPapi.text(true, v1, 0, "armor_set_active"));
        assertEquals("1", EmberSixPapi.text(true, v1, 0, "armor_set_busy"));
        assertEquals("§e焚烬族 护甲 1/2 · 需同族掉落阶 T2+", EmberSixPapi.text(true, v1, 0, "armor_set"));
        assertEquals("§e四件套 护甲 1/2 → 2/2（可激活）", EmberSixPapi.text(true, v1, 0, "armor_chest_fam"));
        assertTrue(EmberSixPapi.setReply(v1).startsWith("四件套进行中：焚烬族护甲 1/2"));

        // active: 2 qualifying; swap chest to burst would break
        EmberItemData[] worn2 = worn(piece("scorch", "head", 2, 2, 2, 0), piece("scorch", "chest", 2, 0, 0, 0));
        EmberSixRank.View v2 = EmberSixRank.view(T, blade, charm, 25, 0, 0, worn2,
                Arrays.asList(piece("burst", "chest", 3, 3, 3, 0)));
        assertEquals("1", EmberSixPapi.text(true, v2, 0, "armor_set_active"));
        assertEquals("0", EmberSixPapi.text(true, v2, 0, "armor_set_busy"));
        assertEquals("§a焚烬族 护甲 2/2 · 受伤 −3%", EmberSixPapi.text(true, v2, 0, "armor_set"));
        assertEquals("§c四件套将中断（护甲 2/2 → 1/2）", EmberSixPapi.text(true, v2, 0, "armor_chest_fam"));
        assertTrue(EmberSixPapi.setReply(v2).contains("受伤略减（−3%）"));

        // no two-piece
        EmberSixRank.View v0 = EmberSixRank.view(T, null, charm, 25, 0, 0, worn2, Collections.<EmberItemData>emptyList());
        assertEquals("0", EmberSixPapi.text(true, v0, 0, "armor_set_active"));
        assertEquals("0", EmberSixPapi.text(true, v0, 0, "armor_set_busy"));
        assertEquals("§7先让刃与护符同族", EmberSixPapi.text(true, v0, 0, "armor_set"));
        assertEquals("四件套未激活：先让刃与护符同族。", EmberSixPapi.setReply(v0));
        // switch off blanks new keys too
        assertEquals("", EmberSixPapi.text(false, v2, 0, "armor_set_active"));
        assertEquals("", EmberSixPapi.text(false, v2, 0, "armor_set_busy"));
    }

    @Test public void singleHookLivesInCombatListenerOnly() throws Exception {
        String src = new String(Files.readAllBytes(Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCombatListener.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("EmberSixRank.setBonusTakenMult"));
        assertTrue(src.contains("D325 四件套受伤"));
        // no second EventHandler dedicated to set bonus elsewhere in p1
        String six = new String(Files.readAllBytes(Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSixSlotService.java")),
                StandardCharsets.UTF_8);
        assertFalse(six.contains("setBonusTakenMult"));
        String formula = new String(Files.readAllBytes(Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFormula.java")),
                StandardCharsets.UTF_8);
        assertFalse(formula.contains("SET_BONUS") || formula.contains("0.97"));
    }

    @Test public void armorLoreMentionsDropTierNotLaterOpen() {
        EmberItemData d = piece("scorch", "chest", 2, 1, 1, 0);
        String joined = EmberSixSlot.armorLore(d).toString();
        assertTrue(joined, joined.contains("四件套看这一行"));
        assertFalse(joined, joined.contains("后续开放"));
    }
}
