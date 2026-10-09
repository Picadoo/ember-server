package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/** D318 六槽 T1-8 · 护甲页占位符：候选按整装生命差排序、平手同族优先；生命差 = 插件真实公式（与 T1-2 同源）. */
public class EmberSixRankTest {

    static final EmberTables T = EmberTables.defaults();

    static EmberItemData piece(String fam, String slot, int tier, int q, int f, int e) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId(tier == 0 ? "none" : fam, slot, tier),
                tier == 0 ? "none" : fam, slot, tier, q, f, e, 0, true, "drop", EmberItemData.DATA_VERSION, 0);
    }

    static final String[] FAM = {"scorch", "burst", "sustain"};

    @Test public void bestIsMaxWholeLoadoutDeltaByTheRealFormula() {
        Random r = new Random(318);
        for (int n = 0; n < 3000; n++) {
            EmberItemData charm = piece(FAM[r.nextInt(3)], "charm", 1 + r.nextInt(3), r.nextInt(4), r.nextInt(4), r.nextInt(11));
            EmberItemData blade = r.nextBoolean() ? piece(FAM[r.nextInt(3)], "blade", 1 + r.nextInt(3), r.nextInt(4), r.nextInt(4), r.nextInt(11)) : null;
            EmberItemData[] worn = new EmberItemData[4];
            for (int i = 0; i < 4; i++) if (r.nextInt(3) > 0) worn[i] = piece(FAM[r.nextInt(3)], EmberItemData.ARMOR_SLOTS.get(i), 1 + r.nextInt(3), r.nextInt(4), r.nextInt(4), 0);
            List<EmberItemData> cands = new ArrayList<EmberItemData>();
            int k = r.nextInt(9);
            for (int j = 0; j < k; j++) cands.add(piece(FAM[r.nextInt(3)], EmberItemData.ARMOR_SLOTS.get(r.nextInt(4)), 1 + r.nextInt(3), r.nextInt(4), r.nextInt(4), 0));
            int lv = 1 + r.nextInt(30);
            EmberSixRank.View v = EmberSixRank.view(T, blade, charm, lv, 0, 0, worn, cands);
            double h = EmberLoadout.compute(T, blade, charm, lv, 0, 0, worn).h;
            assertEquals(Double.doubleToLongBits(h), Double.doubleToLongBits(v.h));
            for (int i = 0; i < 4; i++) {
                double best = Double.NEGATIVE_INFINITY;
                int count = 0;
                for (EmberItemData c : cands) {
                    if (EmberItemData.armorIndex(c.slot) != i) continue;
                    count++;
                    EmberItemData[] a = worn.clone();
                    a[i] = c;
                    best = Math.max(best, EmberLoadout.compute(T, blade, charm, lv, 0, 0, a).h - h);
                }
                assertEquals(count, v.count[i]);
                if (count == 0) { assertNull(v.best[i]); continue; }
                EmberItemData[] a = worn.clone();
                a[i] = v.best[i].piece;
                double d = EmberLoadout.compute(T, blade, charm, lv, 0, 0, a).h - h;
                assertEquals("delta is the real formula", Double.doubleToLongBits(d), Double.doubleToLongBits(v.best[i].delta));
                assertEquals(Double.doubleToLongBits(best), Double.doubleToLongBits(v.best[i].delta));
            }
        }
    }

    @Test public void tiesPreferTheWornFamilyThenTheCharmFamily() {
        EmberItemData charm = piece("burst", "charm", 2, 1, 1, 3);
        EmberItemData worn = piece("scorch", "chest", 2, 0, 0, 0);
        EmberItemData a = piece("sustain", "chest", 2, 2, 1, 0), b = piece("scorch", "chest", 1, 2, 1, 0), c = piece("burst", "chest", 3, 2, 1, 0);
        EmberSixRank.View v = EmberSixRank.view(T, null, charm, 20, 0, 0, new EmberItemData[]{null, worn, null, null}, Arrays.asList(a, c, b));
        assertSame("same q / f → same delta; worn family (scorch) wins over higher tier", b, v.best[1].piece);
        v = EmberSixRank.view(T, null, charm, 20, 0, 0, new EmberItemData[4], Arrays.asList(a, b, c));
        assertSame("empty slot: the charm family (burst) wins", c, v.best[1].piece);
        EmberItemData better = piece("sustain", "chest", 1, 3, 1, 0);
        v = EmberSixRank.view(T, null, charm, 20, 0, 0, new EmberItemData[]{null, worn, null, null}, Arrays.asList(a, b, c, better));
        assertSame("a larger delta beats any family preference", better, v.best[1].piece);
        assertTrue(v.best[1].delta > 0);
    }

    @Test public void allPlanAndTexts() {
        EmberItemData charm = piece("scorch", "charm", 2, 2, 2, 5);
        EmberItemData[] worn = {piece("scorch", "head", 2, 2, 2, 0), piece("burst", "chest", 2, 3, 3, 0), null, piece("scorch", "boots", 2, 1, 1, 0)};
        List<EmberItemData> cands = Arrays.asList(piece("scorch", "head", 2, 2, 2, 0), piece("scorch", "chest", 2, 0, 0, 0),
                piece("sustain", "legs", 1, 0, 0, 0), piece("scorch", "boots", 3, 3, 3, 0));
        EmberSixRank.View v = EmberSixRank.view(T, null, charm, 25, 0, 0, worn, cands);
        assertEquals(0.0, v.best[0].delta, 0);
        assertTrue(v.best[1].delta < 0);
        assertEquals(0.0, v.best[2].delta, 0); // q0 f0 = the empty slot
        assertTrue(v.best[3].delta > 0);
        assertEquals(Arrays.asList(2, 3), EmberSixRank.allPlan(v)); // ties / worse keep the current piece; empty slots fill
        assertEquals("§7生命 不变", EmberSixPapi.text(true, v, 0, "armor_head_delta"));
        assertTrue(EmberSixPapi.text(true, v, 0, "armor_chest_delta").startsWith("§c生命 -"));
        assertTrue(EmberSixPapi.text(true, v, 0, "armor_boots_delta").startsWith("§a生命 +"));
        // D325: no blade → no two-piece → set progress inactive; fam only when progress would change
        assertEquals("", EmberSixPapi.text(true, v, 0, "armor_chest_fam"));
        assertEquals("", EmberSixPapi.text(true, v, 0, "armor_head_fam"));
        assertEquals("§7先让刃与护符同族", EmberSixPapi.text(true, v, 0, "armor_set"));
        assertEquals("0", EmberSixPapi.text(true, v, 0, "armor_set_active"));
        assertEquals("0", EmberSixPapi.text(true, v, 0, "armor_set_busy"));
        assertEquals("§8护腿：空（按成色标准、精工 0% 计算）", EmberSixPapi.text(true, v, 0, "armor_legs"));
        assertEquals("§f胸甲 · 烬爆族", EmberSixPapi.text(true, v, 0, "armor_chest"));
        assertEquals("§7成色 极品 · 精工 6% · §8掉落阶 T2", EmberSixPapi.text(true, v, 0, "armor_chest_info"));
        assertEquals("§7成色 极品 → 标准 · 精工 6% → 0%", EmberSixPapi.text(true, v, 0, "armor_chest_cmp"));
        assertEquals("§f背包里：护腿 · 炽愈族", EmberSixPapi.text(true, v, 0, "armor_legs_cand"));
        assertEquals("1", EmberSixPapi.text(true, v, 0, "armor_all_has"));
        assertTrue(EmberSixPapi.text(true, v, 0, "armor_all").startsWith("§7将换上 2 件 · 生命 +"));
        assertEquals("§7护甲位原来的 3 件已存好 · §e点这里领取", EmberSixPapi.text(true, v, 3, "armor_stash_line"));
        assertEquals("§7没有待领物品", EmberSixPapi.text(true, v, 0, "armor_stash_line"));
        assertEquals("3", EmberSixPapi.text(true, v, 3, "armor_stash"));
        assertEquals("", EmberSixPapi.text(true, v, 0, "armor_nosuch_x"));
        // switch off: everything blank, armor_on 0 — except the 待领 keys (D320 ④: claim works with the switch off)
        for (String k : new String[]{"armor_head", "armor_set", "armor_all", "armor_all_has", "armor_head_has", "armor_chest_delta"}) assertEquals("", EmberSixPapi.text(false, v, 3, k));
        assertEquals("§7护甲位原来的 3 件已存好 · §e点这里领取", EmberSixPapi.text(false, null, 3, "armor_stash_line"));
        assertEquals("3", EmberSixPapi.text(false, null, 3, "armor_stash"));
        assertEquals("1", EmberSixPapi.text(false, null, 3, "armor_stash_has"));
        assertEquals("0", EmberSixPapi.text(false, null, 0, "armor_stash_has"));
        assertEquals("1", EmberSixPapi.text(true, v, 2, "armor_stash_has"));
        assertEquals("0", EmberSixPapi.text(false, null, 0, "armor_on"));
        assertEquals("1", EmberSixPapi.text(true, v, 0, "armor_on"));
    }

    @Test public void noCharmMeansArmorDoesNothing() {
        EmberSixRank.View v = EmberSixRank.view(T, null, null, 10, 0, 0, new EmberItemData[4],
                Arrays.asList(piece("burst", "head", 3, 3, 3, 0)));
        assertEquals(0.0, v.best[0].delta, 0);
        assertEquals("生命 不变", EmberSixRank.deltaText(0.0));
        assertEquals("生命 +0.5", EmberSixRank.deltaText(0.5));
        assertEquals("生命 -0.3", EmberSixRank.deltaText(-0.3));
        assertEquals("生命 +<0.1", EmberSixRank.deltaText(0.01));
    }

    @Test public void routingAndHeldArmorLines() {
        for (String k : new String[]{"armor_on", "armor_head", "armor_boots_delta", "armor_set", "armor_all", "armor_stash_line"})
            assertEquals(k, EmberRunPapi.Section.ARMOR, EmberRunPapi.route(k));
        EmberItemData a = piece("burst", "legs", 2, 1, 1, 0);
        assertEquals("§8" + EmberUpgradeRules.ARMOR_REFUSE, EmberSixPapi.heldArmorLine(a, "enhance"));
        assertEquals("§8" + EmberUpgradeRules.ARMOR_REFUSE, EmberSixPapi.heldArmorLine(a, "next"));
        assertEquals("§7分解得白板胚 0.2 个（零头攒满 1 个自动到账）", EmberSixPapi.heldArmorLine(a, "dismantle"));
        EmberItemData m = new EmberItemData(EmberItemData.newUid(), a.ni, "burst", "legs", 2, 1, 1, 0, 0, true, "migrate", 2, 0);
        assertEquals("§8这件护甲不能分解", EmberSixPapi.heldArmorLine(m, "dismantle"));
    }

    /** D333: %corerpg_p1_held_is_armor% — armor→1, blade/charm/null→0 */
    @Test public void heldIsArmorFlag() {
        assertEquals(EmberRunPapi.Section.LOADOUT, EmberRunPapi.route("held_is_armor"));
        EmberItemData armor = piece("burst", "chest", 2, 1, 1, 0);
        EmberItemData blade = piece("burst", "blade", 2, 1, 1, 0);
        EmberItemData charm = piece("burst", "charm", 2, 1, 1, 0);
        assertEquals("1", EmberSixPapi.heldIsArmor(armor));
        assertEquals("0", EmberSixPapi.heldIsArmor(blade));
        assertEquals("0", EmberSixPapi.heldIsArmor(charm));
        assertEquals("0", EmberSixPapi.heldIsArmor(null));
        assertTrue(armor.isArmor());
        assertFalse(blade.isArmor());
        assertFalse(charm.isArmor());
    }

    /** staged ember_p1_armor + gear snippet: every placeholder routes to a plugin section; player copy has no command teaching / internal words */
    @Test public void stagedMenuKeysRouteAndHaveNoCommandText() throws java.io.IOException {
        java.nio.file.Path dir = java.nio.file.Paths.get("../docs/design/staged/d318-six-slot/trmenu");
        org.junit.Assume.assumeTrue(java.nio.file.Files.isDirectory(dir));
        java.util.regex.Pattern ph = java.util.regex.Pattern.compile("%corerpg_p1_([a-z0-9_]+)%");
        int keys = 0;
        try (java.util.stream.Stream<java.nio.file.Path> st = java.nio.file.Files.list(dir)) {
            for (java.nio.file.Path f : (Iterable<java.nio.file.Path>) st::iterator) {
                String y = new String(java.nio.file.Files.readAllBytes(f), java.nio.charset.StandardCharsets.UTF_8);
                java.util.regex.Matcher m = ph.matcher(y);
                while (m.find()) { keys++; assertEquals(m.group(1), EmberRunPapi.Section.ARMOR, EmberRunPapi.route(m.group(1))); }
                for (String line : y.split("\n")) {
                    String t = line.trim();
                    if (!(t.startsWith("- '") || t.startsWith("name:"))) continue;
                    if (t.startsWith("- 'command:") || t.startsWith("- 'menu:") || t.startsWith("- 'sound:") || t.startsWith("- '(?i)")) continue;
                    assertFalse(f + ": " + t, java.util.regex.Pattern.compile("/[a-zA-Z]").matcher(t).find()); // no /command
                    assertFalse(f + ": " + t, t.contains("corerpg") && !t.contains("%corerpg_"));
                    for (String bad : new String[]{"维护", "备忘", "命令", "指令", "开关", "gear.six_slot", "T1", "D318"}) assertFalse(f + ": " + t, t.contains(bad));
                }
            }
        }
        assertTrue(keys >= 30);
    }

    // ------------------------------------------------------------------ D320 ④ ⑤

    /** ④: claim is decoupled from the switch; ⑤: every action gets a reply route */
    @Test public void claimWorksOffAndEveryClickHasAReply_D320() {
        assertEquals(EmberSixPapi.Route.CLAIM, EmberSixPapi.route(false, 2, "claim"));
        assertEquals("off, nothing to claim → 尚未开放", EmberSixPapi.Route.OFF, EmberSixPapi.route(false, 0, "claim"));
        for (String a : new String[]{"equip", "all", "worn", "set", "", "nosuch"}) {
            assertEquals(a, EmberSixPapi.Route.OFF, EmberSixPapi.route(false, 0, a));
            assertEquals(a, EmberSixPapi.Route.OFF, EmberSixPapi.route(false, 5, a));
        }
        assertEquals(EmberSixPapi.Route.CLAIM, EmberSixPapi.route(true, 0, "claim"));
        assertEquals(EmberSixPapi.Route.EQUIP, EmberSixPapi.route(true, 0, "equip"));
        assertEquals(EmberSixPapi.Route.ALL, EmberSixPapi.route(true, 0, "all"));
        assertEquals(EmberSixPapi.Route.WORN, EmberSixPapi.route(true, 0, "worn"));
        assertEquals(EmberSixPapi.Route.SET, EmberSixPapi.route(true, 0, "set"));
        assertEquals(EmberSixPapi.Route.UNKNOWN, EmberSixPapi.route(true, 0, "nosuch"));
        assertEquals("护甲功能尚未开放。", EmberSixPapi.OFF_TEXT);
        // read-only icons answer in plain words
        EmberItemData charm = piece("scorch", "charm", 2, 2, 2, 5);
        EmberItemData[] worn = {piece("scorch", "head", 2, 2, 2, 0), null, null, null};
        EmberSixRank.View v = EmberSixRank.view(T, null, charm, 25, 0, 0, worn, Arrays.asList(piece("sustain", "chest", 1, 0, 0, 0)));
        String head = EmberSixPapi.wornReply(v, 0), chest = EmberSixPapi.wornReply(v, 1), legs = EmberSixPapi.wornReply(v, 2);
        assertTrue(head, head.startsWith("穿着头盔：焚烬族 · 成色 ") && head.contains("背包里暂时没有可换的头盔"));
        assertTrue(chest, chest.startsWith("胸甲位空着") && chest.contains("点它就能换上"));
        assertTrue(legs, legs.startsWith("护腿位空着") && legs.contains("背包里暂时没有可换的护腿"));
        assertEquals("请从护甲页点选部位。", EmberSixPapi.wornReply(v, -1));
        assertEquals("请从护甲页点选部位。", EmberSixPapi.wornReply(null, 0));
        assertEquals("四件套未激活：先让刃与护符同族。", EmberSixPapi.setReply(v));
        assertEquals("四件套未激活：先让刃与护符同族。",
                EmberSixPapi.setReply(EmberSixRank.view(T, null, charm, 25, 0, 0, new EmberItemData[4], new ArrayList<EmberItemData>())));
        for (String t : new String[]{head, chest, legs, EmberSixPapi.setReply(v)})
        {
            assertFalse(t, java.util.regex.Pattern.compile("/[a-zA-Z]").matcher(t).find());
            for (String bad : new String[]{"corerpg", "命令", "开关", "已落地", "D318", "T1"}) assertFalse(t, t.contains(bad));
        }
    }

    /** ④ ⑤ service wiring: claim is routed before / without the enabled gate; cmd always answers */
    @Test public void serviceCmdRoutesEveryClick_D320() throws java.io.IOException {
        String src = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSixSlotService.java")),
                java.nio.charset.StandardCharsets.UTF_8);
        int a = src.indexOf("public boolean cmd(CommandSender s");
        String body = src.substring(a, src.indexOf("private boolean admin(", a));
        assertFalse("no blanket enabled gate in front of claim", body.contains("if (!EmberSixSlot.enabled())"));
        assertTrue(body.contains("switch (EmberSixPapi.route(on, "));
        for (String c : new String[]{"case CLAIM: claim(p);", "case EQUIP:", "case ALL:", "case WORN: p.sendMessage(", "case SET: p.sendMessage(",
                "case OFF: p.sendMessage(P + EmberSixPapi.OFF_TEXT);", "default: p.sendMessage("}) assertTrue(c, body.contains(c));
        String papi = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberRunPapi.java")),
                java.nio.charset.StandardCharsets.UTF_8);
        assertTrue("off: 待领 keys still read the real count", papi.contains("!key.startsWith(\"armor_stash\") ? 0 : s.stashCount(p.getUniqueId())"));
    }

    static org.bukkit.configuration.ConfigurationSection menu(String file) throws java.io.IOException {
        java.nio.file.Path f = java.nio.file.Paths.get("../docs/design/staged/d318-six-slot/trmenu/" + file);
        org.junit.Assume.assumeTrue(java.nio.file.Files.exists(f));
        return org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(java.nio.file.Files.newBufferedReader(f, java.nio.charset.StandardCharsets.UTF_8));
    }

    static final java.util.regex.Pattern COND = java.util.regex.Pattern.compile("check papi %corerpg_p1_([a-z0-9_]+)% == 1");

    /** the branch TrMenu shows: highest-priority sub-icon whose condition holds, else the default icon */
    static org.bukkit.configuration.ConfigurationSection shown(org.bukkit.configuration.ConfigurationSection icon, java.util.Map<String, String> papi) {
        org.bukkit.configuration.ConfigurationSection best = icon;
        int bestP = Integer.MIN_VALUE;
        for (java.util.Map<?, ?> sub : icon.getMapList("icons")) {
            java.util.regex.Matcher m = COND.matcher(String.valueOf(sub.get("condition")));
            assertTrue("only simple papi == 1 conditions: " + sub.get("condition"), m.matches());
            int pr = ((Number) sub.get("priority")).intValue();
            if ("1".equals(papi.get(m.group(1))) && pr > bestP) {
                bestP = pr;
                org.bukkit.configuration.MemoryConfiguration c = new org.bukkit.configuration.MemoryConfiguration();
                for (java.util.Map.Entry<?, ?> e : sub.entrySet()) c.set(String.valueOf(e.getKey()), e.getValue());
                best = c;
            }
        }
        return best;
    }

    static List<String> clicks(org.bukkit.configuration.ConfigurationSection b) {
        List<String> out = new ArrayList<String>();
        Object all = b.get("actions.all");
        if (all == null && b.get("actions") instanceof java.util.Map) all = ((java.util.Map<?, ?>) b.get("actions")).get("all");
        if (all instanceof List) for (Object o : (List<?>) all) {
            String s = String.valueOf(o);
            if (s.startsWith("command: ") || s.startsWith("menu: ")) out.add(s);
        }
        return out;
    }

    static String name(org.bukkit.configuration.ConfigurationSection b) {
        Object d = b.get("display");
        if (d instanceof org.bukkit.configuration.ConfigurationSection) return ((org.bukkit.configuration.ConfigurationSection) d).getString("name");
        return String.valueOf(((java.util.Map<?, ?>) d).get("name"));
    }

    /** the server answer for one menu click (the same routing the service runs) */
    static EmberSixPapi.Route reply(String click, boolean on, int stash) {
        if (click.startsWith("menu: ")) return null; // navigation is the answer
        String[] w = click.substring("command: ".length()).split(" ");
        assertEquals(click, "corerpg", w[0]);
        assertEquals(click, "p1", w[1]);
        assertEquals(click, "armor", w[2]);
        if (w.length > 4) assertTrue(click, EmberSixSlot.parseSlot(w[4]) >= 0);
        return EmberSixPapi.route(on, stash, w[3]);
    }

    /** ⑤: in every state each button shows the right face and its click gets a reply (never silent, never UNKNOWN) */
    @Test public void stagedMenuEveryButtonReplies_D320() throws java.io.IOException {
        org.bukkit.configuration.ConfigurationSection icons = menu("ember_p1_armor.yml").getConfigurationSection("Icons");
        int checked = 0;
        for (int st = 0; st < 2 * 2 * 2 * 2; st++) {
            boolean on = (st & 1) == 1, stash = (st & 2) == 2, has = (st & 4) == 4, allHas = (st & 8) == 8;
            java.util.Map<String, String> papi = new java.util.HashMap<String, String>();
            papi.put("armor_on", on ? "1" : "0");
            papi.put("armor_stash_has", stash ? "1" : "0");
            for (String k : EmberSixPapi.SLOT_KEYS) papi.put("armor_" + k + "_has", on ? (has ? "1" : "0") : ""); // "" when off
            papi.put("armor_all_has", on ? (allHas ? "1" : "0") : "");
            for (String key : icons.getKeys(false)) {
                if ("#".equals(key)) continue;
                org.bukkit.configuration.ConfigurationSection b = shown(icons.getConfigurationSection(key), papi);
                List<String> cl = clicks(b);
                assertFalse(key + " state " + st + ": click does something", cl.isEmpty());
                if ("R".equals(key)) { assertEquals("menu: ember_p1_gear", cl.get(0)); continue; }
                for (String c : cl) {
                    EmberSixPapi.Route r = reply(c, on, stash ? 2 : 0);
                    assertNotEquals(key + " " + c, EmberSixPapi.Route.UNKNOWN, r);
                    if (!on && !("L".equals(key) && stash)) assertEquals(key + " off → 尚未开放", EmberSixPapi.Route.OFF, r);
                }
                if (!on) {
                    if ("L".equals(key) && stash) {
                        assertEquals("§6待领物品", name(b));
                        assertEquals(EmberSixPapi.Route.CLAIM, reply(cl.get(0), false, 2));
                    } else assertEquals(key + " off face", "§8护甲功能尚未开放", name(b));
                } else {
                    assertNotEquals(key + " on face", "§8护甲功能尚未开放", name(b));
                    if (key.matches("[abcd]")) assertEquals(EmberSixPapi.Route.EQUIP, reply(cl.get(0), true, 0));
                    if (key.matches("[1234]")) assertEquals(EmberSixPapi.Route.WORN, reply(cl.get(0), true, 0));
                    if ("A".equals(key)) assertEquals(allHas ? "§a全部换上" : "§7全部换上", name(b));
                    if (key.matches("[abcd]") && has) assertTrue(name(b).startsWith("§e可换上"));
                }
                checked++;
            }
        }
        assertEquals("16 states × 11 buttons (+ R, the back arrow)", 16 * 11, checked);
        // gear page slot: off & nothing to claim = plain glass (decoration like the border); off & 待领 = claim; on = armor page
        org.bukkit.configuration.ConfigurationSection m = menu("ember_p1_gear.armor-slot.snippet.yml").getConfigurationSection("M");
        java.util.Map<String, String> papi = new java.util.HashMap<String, String>();
        papi.put("armor_on", "0"); papi.put("armor_stash_has", "0");
        assertTrue(clicks(shown(m, papi)).isEmpty());
        assertEquals("§8", name(shown(m, papi)));
        papi.put("armor_stash_has", "1");
        assertEquals(EmberSixPapi.Route.CLAIM, reply(clicks(shown(m, papi)).get(0), false, 1));
        papi.put("armor_on", "1");
        assertEquals("menu: ember_p1_armor", clicks(shown(m, papi)).get(0));
        papi.put("armor_stash_has", "0");
        assertEquals("menu: ember_p1_armor", clicks(shown(m, papi)).get(0));
        System.out.println("[D320] staged menu: " + checked + " icon×state faces checked, every click answered");
    }

    static EmberItemData at(EmberItemData d, long sec) { return d.withOrigin(EmberItemData.Origin.of("ember_abyss", "boss", "r1", sec)); }

    static String uidOrder(EmberSixRank.View v, int slot) {
        StringBuilder sb = new StringBuilder();
        for (EmberSixRank.Pick p : v.ranked.get(slot)) sb.append(p.piece.uid).append(',');
        return sb.toString();
    }

    /** spec §5.4-3 final keys: delta → worn family (skipped for an empty slot) → charm family → tier → newer 获得时间 → uid */
    @Test public void finalKeysAcquiredTimeThenUid() {
        EmberItemData charm = piece("burst", "charm", 2, 1, 1, 3);
        EmberItemData worn = piece("scorch", "chest", 2, 0, 0, 0);
        // identical delta / family / tier: only 获得时间 differs → newer first; no provenance (0) is oldest
        EmberItemData old = at(piece("sustain", "chest", 2, 2, 1, 0), 1_790_000_000L);
        EmberItemData neu = at(piece("sustain", "chest", 2, 2, 1, 0), 1_791_000_000L);
        EmberItemData none = piece("sustain", "chest", 2, 2, 1, 0);
        assertEquals(0L, EmberSixRank.acquiredAt(none));
        EmberSixRank.View v = EmberSixRank.view(T, null, charm, 20, 0, 0, new EmberItemData[]{null, worn, null, null}, Arrays.asList(none, old, neu));
        assertSame(neu, v.ranked.get(1).get(0).piece);
        assertSame(old, v.ranked.get(1).get(1).piece);
        assertSame(none, v.ranked.get(1).get(2).piece);
        // everything equal incl. time → uid ascending decides
        EmberItemData u1 = at(piece("sustain", "chest", 2, 2, 1, 0), 5L), u2 = at(piece("sustain", "chest", 2, 2, 1, 0), 5L);
        EmberItemData lo = u1.uid.compareTo(u2.uid) < 0 ? u1 : u2;
        v = EmberSixRank.view(T, null, charm, 20, 0, 0, new EmberItemData[]{null, worn, null, null}, Arrays.asList(u2, u1));
        assertSame(lo, v.best[1].piece);
        // time is below tier: an older higher-tier piece beats a newer lower-tier one (same delta / family)
        EmberItemData t3old = at(piece("sustain", "chest", 3, 2, 1, 0), 1L), t1new = at(piece("sustain", "chest", 1, 2, 1, 0), 9L);
        v = EmberSixRank.view(T, null, charm, 20, 0, 0, new EmberItemData[]{null, worn, null, null}, Arrays.asList(t1new, t3old));
        assertSame(t3old, v.best[1].piece);
        // empty slot: the worn-family key is skipped, the charm family decides before tier / time
        EmberItemData sc = at(piece("scorch", "chest", 3, 2, 1, 0), 9L), bu = at(piece("burst", "chest", 1, 2, 1, 0), 1L);
        v = EmberSixRank.view(T, null, charm, 20, 0, 0, new EmberItemData[4], Arrays.asList(sc, bu));
        assertSame(bu, v.best[1].piece);
        v = EmberSixRank.view(T, null, charm, 20, 0, 0, new EmberItemData[]{null, worn, null, null}, Arrays.asList(bu, sc));
        assertSame("worn slot: worn family (scorch) first", sc, v.best[1].piece);
    }

    /** T1-8: the same state refreshed many times (any backpack order) gives the identical full order in every slot */
    @Test public void sameInputRefreshedManyTimesKeepsTheOrder() {
        Random r = new Random(5318);
        for (int n = 0; n < 300; n++) {
            EmberItemData charm = piece(FAM[r.nextInt(3)], "charm", 1 + r.nextInt(3), r.nextInt(4), r.nextInt(4), r.nextInt(11));
            EmberItemData[] worn = new EmberItemData[4];
            for (int i = 0; i < 4; i++) if (r.nextBoolean()) worn[i] = piece(FAM[r.nextInt(3)], EmberItemData.ARMOR_SLOTS.get(i), 1 + r.nextInt(3), r.nextInt(4), r.nextInt(4), 0);
            List<EmberItemData> cands = new ArrayList<EmberItemData>();
            int k = 4 + r.nextInt(14);
            for (int j = 0; j < k; j++) { // few distinct values → many full ties down to time / uid
                EmberItemData d = piece(FAM[r.nextInt(2)], EmberItemData.ARMOR_SLOTS.get(r.nextInt(4)), 1 + r.nextInt(2), r.nextInt(2), r.nextInt(2), 0);
                cands.add(r.nextBoolean() ? at(d, 1_790_000_000L + r.nextInt(3)) : d);
            }
            int lv = 1 + r.nextInt(30);
            EmberSixRank.View first = EmberSixRank.view(T, null, charm, lv, 0, 0, worn, cands);
            String[] ref = new String[4];
            for (int i = 0; i < 4; i++) ref[i] = uidOrder(first, i);
            String plan = EmberSixRank.planKey(first, EmberSixRank.allPlan(first));
            for (int rep = 0; rep < 20; rep++) {
                List<EmberItemData> sh = new ArrayList<EmberItemData>(cands);
                java.util.Collections.shuffle(sh, r);
                EmberSixRank.View v = EmberSixRank.view(T, null, charm, lv, 0, 0, worn, sh);
                for (int i = 0; i < 4; i++) assertEquals(ref[i], uidOrder(v, i));
                assertEquals(plan, EmberSixRank.planKey(v, EmberSixRank.allPlan(v)));
            }
            // the comparator is a strict total order on distinct pieces (never 0 → no reliance on sort stability)
            for (int i = 0; i < 4; i++) {
                List<EmberSixRank.Pick> ps = first.ranked.get(i);
                for (int a = 0; a < ps.size(); a++) for (int b = 0; b < ps.size(); b++) {
                    int c = EmberSixRank.order(ps.get(a), ps.get(b), first.worn[i], charm);
                    assertEquals(Integer.signum(Integer.compare(a, b)), Integer.signum(c));
                }
            }
        }
    }

    /** 「全部换上」: ties keep the worn piece; two clicks within 30 s on the same plan run it; changed / expired → refresh, never silent */
    @Test public void equipAllTwoClickConfirm() {
        assertEquals(EmberSixRank.Confirm.PREVIEW, EmberSixRank.confirm(null, null, 1000L, "1:a;"));
        assertEquals(EmberSixRank.Confirm.EXECUTE, EmberSixRank.confirm(1000L, "1:a;", 1000L + 29_999L, "1:a;"));
        assertEquals(EmberSixRank.Confirm.EXECUTE, EmberSixRank.confirm(1000L, "1:a;", 1000L + 30_000L, "1:a;"));
        assertEquals("timed out", EmberSixRank.Confirm.EXPIRED, EmberSixRank.confirm(1000L, "1:a;", 1000L + 30_001L, "1:a;"));
        assertEquals("plan changed", EmberSixRank.Confirm.REFRESHED, EmberSixRank.confirm(1000L, "1:a;", 2000L, "1:b;"));
        assertEquals("plan grew", EmberSixRank.Confirm.REFRESHED, EmberSixRank.confirm(1000L, "1:a;", 2000L, "1:a;2:c;"));
        assertEquals("clock went back", EmberSixRank.Confirm.EXPIRED, EmberSixRank.confirm(5000L, "1:a;", 4000L, "1:a;"));
        assertEquals("§7背包有变化，已刷新方案", EmberSixRank.REFRESHED_TEXT);

        EmberItemData charm = piece("scorch", "charm", 2, 2, 2, 5);
        EmberItemData[] worn = {piece("scorch", "head", 2, 2, 2, 0), null, null, piece("scorch", "boots", 2, 1, 1, 0)};
        EmberItemData sameHead = piece("burst", "head", 3, 2, 2, 0); // same q / f → delta 0 → keep the worn head
        EmberItemData chest = piece("sustain", "chest", 1, 0, 0, 0);  // empty slot → filled even at delta 0
        EmberItemData boots = piece("scorch", "boots", 2, 3, 3, 0);
        List<EmberItemData> cands = new ArrayList<EmberItemData>(Arrays.asList(sameHead, chest, boots));
        EmberSixRank.View v = EmberSixRank.view(T, null, charm, 25, 0, 0, worn, cands);
        assertEquals(0.0, v.best[0].delta, 0);
        List<Integer> todo = EmberSixRank.allPlan(v);
        assertEquals(Arrays.asList(1, 3), todo);
        String k1 = EmberSixRank.planKey(v, todo);
        // simulated clicks: preview → (backpack changes: a better chest arrives) → refresh, nothing runs → confirm runs
        long t0 = 10_000L;
        assertEquals(EmberSixRank.Confirm.PREVIEW, EmberSixRank.confirm(null, null, t0, k1));
        EmberItemData chest2 = piece("scorch", "chest", 2, 3, 3, 0);
        cands.add(chest2);
        v = EmberSixRank.view(T, null, charm, 25, 0, 0, worn, cands);
        String k2 = EmberSixRank.planKey(v, EmberSixRank.allPlan(v));
        assertNotEquals(k1, k2);
        assertEquals(EmberSixRank.Confirm.REFRESHED, EmberSixRank.confirm(t0, k1, t0 + 5_000L, k2));
        assertEquals(EmberSixRank.Confirm.EXECUTE, EmberSixRank.confirm(t0 + 5_000L, k2, t0 + 9_000L, k2));
        // refreshing the same state never changes the key (no spurious "changed")
        assertEquals(k2, EmberSixRank.planKey(EmberSixRank.view(T, null, charm, 25, 0, 0, worn, Arrays.asList(chest2, boots, chest, sameHead)),
                EmberSixRank.allPlan(EmberSixRank.view(T, null, charm, 25, 0, 0, worn, Arrays.asList(chest2, boots, chest, sameHead)))));
    }

    /** D320 ⑥: pure timeout and plan change have different texts; plan change wins when both happened */
    @Test public void timeoutAndPlanChangeAreDifferentTexts_D320() {
        assertEquals("§7确认已超时，请再点一次", EmberSixRank.EXPIRED_TEXT);
        assertNotEquals(EmberSixRank.EXPIRED_TEXT, EmberSixRank.REFRESHED_TEXT);
        assertEquals(EmberSixRank.EXPIRED_TEXT, EmberSixRank.confirmText(EmberSixRank.confirm(0L, "1:a;", 30_001L, "1:a;")));
        assertEquals(EmberSixRank.EXPIRED_TEXT, EmberSixRank.confirmText(EmberSixRank.confirm(0L, "1:a;", 3_600_000L, "1:a;")));
        assertEquals(EmberSixRank.REFRESHED_TEXT, EmberSixRank.confirmText(EmberSixRank.confirm(0L, "1:a;", 5_000L, "1:b;")));
        assertEquals("changed and expired → the change is what the player needs to know", EmberSixRank.REFRESHED_TEXT,
                EmberSixRank.confirmText(EmberSixRank.confirm(0L, "1:a;", 60_000L, "1:b;")));
        assertNull(EmberSixRank.confirmText(EmberSixRank.Confirm.PREVIEW));
        assertNull(EmberSixRank.confirmText(EmberSixRank.Confirm.EXECUTE));
        // window edges, any key: within 30 s and same plan always runs; past it never runs
        Random r = new Random(320);
        for (int n = 0; n < 5000; n++) {
            long at = r.nextInt(1_000_000), dt = r.nextInt(70_000) - 5_000;
            String k = "1:" + r.nextInt(3) + ";", k2 = r.nextBoolean() ? k : "1:" + r.nextInt(3) + ";";
            EmberSixRank.Confirm c = EmberSixRank.confirm(at, k, at + dt, k2);
            if (!k.equals(k2)) assertEquals(EmberSixRank.Confirm.REFRESHED, c);
            else if (dt >= 0 && dt <= EmberSixRank.CONFIRM_MS) assertEquals(EmberSixRank.Confirm.EXECUTE, c);
            else assertEquals(EmberSixRank.Confirm.EXPIRED, c);
        }
    }

    /** the service wiring: refresh text is sent before the new preview; the empty-plan branch also reports a stale preview */
    @Test public void serviceUsesTheConfirmDecision() throws java.io.IOException {
        String src = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSixSlotService.java")),
                java.nio.charset.StandardCharsets.UTF_8);
        int a = src.indexOf("void equipAll(");
        String body = src.substring(a, src.indexOf("boolean swapIn(", a));
        assertTrue(body.contains("EmberSixRank.confirm("));
        assertTrue(body.indexOf("REFRESHED_TEXT") < body.indexOf("将换上："));
        assertTrue(body.indexOf("EmberSixRank.confirmText(c)") < body.indexOf("将换上："));
        assertTrue(body.indexOf("c != EmberSixRank.Confirm.EXECUTE") < body.indexOf("swapIn("));
        assertTrue(src.contains("EmberSixMigration.claimGuarded("));
    }
}
