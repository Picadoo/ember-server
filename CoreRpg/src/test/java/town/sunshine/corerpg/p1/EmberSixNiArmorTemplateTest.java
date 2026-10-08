package town.sunshine.corerpg.p1;

import org.junit.Assume;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * D318 六槽 · 静态对拍 plugins/NeigeItems/Items/ember-armor-v1.yml（余烬-物品）：40 个 NI 护甲 ID = 插件 templateId 生成的集合
 * = ni-armor-ids.md 表；material = EmberSixSlot.materialFor；name / lore 不含 StatService 关键词；原版护甲 / 韧性覆盖为 0。
 * Offline (reads files only); skipped when the yml is absent.
 */
public class EmberSixNiArmorTemplateTest {

    static final Path ROOT = Paths.get("..");
    static final Path NI = ROOT.resolve("plugins/NeigeItems/Items/ember-armor-v1.yml");
    static final Path MD = ROOT.resolve("docs/design/staged/d318-six-slot/ni-armor-ids.md");
    static final String[] FAMS = {"scorch", "burst", "sustain"};
    static final String[] MC_SLOT = {"head", "chest", "legs", "feet"};

    /** every id the plugin can ask NeigeItems for (EmberItemData.templateId over the armor families / slots / tiers) */
    static TreeSet<String> codeIds() {
        TreeSet<String> s = new TreeSet<String>();
        for (String slot : EmberItemData.ARMOR_SLOTS) {
            s.add(EmberItemData.templateId("none", slot, 0));
            for (String f : FAMS) for (int t = 1; t <= 3; t++) s.add(EmberItemData.templateId(f, slot, t));
        }
        return s;
    }

    /** backticked ids in the md table; `x_t{1,2,3}` expands */
    static TreeSet<String> mdIds() throws java.io.IOException {
        String md = new String(Files.readAllBytes(MD), StandardCharsets.UTF_8);
        TreeSet<String> s = new TreeSet<String>();
        Matcher m = Pattern.compile("`(ember_v1_[a-z0-9_]+?)(?:\\{([0-9,]+)\\})?`").matcher(md);
        while (m.find()) {
            if (m.group(2) == null) s.add(m.group(1));
            else for (String n : m.group(2).split(",")) s.add(m.group(1) + n);
        }
        return s;
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> ni() throws java.io.IOException {
        return (Map<String, Object>) new Yaml().load(new String(Files.readAllBytes(NI), StandardCharsets.UTF_8));
    }

    @Test public void idSetsMatchCodeAndDoc() throws java.io.IOException {
        Assume.assumeTrue(Files.isRegularFile(NI));
        TreeSet<String> code = codeIds();
        assertEquals(40, code.size());
        assertEquals("ni-armor-ids.md = code", code, mdIds());
        TreeSet<String> ni = new TreeSet<String>(ni().keySet());
        TreeSet<String> missing = new TreeSet<String>(code); missing.removeAll(ni);
        TreeSet<String> extra = new TreeSet<String>(ni); extra.removeAll(code);
        assertTrue("missing in yml " + missing + " / extra in yml " + extra, missing.isEmpty() && extra.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test public void materialLoreAndVanillaStats() throws java.io.IOException {
        Assume.assumeTrue(Files.isRegularFile(NI));
        Pattern stat = Pattern.compile("物理伤害|生命力|物理防御");
        List<String> bad = new ArrayList<String>();
        for (Map.Entry<String, Object> e : ni().entrySet()) {
            String id = e.getKey();
            Map<String, Object> it = (Map<String, Object>) e.getValue();
            Matcher m = Pattern.compile("ember_v1_(?:t0_([a-z]+)|([a-z]+)_([a-z]+)_t([1-3]))").matcher(id);
            if (!m.matches()) { bad.add(id + ": unexpected id"); continue; }
            String slot = m.group(1) != null ? m.group(1) : m.group(3);
            int tier = m.group(1) != null ? 0 : Integer.parseInt(m.group(4));
            int si = EmberItemData.armorIndex(slot);
            if (si < 0) { bad.add(id + ": slot " + slot); continue; }
            String want = EmberSixSlot.materialFor(tier, si);
            if (!want.equals(String.valueOf(it.get("material")))) bad.add(id + ": material " + it.get("material") + " != " + want);
            List<String> text = new ArrayList<String>();
            text.add(String.valueOf(it.get("name")));
            if (it.get("lore") != null) for (Object l : (List<Object>) it.get("lore")) text.add(String.valueOf(l));
            for (String t : text) if (stat.matcher(t).find()) bad.add(id + ": StatService keyword in '" + t + "'");
            if (!Boolean.TRUE.equals(it.get("unbreakable"))) bad.add(id + ": not unbreakable");
            List<Object> hide = (List<Object>) it.get("hideflags");
            if (hide == null || !hide.contains("HIDE_ATTRIBUTES")) bad.add(id + ": attributes not hidden");
            Map<String, Object> nbt = (Map<String, Object>) it.get("nbt");
            List<Map<String, Object>> mods = nbt == null ? null : (List<Map<String, Object>>) nbt.get("AttributeModifiers");
            TreeSet<String> zeroed = new TreeSet<String>();
            if (mods != null) for (Map<String, Object> mod : mods) {
                if (!String.valueOf(mod.get("Amount")).matches("\\(Double\\)\\s*0(\\.0+)?")) bad.add(id + ": modifier amount " + mod.get("Amount"));
                if (!MC_SLOT[si].equals(String.valueOf(mod.get("Slot")))) bad.add(id + ": modifier slot " + mod.get("Slot"));
                zeroed.add(String.valueOf(mod.get("AttributeName")));
            }
            if (!zeroed.contains("generic.armor") || !zeroed.contains("generic.armorToughness")) bad.add(id + ": armor / toughness not zeroed " + zeroed);
        }
        assertTrue(bad.toString(), bad.isEmpty());
    }
}
