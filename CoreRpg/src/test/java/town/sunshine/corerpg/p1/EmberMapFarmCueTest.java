package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import town.sunshine.corerpg.PlayerData;

import static org.junit.Assert.*;

public class EmberMapFarmCueTest {
    @Test public void nextLineNamesChase() {
        String a = EmberMapFarmCue.nextLine("scorch", "blade", null, null, 0, 0);
        assertTrue(a.contains("刃"));
        String b = EmberMapFarmCue.nextLine("burst", "charm", "burst", "护符", 2, 5);
        assertTrue(b.contains("同族"));
        String c = EmberMapFarmCue.nextLine("sustain", "blade", "scorch", "刃", 1, 3);
        assertTrue(c.contains("现穿"));
    }

    /** D12339: q07 lootSlot=charm; q01–q06 matrix unchanged. */
    @Test public void q07CharmBias_q01toQ06Unchanged() {
        EmberRunMaps m = bundled();
        assertEquals("scorch", m.byKey("q01").lootFamily);
        assertEquals("blade", m.byKey("q01").lootSlot);
        assertEquals("burst", m.byKey("q02").lootFamily);
        assertEquals("charm", m.byKey("q02").lootSlot);
        assertEquals("sustain", m.byKey("q03").lootFamily);
        assertEquals("blade", m.byKey("q03").lootSlot);
        assertEquals("scorch", m.byKey("q04").lootFamily);
        assertEquals("charm", m.byKey("q04").lootSlot);
        assertEquals("burst", m.byKey("q05").lootFamily);
        assertEquals("blade", m.byKey("q05").lootSlot); // D12341: keep blade (L09 main)
        assertEquals("sustain", m.byKey("q06").lootFamily);
        assertEquals("charm", m.byKey("q06").lootSlot);
        EmberRunMaps.MapDef q7 = m.byKey("q07");
        assertEquals("burst", q7.lootFamily);
        assertEquals("charm", q7.lootSlot);
        String label = EmberRunMaps.lootLabel(q7);
        assertTrue(label.contains("护符"));
        assertFalse(label.contains("部位不偏"));
        // raid still no slot
        assertNull(m.raids.get("r01").lootSlot);
    }

    /** D12340: prefer sig next-ring only when identity 齐 ∧ (chase ∨ marks under need). */
    @Test public void preferSigNextRing_rules() {
        EmberRunMaps.MapDef q01 = bundled().byKey("q01");
        PlayerData empty = new PlayerData();
        assertFalse(EmberMapFarmCue.preferSigNextRing(q01, empty));
        assertFalse(EmberMapFarmCue.preferSigNextRing(q01, null));
        assertFalse(EmberMapFarmCue.preferSigNextRing(null, empty));

        PlayerData incomplete = new PlayerData();
        EmberCodex.register(incomplete, "scorch", "blade", 1, "drop");
        // T1 needs blade+charm → still gap
        assertFalse(EmberMapFarmCue.preferSigNextRing(q01, incomplete));

        PlayerData complete = new PlayerData();
        fillIdentity(complete, q01);
        assertTrue(EmberMapIdentity.menuLine(q01, complete).contains("图鉴齐"));
        // complete but no chase / no marks → false
        assertFalse(EmberMapFarmCue.preferSigNextRing(q01, complete));

        // complete + chase → true
        EmberSigChase.set(complete, EmberSignature.byId("L01"));
        assertTrue(EmberMapFarmCue.preferSigNextRing(q01, complete));

        // complete + map marks∈[1, need) no chase → true
        PlayerData marksOnly = new PlayerData();
        fillIdentity(marksOnly, q01);
        marksOnly.addPeriodCount(EmberSignature.C_MARK + "q01", "all", 2);
        assertTrue(EmberMapFarmCue.preferSigNextRing(q01, marksOnly));

        // marks at imprint need (full) without chase → false (未满烙 only)
        PlayerData fullMarks = new PlayerData();
        fillIdentity(fullMarks, q01);
        fullMarks.addPeriodCount(EmberSignature.C_MARK + "q01", "all", EmberSignature.imprintMarks());
        assertFalse(EmberMapFarmCue.preferSigNextRing(q01, fullMarks));
    }

    private static void fillIdentity(PlayerData d, EmberRunMaps.MapDef m) {
        String fam = m.lootFamily;
        String dl = m.dropLabel == null ? "T1" : m.dropLabel.trim().toUpperCase();
        int tier = 1;
        if (dl.startsWith("T") && dl.length() >= 2) {
            try { tier = Integer.parseInt(dl.substring(1, 2)); } catch (NumberFormatException ignored) {}
        }
        for (int t = 1; t <= tier; t++) {
            for (String slot : new String[]{"blade", "charm"}) {
                EmberCodex.register(d, fam, slot, t, "drop");
            }
        }
    }

    private static EmberRunMaps bundled() {
        try (InputStream in = EmberMapFarmCueTest.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            return EmberRunMaps.parse(y);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
