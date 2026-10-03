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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

/** Enhance + socket per docs/design/DESIGN-ember-enhance-socket.md (reads enhance.yml). */
public final class EnhanceService {

    private final JavaPlugin plugin;
    private final NiBridge ni;
    private final Random random = new Random();
    private File enhanceFile;

    private boolean enabled = true;
    private final Set<String> whitelist = new LinkedHashSet<String>();
    private final Map<Integer, LevelDef> levels = new HashMap<Integer, LevelDef>();
    private final Map<String, SocketDef> sockets = new HashMap<String, SocketDef>();
    private final Set<String> gemIds = new LinkedHashSet<String>();
    private final Map<String, Map<String, Double>> gemStats = new HashMap<String, Map<String, Double>>();
    private final Map<String, Map<String, Double>> statPerLevel = new HashMap<String, Map<String, Double>>();

    private String displayPrefix = "§8强化 §f+";
    private String markerPrefix = "§8§o#ember_en:";
    private String socketEmpty = "§7空";
    private String socketLocked = "§7未解锁";
    private String socketMarkerPrefix = "§8§o#ember_sk:";
    private boolean autoUseProtect = true;
    private String protectId = "mat_ember_protect_scroll";
    private String stableId = "mat_ember_stable_charm";
    private boolean uniqueGemIds;

    static final class LevelDef {
        final double chance;
        final String fail; // stay | down1 | set8
        final boolean protect;
        final boolean stableRequired;
        final Map<String, Integer> costs;
        LevelDef(double chance, String fail, boolean protect, boolean stableRequired, Map<String, Integer> costs) {
            this.chance = chance; this.fail = fail; this.protect = protect;
            this.stableRequired = stableRequired; this.costs = costs;
        }
    }

    static final class SocketDef {
        final int max;
        final int[] unlockAt;
        SocketDef(int max, int[] unlockAt) { this.max = max; this.unlockAt = unlockAt; }
    }

    public EnhanceService(JavaPlugin plugin, NiBridge ni) {
        this.plugin = plugin;
        this.ni = ni;
    }

    public void reload() {
        enhanceFile = new File(plugin.getDataFolder(), "enhance.yml");
        if (!enhanceFile.exists()) {
            plugin.saveResource("enhance.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(enhanceFile);
        // merge defaults from jar
        InputStream in = plugin.getResource("enhance.yml");
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
        levels.clear();
        sockets.clear();
        gemIds.clear();
        gemStats.clear();
        statPerLevel.clear();

        enabled = cfg.getBoolean("enabled", true);
        List<String> wl = cfg.getStringList("whitelist_ni_ids");
        if (wl != null) whitelist.addAll(wl);

        ConfigurationSection lore = cfg.getConfigurationSection("lore");
        if (lore != null) {
            displayPrefix = lore.getString("display_prefix", displayPrefix);
            markerPrefix = lore.getString("marker_prefix", markerPrefix);
            socketEmpty = lore.getString("socket_empty", socketEmpty);
            socketLocked = lore.getString("socket_locked", socketLocked);
            socketMarkerPrefix = lore.getString("socket_marker_prefix", socketMarkerPrefix);
        }
        autoUseProtect = cfg.getBoolean("auto_use_protect", true);
        protectId = cfg.getString("protect_scroll_ni_id", "mat_ember_protect_scroll");
        stableId = cfg.getString("stable_charm_ni_id", "mat_ember_stable_charm");

        ConfigurationSection lvlSec = cfg.getConfigurationSection("levels");
        if (lvlSec != null) {
            for (String key : lvlSec.getKeys(false)) {
                try {
                    int target = Integer.parseInt(key);
                    ConfigurationSection one = lvlSec.getConfigurationSection(key);
                    if (one == null) continue;
                    double chance = one.getDouble("chance", 0);
                    String fail = one.getString("fail", "stay");
                    boolean protect = one.getBoolean("protect", false);
                    boolean stableReq = one.getBoolean("stable_required", false);
                    Map<String, Integer> costs = new LinkedHashMap<String, Integer>();
                    ConfigurationSection costSec = one.getConfigurationSection("costs");
                    if (costSec != null) {
                        for (String mat : costSec.getKeys(false)) {
                            costs.put(mat, Integer.valueOf(costSec.getInt(mat)));
                        }
                    }
                    levels.put(Integer.valueOf(target), new LevelDef(chance, fail, protect, stableReq, costs));
                } catch (NumberFormatException ignored) {}
            }
        }

        ConfigurationSection stats = cfg.getConfigurationSection("stat_per_level");
        if (stats != null) {
            for (String gear : stats.getKeys(false)) {
                ConfigurationSection g = stats.getConfigurationSection(gear);
                Map<String, Double> map = new LinkedHashMap<String, Double>();
                if (g != null) {
                    for (String k : g.getKeys(false)) map.put(k, Double.valueOf(g.getDouble(k)));
                }
                statPerLevel.put(gear, map);
            }
        }

        ConfigurationSection sock = cfg.getConfigurationSection("socket");
        if (sock != null) {
            uniqueGemIds = sock.getBoolean("unique_gem_ids", false);
            for (String gear : sock.getKeys(false)) {
                if ("unique_gem_ids".equals(gear)) continue;
                ConfigurationSection g = sock.getConfigurationSection(gear);
                if (g == null) continue;
                int max = g.getInt("max", 1);
                List<Integer> unlockList = g.getIntegerList("unlock_at");
                int[] unlockAt = new int[max];
                for (int i = 0; i < max; i++) {
                    unlockAt[i] = (unlockList != null && i < unlockList.size())
                            ? unlockList.get(i).intValue() : Integer.MAX_VALUE;
                }
                sockets.put(gear, new SocketDef(max, unlockAt));
            }
        }

        ConfigurationSection gems = cfg.getConfigurationSection("gems");
        if (gems != null) {
            for (String id : gems.getKeys(false)) {
                gemIds.add(id);
                ConfigurationSection g = gems.getConfigurationSection(id);
                Map<String, Double> map = new LinkedHashMap<String, Double>();
                if (g != null) {
                    for (String k : g.getKeys(false)) map.put(k, Double.valueOf(g.getDouble(k)));
                }
                gemStats.put(id, map);
            }
        }

        if (whitelist.isEmpty()) {
            whitelist.add("gear_ember_blade");
            whitelist.add("gear_ember_charm");
        }
        if (levels.isEmpty()) applyHardcodedLevelDefaults();
        if (sockets.isEmpty()) {
            sockets.put("gear_ember_blade", new SocketDef(2, new int[]{0, 5}));
            sockets.put("gear_ember_charm", new SocketDef(1, new int[]{3}));
        }
        if (gemIds.isEmpty()) {
            Collections.addAll(gemIds, "gem_ember_sharp", "gem_ember_steady", "gem_ember_drain", "gem_ember_gale");
        }

        if (ni.isReady()) {
            ni.warnMissingOnceIfAbsent(protectId);
            ni.warnMissingOnceIfAbsent(stableId);
            for (String id : gemIds) ni.warnMissingOnceIfAbsent(id);
            for (String id : whitelist) ni.warnMissingOnceIfAbsent(id);
            Set<String> mats = new HashSet<String>();
            for (LevelDef d : levels.values()) mats.addAll(d.costs.keySet());
            for (String id : mats) ni.warnMissingOnceIfAbsent(id);
        }
        plugin.getLogger().info("enhance.yml loaded: levels=" + levels.size()
                + " gems=" + gemIds.size() + " whitelist=" + whitelist);
    }

    private void applyHardcodedLevelDefaults() {
        // mirrors enhance.yml §6 if file broken
        putLvl(1, 1.00, "stay", false, false, c("mat_ember_shard", 3));
        putLvl(2, 0.90, "stay", false, false, c("mat_ember_shard", 5));
        putLvl(3, 0.80, "stay", false, false, c("mat_ember_shard", 8));
        Map<String, Integer> m4 = c("mat_ember_shard", 10); m4.put("mat_ember_bone_dust", 2);
        putLvl(4, 0.70, "down1", false, false, m4);
        Map<String, Integer> m5 = c("mat_ember_shard", 12); m5.put("mat_ember_bone_dust", 3);
        putLvl(5, 0.55, "down1", false, false, m5);
        Map<String, Integer> m6 = c("mat_ember_shard", 15); m6.put("mat_ember_bone_dust", 5);
        putLvl(6, 0.40, "down1", false, false, m6);
        Map<String, Integer> m7 = c("mat_ember_core_fragment", 1); m7.put("mat_ember_shard", 10);
        putLvl(7, 0.35, "down1", true, false, m7);
        Map<String, Integer> m8 = c("mat_ember_core_fragment", 1); m8.put("mat_ember_shard", 15);
        putLvl(8, 0.25, "down1", true, false, m8);
        Map<String, Integer> m9 = c("mat_ember_core_fragment", 2); m9.put("mat_ember_shard", 20);
        putLvl(9, 0.15, "down1", true, false, m9);
        Map<String, Integer> m10 = c("mat_ember_core_fragment", 3); m10.put("mat_ember_stable_charm", 1);
        putLvl(10, 0.08, "set8", false, true, m10);
    }

    private static Map<String, Integer> c(String id, int n) {
        Map<String, Integer> m = new LinkedHashMap<String, Integer>();
        m.put(id, Integer.valueOf(n));
        return m;
    }

    private void putLvl(int t, double ch, String fail, boolean prot, boolean stab, Map<String, Integer> costs) {
        levels.put(Integer.valueOf(t), new LevelDef(ch, fail, prot, stab, costs));
    }

    public boolean isEnabled() { return enabled; }

    public String resolveGearId(ItemStack stack) {
        String id = ni.getNiId(stack);
        if (id != null && whitelist.contains(id)) return id;
        return null;
    }

    private SocketDef socketDef(String gearId) {
        SocketDef d = sockets.get(gearId);
        if (d == null) d = new SocketDef(1, new int[]{0});
        return d;
    }

    private String[] currentSockets(ItemStack stack, String gearId, int level) {
        SocketDef def = socketDef(gearId);
        String[] prev = GearLore.readSockets(stack, def.max);
        return GearLore.syncSockets(prev, level, def.unlockAt, def.max);
    }

    private void writeState(ItemStack stack, String gearId, int level, String[] socks) {
        GearLore.write(stack, level, socks, displayPrefix, markerPrefix,
                socketEmpty, socketLocked, socketMarkerPrefix);
    }

    private ItemStack hand(Player p) { return p.getInventory().getItemInMainHand(); }

    private void setHand(Player p, ItemStack s) {
        p.getInventory().setItemInMainHand(s);
        p.updateInventory();
    }

    public void cmdInfo(Player p) {
        if (!enabled) { p.sendMessage(ChatColor.RED + "[强化] 系统未启用"); return; }
        ItemStack stack = hand(p);
        if (stack == null || stack.getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "[强化] 请手持余烬刃或护符");
            return;
        }
        String gearId = resolveGearId(stack);
        if (gearId == null) {
            p.sendMessage(ChatColor.RED + "[强化] 请手持余烬刃或护符");
            return;
        }
        int level = GearLore.readEnhance(stack);
        SocketDef def = socketDef(gearId);
        String[] socks = currentSockets(stack, gearId, level);
        p.sendMessage(ChatColor.GOLD + "[强化] §e" + gearId + ChatColor.GRAY + " 当前 "
                + ChatColor.WHITE + "+" + level);
        Map<String, Double> stats = statPerLevel.get(gearId);
        if (stats != null && !stats.isEmpty() && level > 0) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Double> e : stats.entrySet()) {
                if (sb.length() > 0) sb.append(ChatColor.GRAY + ", ");
                double v = e.getValue().doubleValue() * level;
                sb.append(ChatColor.WHITE).append(e.getKey()).append(" +").append(trimNum(v));
            }
            p.sendMessage(ChatColor.GRAY + "  属性: " + sb);
        }
        if (level >= 10) {
            p.sendMessage(ChatColor.GREEN + "  已达上限 +10");
        } else {
            int next = level + 1;
            LevelDef ld = levels.get(Integer.valueOf(next));
            if (ld == null) {
                p.sendMessage(ChatColor.RED + "  缺少 levels." + next + " 配置");
            } else {
                int pct = (int) Math.round(ld.chance * 100.0);
                p.sendMessage(ChatColor.YELLOW + "  下一档 +" + next + " 成功率 §f" + pct + "%"
                        + ChatColor.GRAY + " · 失败: " + failText(ld));
                p.sendMessage(ChatColor.GRAY + "  消耗: " + formatCosts(p, ld.costs));
                if (ld.protect) {
                    int have = ni.countInInventory(p, protectId);
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "  保护券 " + protectId + " ×" + have
                            + (autoUseProtect && have > 0
                            ? ChatColor.GREEN + "（失败自动使用，不掉级）"
                            : ChatColor.YELLOW + "（无则失败掉级）"));
                }
                if (ld.stableRequired) {
                    p.sendMessage(ChatColor.AQUA + "  需稳固符（计入消耗，失败停在 +" + level + "）");
                }
            }
        }
        p.sendMessage(ChatColor.GRAY + "  孔位:");
        for (int i = 0; i < def.max; i++) {
            String state;
            if (socks[i] == null) state = ChatColor.DARK_GRAY + "未解锁(需+" + def.unlockAt[i] + ")";
            else if (socks[i].isEmpty()) state = ChatColor.GRAY + "空";
            else state = ChatColor.WHITE + socks[i];
            p.sendMessage(ChatColor.GRAY + "    孔" + (i + 1) + ": " + state);
        }
    }

    public void cmdAttempt(Player p) {
        if (!enabled) { p.sendMessage(ChatColor.RED + "[强化] 系统未启用"); return; }
        ItemStack stack = hand(p);
        if (stack == null || stack.getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "[强化] 请手持余烬刃或护符");
            return;
        }
        String gearId = resolveGearId(stack);
        if (gearId == null) {
            p.sendMessage(ChatColor.RED + "[强化] 请手持余烬刃或护符");
            return;
        }
        int level = GearLore.readEnhance(stack);
        if (level >= 10) {
            p.sendMessage(ChatColor.YELLOW + "[强化] 已达上限 +10");
            return;
        }
        int next = level + 1;
        LevelDef ld = levels.get(Integer.valueOf(next));
        if (ld == null) {
            p.sendMessage(ChatColor.RED + "[强化] 缺少 levels." + next + " 配置");
            return;
        }
        Map<String, Integer> need = effectiveCosts(ld.costs);
        for (Map.Entry<String, Integer> e : need.entrySet()) {
            int have = ni.countInInventory(p, e.getKey());
            if (have < e.getValue().intValue()) {
                p.sendMessage(ChatColor.RED + "[强化] 材料不足：" + e.getKey()
                        + " 需要×" + e.getValue() + " 拥有×" + have);
                return;
            }
        }
        // consume all listed costs (includes stable charm for +10)
        for (Map.Entry<String, Integer> e : need.entrySet()) {
            if (!ni.consumeExact(p, e.getKey(), e.getValue().intValue())) {
                p.sendMessage(ChatColor.RED + "[强化] 扣除材料失败：" + e.getKey());
                return;
            }
        }

        boolean success = random.nextDouble() < ld.chance;
        int newLevel = level;
        String msg;
        if (success) {
            newLevel = next;
            msg = ChatColor.GREEN + "[强化] 成功 → +" + newLevel;
        } else {
            boolean usedProtect = false;
            if (ld.protect && autoUseProtect && ni.countInInventory(p, protectId) > 0) {
                ni.consumeExact(p, protectId, 1);
                usedProtect = true;
                newLevel = level;
                msg = ChatColor.YELLOW + "[强化] 失败，保护券生效，保持 +" + level;
            } else if (ld.stableRequired) {
                // stable already consumed as cost → stay
                newLevel = level;
                msg = ChatColor.YELLOW + "[强化] 失败，稳固符生效，停在 +" + level;
            } else if ("down1".equals(ld.fail)) {
                newLevel = Math.max(0, level - 1);
                msg = ChatColor.YELLOW + "[强化] 失败，掉 1 级 +" + level + " → +" + newLevel;
            } else if ("set8".equals(ld.fail)) {
                newLevel = 8;
                msg = ChatColor.YELLOW + "[强化] 失败，掉到 +8";
            } else {
                newLevel = level;
                msg = ChatColor.YELLOW + "[强化] 失败，等级不变 +" + level;
            }
            if (!usedProtect && ld.protect && !autoUseProtect) {
                // no-op; already handled
            }
        }

        String[] socks = currentSockets(stack, gearId, newLevel);
        writeState(stack, gearId, newLevel, socks);
        setHand(p, stack);
        int pct = (int) Math.round(ld.chance * 100.0);
        p.sendMessage(msg + ChatColor.GRAY + "（成功率 " + pct + "%）");
    }

    /** Admin testing only — no player path to +10. */
    public void cmdSet(Player p, int level) {
        ItemStack stack = hand(p);
        if (stack == null || stack.getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "[强化] 请手持余烬刃或护符");
            return;
        }
        String gearId = resolveGearId(stack);
        if (gearId == null) {
            p.sendMessage(ChatColor.RED + "[强化] 请手持余烬刃或护符");
            return;
        }
        level = Math.max(0, Math.min(10, level));
        String[] socks = currentSockets(stack, gearId, level);
        writeState(stack, gearId, level, socks);
        setHand(p, stack);
        p.sendMessage(ChatColor.GREEN + "[强化][管理] 已设置 +" + level
                + ChatColor.DARK_GRAY + "（仅测试）");
    }

    public void cmdSocketList(Player p) {
        ItemStack stack = hand(p);
        if (stack == null || stack.getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "[镶嵌] 请手持余烬刃或护符");
            return;
        }
        String gearId = resolveGearId(stack);
        if (gearId == null) {
            p.sendMessage(ChatColor.RED + "[镶嵌] 请手持余烬刃或护符");
            return;
        }
        int level = GearLore.readEnhance(stack);
        SocketDef def = socketDef(gearId);
        String[] socks = currentSockets(stack, gearId, level);
        p.sendMessage(ChatColor.GOLD + "[镶嵌] §e" + gearId + ChatColor.GRAY
                + " 强化 +" + level);
        for (int i = 0; i < def.max; i++) {
            String state;
            if (socks[i] == null) state = ChatColor.DARK_GRAY + "未解锁";
            else if (socks[i].isEmpty()) state = ChatColor.GRAY + "空";
            else state = ChatColor.WHITE + socks[i];
            p.sendMessage(ChatColor.GRAY + "  孔" + (i + 1) + ": " + state
                    + ChatColor.DARK_GRAY + " (解锁+" + def.unlockAt[i] + ")");
        }
        p.sendMessage(ChatColor.GRAY + "  可用: " + join(gemIds));
    }

    public void cmdSocketInsert(Player p, String gemId) {
        if (gemId == null || gemId.isEmpty()) {
            p.sendMessage(ChatColor.YELLOW + "/corerpg socket insert <gemId>");
            p.sendMessage(ChatColor.GRAY + "  " + join(gemIds));
            return;
        }
        if (!gemIds.contains(gemId)) {
            p.sendMessage(ChatColor.RED + "[镶嵌] 无效宝石：" + gemId);
            p.sendMessage(ChatColor.GRAY + "  可用: " + join(gemIds));
            return;
        }
        ItemStack stack = hand(p);
        if (stack == null || stack.getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "[镶嵌] 请手持余烬刃或护符");
            return;
        }
        String gearId = resolveGearId(stack);
        if (gearId == null) {
            p.sendMessage(ChatColor.RED + "[镶嵌] 请手持余烬刃或护符");
            return;
        }
        int level = GearLore.readEnhance(stack);
        SocketDef def = socketDef(gearId);
        String[] socks = currentSockets(stack, gearId, level);
        int idx = GearLore.firstEmptyUnlocked(socks);
        if (idx < 0) {
            // distinguish locked vs full
            boolean anyLocked = false;
            for (int i = 0; i < socks.length; i++) if (socks[i] == null) anyLocked = true;
            if (anyLocked && GearLore.firstEmptyUnlocked(socks) < 0) {
                // check if all unlocked are filled
                boolean hasEmpty = false;
                for (String s : socks) if (s != null && s.isEmpty()) hasEmpty = true;
                if (!hasEmpty) {
                    boolean allLocked = true;
                    for (String s : socks) if (s != null) allLocked = false;
                    if (allLocked) {
                        p.sendMessage(ChatColor.RED + "[镶嵌] 孔位未解锁（当前 +" + level
                                + "，需 +" + def.unlockAt[0] + "）");
                        return;
                    }
                    p.sendMessage(ChatColor.YELLOW + "[镶嵌] 已无空孔");
                    return;
                }
            }
            p.sendMessage(ChatColor.RED + "[镶嵌] 孔位未解锁 / 已无空孔");
            return;
        }
        if (uniqueGemIds) {
            for (String s : socks) {
                if (gemId.equals(s)) {
                    p.sendMessage(ChatColor.RED + "[镶嵌] 同 id 宝石不可重复镶嵌");
                    return;
                }
            }
        }
        if (ni.countInInventory(p, gemId) < 1) {
            p.sendMessage(ChatColor.RED + "[镶嵌] 背包无此石：" + gemId);
            return;
        }
        if (!ni.consumeExact(p, gemId, 1)) {
            p.sendMessage(ChatColor.RED + "[镶嵌] 扣除宝石失败");
            return;
        }
        socks[idx] = gemId;
        writeState(stack, gearId, level, socks);
        setHand(p, stack);
        p.sendMessage(ChatColor.GREEN + "[镶嵌] 已嵌入孔" + (idx + 1) + " → " + gemId);
    }

    public void cmdSocketRemove(Player p, String slotArg) {
        ItemStack stack = hand(p);
        if (stack == null || stack.getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "[镶嵌] 请手持余烬刃或护符");
            return;
        }
        String gearId = resolveGearId(stack);
        if (gearId == null) {
            p.sendMessage(ChatColor.RED + "[镶嵌] 请手持余烬刃或护符");
            return;
        }
        int slot;
        try { slot = Integer.parseInt(slotArg); } catch (Exception e) {
            p.sendMessage(ChatColor.YELLOW + "/corerpg socket remove <slot>  §8slot=1或2");
            return;
        }
        SocketDef def = socketDef(gearId);
        if (slot < 1 || slot > def.max) {
            p.sendMessage(ChatColor.RED + "[镶嵌] 无效孔位：" + slot + "（1.." + def.max + "）");
            return;
        }
        int level = GearLore.readEnhance(stack);
        String[] socks = currentSockets(stack, gearId, level);
        int idx = slot - 1;
        if (socks[idx] == null) {
            p.sendMessage(ChatColor.RED + "[镶嵌] 孔位未解锁");
            return;
        }
        if (socks[idx].isEmpty()) {
            p.sendMessage(ChatColor.YELLOW + "[镶嵌] 该孔为空");
            return;
        }
        String gemId = socks[idx];
        socks[idx] = "";
        writeState(stack, gearId, level, socks);
        setHand(p, stack);
        // return gem to inventory
        boolean given = false;
        if (ni.isReady() && ni.hasNiDefinition(gemId)) {
            try {
                ItemStack gem = pers.neige.neigeitems.manager.ItemManager.INSTANCE.getItemStack(gemId);
                if (gem != null) {
                    gem = gem.clone();
                    gem.setAmount(1);
                    Map<Integer, ItemStack> leftover = p.getInventory().addItem(gem);
                    given = leftover == null || leftover.isEmpty();
                    if (!given) {
                        for (ItemStack drop : leftover.values()) {
                            p.getWorld().dropItemNaturally(p.getLocation(), drop);
                        }
                        given = true;
                    }
                }
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "return gem failed: " + gemId, t);
            }
        }
        if (!given) {
            p.sendMessage(ChatColor.YELLOW + "[镶嵌] 已卸下 " + gemId + "，但无法生成物品（NI 缺失），仅清空孔位");
        } else {
            p.sendMessage(ChatColor.GREEN + "[镶嵌] 已卸下孔" + slot + " → " + gemId + " 回背包");
        }
    }

    private Map<String, Integer> effectiveCosts(Map<String, Integer> raw) {
        Map<String, Integer> out = new LinkedHashMap<String, Integer>();
        if (raw == null) return out;
        for (Map.Entry<String, Integer> e : raw.entrySet()) {
            if (e.getValue() == null || e.getValue().intValue() <= 0) continue;
            // if NI def missing, still require by inventory match (lore/id); warn once
            if (ni.isReady() && !ni.hasNiDefinition(e.getKey())) {
                ni.warnMissingOnce(e.getKey());
            }
            out.put(e.getKey(), e.getValue());
        }
        return out;
    }

    private String formatCosts(Player p, Map<String, Integer> costs) {
        if (costs == null || costs.isEmpty()) return "无";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : costs.entrySet()) {
            if (sb.length() > 0) sb.append(ChatColor.GRAY + ", ");
            int have = ni.countInInventory(p, e.getKey());
            sb.append(ChatColor.WHITE).append(e.getKey()).append("×").append(e.getValue())
                    .append(ChatColor.DARK_GRAY).append("(").append(have).append(")");
        }
        return sb.toString();
    }

    private static String failText(LevelDef ld) {
        if (ld.stableRequired) return "停在当前（稳固符已付）/ 否则掉到+8";
        if (ld.protect) return "掉1级（有保护券则不掉）";
        if ("down1".equals(ld.fail)) return "掉1级";
        if ("set8".equals(ld.fail)) return "掉到+8";
        return "不变";
    }

    private static String trimNum(double v) {
        if (Math.abs(v - Math.rint(v)) < 1e-9) return String.valueOf((long) Math.rint(v));
        return String.valueOf(v);
    }

    private static String join(Set<String> ids) {
        StringBuilder sb = new StringBuilder();
        for (String id : ids) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(id);
        }
        return sb.toString();
    }
}
