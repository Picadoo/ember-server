package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** Scrap + reforge per docs/status/STATUS-ember-disassemble.md / scrap.yml. */
public final class ScrapService {

    private final JavaPlugin plugin;
    private final NiBridge ni;
    private final Random random = new Random();

    private boolean enabled = true;
    private final Set<String> whitelist = new LinkedHashSet<String>();
    private final Map<String, GearScrap> scrapByGear = new LinkedHashMap<String, GearScrap>();
    private int enhanceBonusPerLevels = 2;

    private String stoneNiId = "mat_ember_reforge_stone";
    private int reforgeRolls = 1;
    private final List<AffixPoolEntry> secondaryPool = new ArrayList<AffixPoolEntry>();
    private final Map<String, String> affixDisplay = new LinkedHashMap<String, String>();
    private final Map<String, String> affixSuffix = new LinkedHashMap<String, String>();

    static final class Range {
        final int min;
        final int max;
        Range(int min, int max) {
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
        }
        int roll(Random r) {
            if (max <= min) return min;
            return min + r.nextInt(max - min + 1);
        }
    }

    static final class GearScrap {
        final Map<String, Range> guaranteed = new LinkedHashMap<String, Range>();
        final Map<String, Double> chance = new LinkedHashMap<String, Double>();
    }

    static final class AffixPoolEntry {
        final String id;
        final String display;
        final int min;
        final int max;
        final String suffix;
        AffixPoolEntry(String id, String display, int min, int max, String suffix) {
            this.id = id;
            this.display = display;
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
            this.suffix = suffix == null ? "" : suffix;
        }
        int rollValue(Random r) {
            if (max <= min) return min;
            return min + r.nextInt(max - min + 1);
        }
    }

    public ScrapService(JavaPlugin plugin, NiBridge ni) {
        this.plugin = plugin;
        this.ni = ni;
    }

    public void reload() {
        File scrapFile = new File(plugin.getDataFolder(), "scrap.yml");
        if (!scrapFile.exists()) {
            plugin.saveResource("scrap.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(scrapFile);
        InputStream in = plugin.getResource("scrap.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        loadFrom(cfg);
    }

    private void loadFrom(FileConfiguration cfg) {
        whitelist.clear();
        scrapByGear.clear();
        secondaryPool.clear();
        affixDisplay.clear();
        affixSuffix.clear();

        enabled = cfg.getBoolean("enabled", true);
        List<String> wl = cfg.getStringList("whitelist_ni_ids");
        if (wl != null) whitelist.addAll(wl);

        ConfigurationSection scrapRoot = cfg.getConfigurationSection("scrap");
        if (scrapRoot != null) {
            enhanceBonusPerLevels = scrapRoot.getInt("enhance_bonus_per_levels", 2);
            for (String gearId : scrapRoot.getKeys(false)) {
                if ("enhance_bonus_per_levels".equals(gearId)) continue;
                ConfigurationSection gearSec = scrapRoot.getConfigurationSection(gearId);
                if (gearSec == null) continue;
                GearScrap gs = new GearScrap();
                ConfigurationSection gu = gearSec.getConfigurationSection("guaranteed");
                if (gu != null) {
                    for (String mat : gu.getKeys(false)) {
                        List<Integer> range = gu.getIntegerList(mat);
                        if (range != null && range.size() >= 2) {
                            gs.guaranteed.put(mat, new Range(range.get(0).intValue(), range.get(1).intValue()));
                        } else {
                            int n = gu.getInt(mat, 0);
                            if (n > 0) gs.guaranteed.put(mat, new Range(n, n));
                        }
                    }
                }
                ConfigurationSection ch = gearSec.getConfigurationSection("chance");
                if (ch != null) {
                    for (String gem : ch.getKeys(false)) {
                        gs.chance.put(gem, Double.valueOf(ch.getDouble(gem)));
                    }
                }
                scrapByGear.put(gearId, gs);
            }
        }

        ConfigurationSection ref = cfg.getConfigurationSection("reforge");
        if (ref != null) {
            stoneNiId = ref.getString("stone_ni_id", "mat_ember_reforge_stone");
            reforgeRolls = Math.max(0, ref.getInt("rolls", 1));
            List<?> pool = ref.getList("secondary_pool");
            if (pool != null) {
                for (Object o : pool) {
                    if (!(o instanceof Map)) continue;
                    Map<?, ?> m = (Map<?, ?>) o;
                    String id = str(m.get("id"));
                    if (id.isEmpty()) continue;
                    String display = m.containsKey("display") ? str(m.get("display")) : id;
                    int min = toInt(m.get("min"), 1);
                    int max = toInt(m.get("max"), min);
                    String suffix = m.containsKey("suffix") ? str(m.get("suffix")) : "";
                    AffixPoolEntry e = new AffixPoolEntry(id, display, min, max, suffix);
                    secondaryPool.add(e);
                    affixDisplay.put(id, display);
                    affixSuffix.put(id, suffix);
                }
            }
        }

        if (whitelist.isEmpty()) {
            whitelist.add("gear_ember_blade");
            whitelist.add("gear_ember_charm");
        }
        if (scrapByGear.isEmpty()) applyHardcodedScrapDefaults();
        if (secondaryPool.isEmpty()) applyHardcodedPoolDefaults();

        if (ni.isReady()) {
            for (String id : whitelist) ni.warnMissingOnceIfAbsent(id);
            ni.warnMissingOnceIfAbsent(stoneNiId);
            for (GearScrap gs : scrapByGear.values()) {
                for (String id : gs.guaranteed.keySet()) ni.warnMissingOnceIfAbsent(id);
                for (String id : gs.chance.keySet()) ni.warnMissingOnceIfAbsent(id);
            }
        }
        plugin.getLogger().info("scrap.yml loaded: scrapGears=" + scrapByGear.size()
                + " pool=" + secondaryPool.size() + " whitelist=" + whitelist);
    }

    private void applyHardcodedScrapDefaults() {
        GearScrap blade = new GearScrap();
        blade.guaranteed.put("mat_ember_shard", new Range(2, 4));
        blade.chance.put("gem_ember_sharp", Double.valueOf(0.08));
        blade.chance.put("gem_ember_steady", Double.valueOf(0.05));
        blade.chance.put("gem_ember_drain", Double.valueOf(0.05));
        blade.chance.put("gem_ember_gale", Double.valueOf(0.05));
        scrapByGear.put("gear_ember_blade", blade);

        GearScrap charm = new GearScrap();
        charm.guaranteed.put("mat_ember_bone_dust", new Range(2, 4));
        charm.chance.put("gem_ember_sharp", Double.valueOf(0.05));
        charm.chance.put("gem_ember_steady", Double.valueOf(0.08));
        charm.chance.put("gem_ember_drain", Double.valueOf(0.05));
        charm.chance.put("gem_ember_gale", Double.valueOf(0.05));
        scrapByGear.put("gear_ember_charm", charm);
    }

    private void applyHardcodedPoolDefaults() {
        addPool("crit_chance", "暴击率", 1, 3, "%");
        addPool("attack_speed", "攻速", 1, 4, "%");
        addPool("max_health", "生命", 2, 8, "");
        addPool("phys_defense", "物理防御", 1, 3, "");
    }

    private void addPool(String id, String display, int min, int max, String suffix) {
        AffixPoolEntry e = new AffixPoolEntry(id, display, min, max, suffix);
        secondaryPool.add(e);
        affixDisplay.put(id, display);
        affixSuffix.put(id, suffix);
    }

    private static String str(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private static int toInt(Object o, int def) {
        if (o instanceof Number) return ((Number) o).intValue();
        if (o != null) {
            try { return Integer.parseInt(String.valueOf(o)); } catch (NumberFormatException ignored) {}
        }
        return def;
    }

    public boolean isEnabled() { return enabled; }

    public String resolveGearId(ItemStack stack) {
        String id = ni.getNiId(stack);
        if (id != null && whitelist.contains(id)) return id;
        return null;
    }

    private ItemStack hand(Player p) { return p.getInventory().getItemInMainHand(); }

    private void setHand(Player p, ItemStack s) {
        p.getInventory().setItemInMainHand(s);
        p.updateInventory();
    }

    /** Preview or execute scrap. */
    public void cmdScrap(Player p, boolean infoOnly) {
        if (!enabled) {
            p.sendMessage(ChatColor.RED + "[分解] 系统未启用");
            return;
        }
        ItemStack stack = hand(p);
        if (stack == null || stack.getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "[分解] 请手持余烬刃或护符");
            return;
        }
        String gearId = resolveGearId(stack);
        if (gearId == null) {
            p.sendMessage(ChatColor.RED + "[分解] 手持物不在白名单（刃/护符）");
            return;
        }
        GearScrap gs = scrapByGear.get(gearId);
        if (gs == null) {
            p.sendMessage(ChatColor.RED + "[分解] 缺少 scrap." + gearId + " 配置");
            return;
        }
        int enhance = GearLore.readEnhance(stack);
        Map<String, Integer> grants = computeGrants(gs, enhance, !infoOnly);

        if (infoOnly) {
            p.sendMessage(ChatColor.GOLD + "[分解] §e预览 §f" + gearId
                    + ChatColor.GRAY + " 强化+" + enhance);
            p.sendMessage(ChatColor.GRAY + "  保底: " + formatGrantPreview(gs, enhance));
            p.sendMessage(ChatColor.GRAY + "  概率: " + formatChancePreview(gs));
            if (enhanceBonusPerLevels > 0 && enhance >= enhanceBonusPerLevels) {
                int bonus = enhance / enhanceBonusPerLevels;
                p.sendMessage(ChatColor.YELLOW + "  强化加成: 每 " + enhanceBonusPerLevels
                        + " 级 +1 主材料 → 当前 +" + bonus);
            }
            p.sendMessage(ChatColor.DARK_GRAY + "  执行: /corerpg scrap");
            return;
        }

        // destroy 1 from hand
        int amt = stack.getAmount();
        if (amt <= 1) {
            setHand(p, null);
        } else {
            stack.setAmount(amt - 1);
            setHand(p, stack);
        }

        List<String> summary = new ArrayList<String>();
        for (Map.Entry<String, Integer> e : grants.entrySet()) {
            int n = e.getValue().intValue();
            if (n <= 0) continue;
            boolean ok = ni.giveNiItem(p, e.getKey(), n);
            summary.add(e.getKey() + "×" + n + (ok ? "" : ChatColor.RED + "(给予失败)"));
        }
        if (summary.isEmpty()) {
            p.sendMessage(ChatColor.YELLOW + "[分解] 已销毁 " + gearId + "，但无产出（检查配置）");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < summary.size(); i++) {
                if (i > 0) sb.append(ChatColor.GRAY).append(", ");
                sb.append(ChatColor.WHITE).append(summary.get(i));
            }
            p.sendMessage(ChatColor.GREEN + "[分解] 已分解 §e" + gearId
                    + ChatColor.GRAY + "（+" + enhance + "）→ " + sb);
        }
    }

    private Map<String, Integer> computeGrants(GearScrap gs, int enhance, boolean rollChance) {
        Map<String, Integer> out = new LinkedHashMap<String, Integer>();
        int bonus = 0;
        if (enhanceBonusPerLevels > 0 && enhance > 0) {
            bonus = enhance / enhanceBonusPerLevels;
        }
        boolean firstGuaranteed = true;
        for (Map.Entry<String, Range> e : gs.guaranteed.entrySet()) {
            int n = e.getValue().roll(random);
            if (firstGuaranteed && bonus > 0) {
                n += bonus;
                firstGuaranteed = false;
            }
            if (n > 0) out.put(e.getKey(), Integer.valueOf(n));
        }
        if (rollChance) {
            for (Map.Entry<String, Double> e : gs.chance.entrySet()) {
                double c = e.getValue() == null ? 0.0 : e.getValue().doubleValue();
                if (c > 0.0 && random.nextDouble() < c) {
                    Integer prev = out.get(e.getKey());
                    out.put(e.getKey(), Integer.valueOf((prev == null ? 0 : prev.intValue()) + 1));
                }
            }
        }
        return out;
    }

    private String formatGrantPreview(GearScrap gs, int enhance) {
        int bonus = (enhanceBonusPerLevels > 0 && enhance > 0)
                ? enhance / enhanceBonusPerLevels : 0;
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Range> e : gs.guaranteed.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            Range r = e.getValue();
            String range = r.min == r.max ? ("×" + r.min) : ("×" + r.min + "-" + r.max);
            if (first && bonus > 0) {
                range = range + ChatColor.YELLOW + "(+" + bonus + ")" + ChatColor.GRAY;
                first = false;
            }
            sb.append(e.getKey()).append(range);
        }
        return sb.length() == 0 ? "无" : sb.toString();
    }

    private String formatChancePreview(GearScrap gs) {
        if (gs.chance.isEmpty()) return "无";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> e : gs.chance.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            int pct = (int) Math.round(e.getValue().doubleValue() * 100.0);
            sb.append(e.getKey()).append(" ").append(pct).append("%");
        }
        return sb.toString();
    }

    public void cmdReforge(Player p) {
        if (!enabled) {
            p.sendMessage(ChatColor.RED + "[重铸] 系统未启用");
            return;
        }
        ItemStack stack = hand(p);
        if (stack == null || stack.getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "[重铸] 请手持余烬刃或护符");
            return;
        }
        String gearId = resolveGearId(stack);
        if (gearId == null) {
            p.sendMessage(ChatColor.RED + "[重铸] 手持物不在白名单（刃/护符）");
            return;
        }
        if (secondaryPool.isEmpty()) {
            p.sendMessage(ChatColor.RED + "[重铸] 次要词缀池为空");
            return;
        }
        if (ni.countInInventory(p, stoneNiId) < 1) {
            p.sendMessage(ChatColor.RED + "[重铸] 需要 " + stoneNiId + " ×1");
            return;
        }
        if (!ni.consumeExact(p, stoneNiId, 1)) {
            p.sendMessage(ChatColor.RED + "[重铸] 扣除重铸石失败");
            return;
        }

        List<GearLore.Affix> rolled = new ArrayList<GearLore.Affix>();
        int rolls = Math.max(0, reforgeRolls);
        // avoid duplicate ids in one reforge when pool allows
        List<AffixPoolEntry> bag = new ArrayList<AffixPoolEntry>(secondaryPool);
        for (int i = 0; i < rolls; i++) {
            if (bag.isEmpty()) bag = new ArrayList<AffixPoolEntry>(secondaryPool);
            AffixPoolEntry pick = bag.get(random.nextInt(bag.size()));
            bag.remove(pick);
            rolled.add(new GearLore.Affix(pick.id, pick.rollValue(random)));
        }

        GearLore.rewriteAffixes(stack, rolled, affixDisplay, affixSuffix);
        setHand(p, stack);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rolled.size(); i++) {
            GearLore.Affix a = rolled.get(i);
            if (i > 0) sb.append(ChatColor.GRAY).append(", ");
            String name = affixDisplay.containsKey(a.id) ? affixDisplay.get(a.id) : a.id;
            String suffix = affixSuffix.containsKey(a.id) ? affixSuffix.get(a.id) : "";
            if (suffix == null) suffix = "";
            sb.append(ChatColor.WHITE).append(name).append(" +").append(a.value).append(suffix);
        }
        int enhance = GearLore.readEnhance(stack);
        p.sendMessage(ChatColor.GREEN + "[重铸] §e" + gearId
                + ChatColor.GRAY + " +" + enhance + " 次要刷新 → "
                + (sb.length() == 0 ? ChatColor.DARK_GRAY + "无" : sb.toString()));
    }
}
