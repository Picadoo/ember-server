package town.sunshine.corerpg.p1;

import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

import static org.junit.Assert.*;

/**
 * TIP-BR D13192–D13207: MG2 enter-once menu honesty — PAPI {@code access_q01}/{@code access_q07}.
 * SoT = {@link EmberSkillKit#gemPathUnlocked} / {@link EmberSkillKit#endgameUnlocked} (enter∨firstClear).
 * Display only — zero unlock math edits. {@code q01done}/{@code q07done} stay GATE first-clear bits.
 */
public class EmberMapEnterUnlockAccessTest {

    /**
     * Offline clear branch: same predicate as {@code EmberRunService.firstClearedKey} →
     * {@link EmberFirstClear#fact} (production one-line delegate). Used when EmberRunService
     * cannot be constructed without a plugin.
     */
    private static boolean unlockedEnterOrClear(PlayerData d, String enterCounter, String mapKey) {
        if (d == null) return false;
        if (d.periodCount(enterCounter, "all") > 0) return true;
        return EmberFirstClear.fact(d, mapKey);
    }

    @Test
    public void neitherAccessQ01IsZero() {
        PlayerData d = new PlayerData();
        assertFalse(EmberSkillKit.gemPathUnlocked(d, null));
        assertEquals("0", EmberRunPapi.accessBit(EmberSkillKit.gemPathUnlocked(d, null)));
        assertEquals("0", EmberRunPapi.accessBit(unlockedEnterOrClear(d, EmberSkillKit.C_GEM_ENTER, "q01")));
    }

    @Test
    public void neitherAccessQ07IsZero() {
        PlayerData d = new PlayerData();
        assertFalse(EmberSkillKit.endgameUnlocked(d, null));
        assertFalse(EmberSkillKit.attuneUnlocked(d, null));
        assertEquals("0", EmberRunPapi.accessBit(EmberSkillKit.endgameUnlocked(d, null)));
        assertEquals("0", EmberRunPapi.accessBit(unlockedEnterOrClear(d, EmberSkillKit.C_ATTUNE_ENTER, EmberSignature.ALT_UNLOCK)));
    }

    @Test
    public void enterOnlyAccessQ01IsOne() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markGemEnter(d));
        assertFalse(EmberFirstClear.fact(d, "q01")); // clear-only fact off
        assertTrue(EmberSkillKit.gemPathUnlocked(d, null));
        assertEquals("1", EmberRunPapi.accessBit(EmberSkillKit.gemPathUnlocked(d, null)));
        assertEquals("1", EmberRunPapi.accessBit(unlockedEnterOrClear(d, EmberSkillKit.C_GEM_ENTER, "q01")));
    }

    @Test
    public void enterOnlyAccessQ07IsOne() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markAttuneEnter(d));
        assertFalse(EmberFirstClear.fact(d, EmberSignature.ALT_UNLOCK));
        assertTrue(EmberSkillKit.endgameUnlocked(d, null));
        assertEquals("1", EmberRunPapi.accessBit(EmberSkillKit.endgameUnlocked(d, null)));
        assertEquals("1", EmberRunPapi.accessBit(unlockedEnterOrClear(d, EmberSkillKit.C_ATTUNE_ENTER, EmberSignature.ALT_UNLOCK)));
    }

    @Test
    public void clearOnlyAccessQ01IsOne() {
        PlayerData d = new PlayerData();
        EmberFirstClear.setFact(d, "q01", true);
        assertEquals(0, d.periodCount(EmberSkillKit.C_GEM_ENTER, "all"));
        assertFalse(EmberSkillKit.gemPathUnlocked(d, null)); // runs==null hides clear; SoT still clear∨enter
        assertTrue(EmberFirstClear.fact(d, "q01"));
        assertTrue(unlockedEnterOrClear(d, EmberSkillKit.C_GEM_ENTER, "q01"));
        assertEquals("1", EmberRunPapi.accessBit(unlockedEnterOrClear(d, EmberSkillKit.C_GEM_ENTER, "q01")));
    }

    @Test
    public void clearOnlyAccessQ07IsOne() {
        PlayerData d = new PlayerData();
        EmberFirstClear.setFact(d, EmberSignature.ALT_UNLOCK, true);
        assertEquals(0, d.periodCount(EmberSkillKit.C_ATTUNE_ENTER, "all"));
        assertFalse(EmberSkillKit.endgameUnlocked(d, null));
        assertTrue(EmberFirstClear.fact(d, EmberSignature.ALT_UNLOCK));
        assertTrue(unlockedEnterOrClear(d, EmberSkillKit.C_ATTUNE_ENTER, EmberSignature.ALT_UNLOCK));
        assertEquals("1", EmberRunPapi.accessBit(unlockedEnterOrClear(d, EmberSkillKit.C_ATTUNE_ENTER, EmberSignature.ALT_UNLOCK)));
    }

    @Test
    public void papiRoutesAccessKeysToGate() {
        assertEquals(EmberRunPapi.Section.GATE, EmberRunPapi.route("access_q01"));
        assertEquals(EmberRunPapi.Section.GATE, EmberRunPapi.route("access_q07"));
        // q01done / q07done retained as first-clear menu bits
        assertEquals(EmberRunPapi.Section.GATE, EmberRunPapi.route("q01done"));
        assertEquals(EmberRunPapi.Section.GATE, EmberRunPapi.route("q07done"));
        assertTrue(EmberRunPapi.isQDone("q01done"));
        assertTrue(EmberRunPapi.isQDone("q07done"));
    }

    @Test
    public void firstClearedKeyStillDelegatesToFact() throws Exception {
        // pin SoT mirror used by clear-only offline tests
        String s = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRunService.java")),
                java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(s.contains("public boolean firstClearedKey(PlayerData d, String key)"));
        assertTrue(s.contains("return EmberFirstClear.fact(d, key);"));
        // gate wires SkillKit SoT (not a second unlock OR)
        String p = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRunPapi.java")),
                java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(p.contains("accessBit(EmberSkillKit.gemPathUnlocked(d, runs))"));
        assertTrue(p.contains("accessBit(EmberSkillKit.endgameUnlocked(d, runs))"));
        assertTrue(p.contains("if (\"q07done\".equals(key)) return runs.progressFlag(d, \"q07\")"));
    }
}
