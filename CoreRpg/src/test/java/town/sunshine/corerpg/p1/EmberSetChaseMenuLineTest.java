package town.sunshine.corerpg.p1;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

import static org.junit.Assert.*;

/** D12265/D12271: menuLine states — no focus / gap / wearing+awk incomplete / full. */
public class EmberSetChaseMenuLineTest {

    private final EmberTables t = EmberTables.defaults();

    private static EmberItemData blade(String fam, int tier, int enh) {
        return EmberItemData.create(fam, "blade", tier, 0, 0, enh, true, "admin");
    }

    private static EmberItemData charm(String fam, int tier, int enh) {
        return EmberItemData.create(fam, "charm", tier, 0, 0, enh, true, "admin");
    }

    private static PlayerData focus(int id) {
        PlayerData d = new PlayerData();
        if (id != EmberSetFocus.NONE) EmberSetFocus.set(d, id);
        return d;
    }

    @Test public void menuLine_nullSafeNoFocus() {
        assertEquals("§8套装焦点：未选 · 点右选焚烬/烬爆/炽愈", EmberSetChase.menuLine(null, null));
        assertEquals("§8套装焦点：未选 · 点右选焚烬/烬爆/炽愈",
                EmberSetChase.menuLine(new PlayerData(), EmberLoadout.compute(t, null, null, 10)));
    }

    @Test public void menuLine_gap_bladeOnly() {
        PlayerData d = focus(EmberSetFocus.SCORCH);
        EmberLoadout lo = EmberLoadout.compute(t, blade("scorch", 1, 0), null, 10);
        String line = EmberSetChase.menuLine(d, lo);
        assertTrue(line, line.startsWith("§e焦点§f焚烬"));
        assertTrue(line, line.contains("同族 1/2"));
        assertTrue(line, line.contains("还差同族护符"));
        assertFalse(line, line.contains("穿着"));
    }

    @Test public void menuLine_gap_wrongFamily() {
        PlayerData d = focus(EmberSetFocus.BURST);
        EmberLoadout lo = EmberLoadout.compute(t, blade("scorch", 2, 6), charm("scorch", 2, 6), 20);
        String line = EmberSetChase.menuLine(d, lo);
        assertTrue(line, line.contains("焦点§f烬爆"));
        assertTrue(line, line.contains("同族 0/2"));
        assertTrue(line, line.contains("还差"));
        assertTrue(line, line.contains("换烬爆") || line.contains("都不是烬爆"));
    }

    @Test public void menuLine_wearingAwakeningIncomplete() {
        PlayerData d = focus(EmberSetFocus.BURST);
        // T2+6 both → awakening II, next needs T3+9
        EmberLoadout lo = EmberLoadout.compute(t, blade("burst", 2, 6), charm("burst", 2, 6), 30);
        assertEquals("burst", lo.activeSet);
        assertEquals(2, lo.awakening);
        String line = EmberSetChase.menuLine(d, lo);
        assertTrue(line, line.contains("焦点§f烬爆"));
        assertTrue(line, line.contains("穿着"));
        assertTrue(line, line.contains("觉醒II"));
        assertTrue(line, line.contains("还差"));
        assertFalse(line, line.contains("觉醒III · 齐"));
    }

    @Test public void menuLine_fullAwakening() {
        PlayerData d = focus(EmberSetFocus.SUSTAIN);
        EmberLoadout lo = EmberLoadout.compute(t, blade("sustain", 3, 9), charm("sustain", 3, 9), 40);
        assertEquals(3, lo.awakening);
        String line = EmberSetChase.menuLine(d, lo);
        assertTrue(line, line.startsWith("§a焦点§f炽愈"));
        assertTrue(line, line.contains("觉醒III · 齐"));
        // codex incomplete → 还差N种 half; complete → 图鉴齐
        assertTrue(line, line.contains("图鉴齐") || line.contains("还差"));
    }

    @Test public void menuLine_fullWithCodexComplete() {
        PlayerData d = focus(EmberSetFocus.SCORCH);
        for (int tier = 1; tier <= 3; tier++) {
            for (String slot : new String[]{"blade", "charm"}) {
                EmberCodex.register(d, "scorch", slot, tier, "drop");
            }
        }
        EmberLoadout lo = EmberLoadout.compute(t, blade("scorch", 3, 9), charm("scorch", 3, 9), 40);
        String line = EmberSetChase.menuLine(d, lo);
        assertEquals("§a焦点§f焚烬 §a觉醒III · 齐 §8图鉴齐", line);
    }

    @Test public void compressAndGapHelpers() {
        assertEquals("满", EmberSetChase.compressAwakeningRemain("觉醒满"));
        assertEquals("刃 +6→+9", EmberSetChase.compressAwakeningRemain("觉醒III 缺：刃 +6→+9"));
        assertEquals("刃 T2→T3", EmberSetChase.compressAwakeningRemain("下一档 觉醒III 缺：刃 T2→T3；护符 +6→+9".replace("下一档 ", "")));
        PlayerData d = focus(EmberSetFocus.SCORCH);
        EmberLoadout lo = EmberLoadout.compute(t, blade("scorch", 1, 0), null, 10);
        assertEquals("同族护符", EmberSetChase.gapNeedWord(d, lo));
    }
}
