package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reads talent.yml; unlock / info / reset / grant. */
public final class TalentService {

    private final JavaPlugin plugin;
    private final NiBridge ni;
    private final PlayerDataStore dataStore;

    private boolean enabled = true;
    private int pointsPerLevel = 1;
    private int levelPointsFrom = 2;
    private int maxSpendablePoints = 20;
    private int startingTalentPoints = 5;
    private int emberLevelDefault = 10;
    private int freePerDay = 1;
    private int extraCrystalCash = 25;
    private String ticketNiId = "mat_ember_talent_reset";

    /** treeId -> (nodeId -> NodeDef) */
    private final Map<String, Map<String, NodeDef>> trees = new LinkedHashMap<String, Map<String, NodeDef>>();
    /** nodeId -> treeId for reverse lookup */
    private final Map<String, String> nodeToTree = new LinkedHashMap<String, String>();

    public static final class NodeDef {
        public final String id;
        public final String display;
        public final String type; // passive | skill | qol
        public final int cost;
        public final List<String> requires;
        public final Map<String, Double> stats;
        public final String skillId;
        NodeDef(String id, String display, String type, int cost, List<String> requires,
                Map<String, Double> stats, String skillId) {
            this.id = id;
            this.display = display;
            this.type = type;
            this.cost = cost;
            this.requires = requires;
            this.stats = stats;
            this.skillId = skillId;
        }
    }

    public TalentService(JavaPlugin plugin, NiBridge ni, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.ni = ni;
        this.dataStore = dataStore;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "talent.yml");
        if (!file.exists()) {
            plugin.saveResource("talent.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("talent.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        loadFrom(cfg);
        if (ni != null) ni.warnMissingOnceIfAbsent(ticketNiId);
    }

    private void loadFrom(FileConfiguration cfg) {
        trees.clear();
        nodeToTree.clear();
        enabled = cfg.getBoolean("enabled", true);
        pointsPerLevel = cfg.getInt("points_per_level", 1);
        levelPointsFrom = cfg.getInt("level_points_from", 2);
        maxSpendablePoints = cfg.getInt("max_spendable_points", 20);
        startingTalentPoints = cfg.getInt("starting_talent_points", 5);
        emberLevelDefault = cfg.getInt("ember_level", 10);
        ConfigurationSection reset = cfg.getConfigurationSection("reset");
        if (reset != null) {
            freePerDay = reset.getInt("free_per_day", 1);
            extraCrystalCash = reset.getInt("extra_crystal_cash", 25);
            ticketNiId = reset.getString("ticket_ni_id", "mat_ember_talent_reset");
        }
        ConfigurationSection treesSec = cfg.getConfigurationSection("trees");
        if (treesSec != null) {
            for (String treeId : treesSec.getKeys(false)) {
                ConfigurationSection nodesSec = treesSec.getConfigurationSection(treeId + ".nodes");
                if (nodesSec == null) continue;
                Map<String, NodeDef> map = new LinkedHashMap<String, NodeDef>();
                for (String nodeId : nodesSec.getKeys(false)) {
                    ConfigurationSection n = nodesSec.getConfigurationSection(nodeId);
                    if (n == null) continue;
                    List<String> req = new ArrayList<String>();
                    List<?> rawReq = n.getList("requires");
                    if (rawReq != null) {
                        for (Object o : rawReq) {
                            if (o != null) {
                                String s = String.valueOf(o).trim();
                                if (!s.isEmpty()) req.add(s);
                            }
                        }
                    }
                    Map<String, Double> stats = new LinkedHashMap<String, Double>();
                    ConfigurationSection st = n.getConfigurationSection("stats");
                    if (st != null) {
                        for (String k : st.getKeys(false)) {
                            stats.put(k, Double.valueOf(st.getDouble(k)));
                        }
                    }
                    NodeDef def = new NodeDef(
                            nodeId,
                            color(n.getString("display", nodeId)),
                            n.getString("type", "passive"),
                            Math.max(1, n.getInt("cost", 1)),
                            Collections.unmodifiableList(req),
                            Collections.unmodifiableMap(stats),
                            n.getString("skill_id", null));
                    map.put(nodeId, def);
                    nodeToTree.put(nodeId, treeId.toLowerCase());
                }
                trees.put(treeId.toLowerCase(), map);
            }
        }
    }

    public boolean isEnabled() { return enabled; }
    public int getMaxSpendablePoints() { return maxSpendablePoints; }
    public int getStartingTalentPoints() { return startingTalentPoints; }
    public int getEmberLevelDefault() { return emberLevelDefault; }

    /**
     * earned seed = max(starting_talent_points,
     *   (ember_level - level_points_from + 1) * points_per_level),
     * then min with max_spendable.
     */
    public int computeStartingEarned(int emberLevel) {
        int level = emberLevel > 0 ? emberLevel : emberLevelDefault;
        int fromLevel = 0;
        if (level >= levelPointsFrom && pointsPerLevel > 0) {
            fromLevel = (level - levelPointsFrom + 1) * pointsPerLevel;
        }
        int seed = Math.max(startingTalentPoints, fromLevel);
        if (seed <= 0) seed = 5;
        return Math.min(maxSpendablePoints, seed);
    }

    public NodeDef getNode(String nodeId) {
        if (nodeId == null) return null;
        String tree = nodeToTree.get(nodeId);
        if (tree == null) return null;
        Map<String, NodeDef> map = trees.get(tree);
        return map == null ? null : map.get(nodeId);
    }

    public String treeOf(String nodeId) {
        return nodeToTree.get(nodeId);
    }

    public Map<String, NodeDef> treeNodes(String covenantId) {
        if (covenantId == null) return Collections.emptyMap();
        Map<String, NodeDef> m = trees.get(covenantId.toLowerCase());
        return m == null ? Collections.<String, NodeDef>emptyMap() : m;
    }

    private static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s.replace('§', '&'));
    }

    private static String formatNum(double v) {
        if (Math.abs(v - Math.rint(v)) < 1e-9) return String.valueOf((long) Math.rint(v));
        return String.format("%.3f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    public String formatStats(NodeDef def) {
        if (def == null) return "";
        StringBuilder sb = new StringBuilder();
        if (def.stats != null && !def.stats.isEmpty()) {
            for (Map.Entry<String, Double> e : def.stats.entrySet()) {
                if (sb.length() > 0) sb.append(ChatColor.GRAY).append(", ");
                sb.append(ChatColor.WHITE).append(e.getKey())
                        .append(ChatColor.GRAY).append("=")
                        .append(ChatColor.AQUA).append(formatNum(e.getValue().doubleValue()));
            }
        }
        if ("skill".equalsIgnoreCase(def.type) && def.skillId != null) {
            if (sb.length() > 0) sb.append(ChatColor.GRAY).append(", ");
            sb.append(ChatColor.LIGHT_PURPLE).append("skill_id=").append(def.skillId);
        }
        if (sb.length() == 0) return ChatColor.DARK_GRAY + "（无）";
        return sb.toString();
    }

    public void cmdShow(Player player) {
        if (!enabled) {
            player.sendMessage(ChatColor.RED + "[天赋] 功能未启用");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        if (!data.hasCovenant()) {
            player.sendMessage(ChatColor.RED + "[天赋] 请先选定誓约");
            player.sendMessage(ChatColor.GRAY + "  /corerpg covenant set <blaze|ash|warden>");
            return;
        }
        int avail = data.getTalentPointsAvailable();
        player.sendMessage(ChatColor.GREEN + "[天赋] 点数 可用=" + avail
                + " 已花=" + data.getTalentPointsSpent()
                + " 已获=" + data.getTalentPointsEarned()
                + " 硬顶=" + maxSpendablePoints);
        player.sendMessage(ChatColor.GRAY + "  誓约树：" + data.getCovenant());
        List<String> nodes = data.getTalentNodes();
        if (nodes.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "  已解锁：无");
        } else {
            player.sendMessage(ChatColor.GRAY + "  已解锁：" + ChatColor.WHITE + join(nodes, ", "));
        }
        player.sendMessage(ChatColor.DARK_GRAY + "  /corerpg talent info|unlock <nodeId> · reset");
    }

    private static String join(List<String> list, String sep) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (sb.length() > 0) sb.append(sep);
            sb.append(s);
        }
        return sb.toString();
    }

    public void cmdInfo(Player player, String nodeId) {
        if (!enabled) {
            player.sendMessage(ChatColor.RED + "[天赋] 功能未启用");
            return;
        }
        if (nodeId == null || nodeId.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "[天赋] /corerpg talent info <nodeId>");
            return;
        }
        NodeDef def = getNode(nodeId);
        if (def == null) {
            player.sendMessage(ChatColor.RED + "[天赋] 未知节点：" + nodeId);
            return;
        }
        String tree = treeOf(nodeId);
        player.sendMessage(ChatColor.GREEN + "[天赋] " + def.display + ChatColor.GRAY + "（" + def.id + "）");
        player.sendMessage(ChatColor.GRAY + "  树=" + tree + " · type=" + def.type + " · cost=" + def.cost);
        player.sendMessage(ChatColor.GRAY + "  前置：" + (def.requires.isEmpty() ? "无" : join(def.requires, ", ")));
        player.sendMessage(ChatColor.GRAY + "  效果：" + formatStats(def));
        PlayerData data = dataStore.get(player.getUniqueId());
        if (data.hasTalentNode(nodeId)) {
            player.sendMessage(ChatColor.AQUA + "  状态：已解锁");
        } else {
            player.sendMessage(ChatColor.YELLOW + "  状态：未解锁");
        }
    }

    public void cmdUnlock(Player player, String nodeId) {
        if (!enabled) {
            player.sendMessage(ChatColor.RED + "[天赋] 功能未启用");
            return;
        }
        if (nodeId == null || nodeId.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "[天赋] /corerpg talent unlock <nodeId>");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        if (!data.hasCovenant()) {
            player.sendMessage(ChatColor.RED + "[天赋] 请先选定誓约");
            return;
        }
        NodeDef def = getNode(nodeId);
        if (def == null) {
            player.sendMessage(ChatColor.RED + "[天赋] 未知节点：" + nodeId);
            return;
        }
        String tree = treeOf(nodeId);
        if (tree == null || !tree.equalsIgnoreCase(data.getCovenant())) {
            player.sendMessage(ChatColor.RED + "[天赋] 节点不属于当前誓约树（需 " + tree + "）");
            return;
        }
        if (data.hasTalentNode(nodeId)) {
            player.sendMessage(ChatColor.YELLOW + "[天赋] 已解锁：" + ChatColor.stripColor(def.display)
                    + "（" + def.id + "）");
            return;
        }
        for (String req : def.requires) {
            if (!data.hasTalentNode(req)) {
                player.sendMessage(ChatColor.RED + "[天赋] 前置未点：" + req);
                return;
            }
        }
        int cost = def.cost;
        if (data.getTalentPointsAvailable() < cost) {
            player.sendMessage(ChatColor.RED + "[天赋] 点不足（需 " + cost
                    + "，可用 " + data.getTalentPointsAvailable() + "）");
            return;
        }
        if (data.getTalentPointsSpent() + cost > maxSpendablePoints) {
            player.sendMessage(ChatColor.RED + "[天赋] 已达可花硬顶 " + maxSpendablePoints
                    + "（已花 " + data.getTalentPointsSpent() + "）");
            return;
        }
        data.setTalentPointsSpent(data.getTalentPointsSpent() + cost);
        data.addTalentNode(nodeId);
        dataStore.flushMutation(player.getUniqueId());
        int left = data.getTalentPointsAvailable();
        player.sendMessage(ChatColor.GREEN + "[天赋] 解锁："
                + ChatColor.stripColor(def.display) + "（" + def.id + "）剩余 " + left + " 点");
        player.sendMessage(ChatColor.GRAY + "  效果：" + formatStats(def));
        if ("skill".equalsIgnoreCase(def.type) && def.skillId != null) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "  [技能占位] skill_id=" + def.skillId
                    + ChatColor.DARK_GRAY + "（未接 MM 释放）");
        }
    }

    public void cmdReset(Player player) {
        if (!enabled) {
            player.sendMessage(ChatColor.RED + "[天赋] 功能未启用");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        if (data.getTalentNodes().isEmpty() && data.getTalentPointsSpent() <= 0) {
            player.sendMessage(ChatColor.YELLOW + "[天赋] 当前无已分配天赋，无需洗点。");
            return;
        }
        String today = DailyService.today();
        boolean freeOk = false;
        if (!today.equals(data.getTalentFreeResetDate())) {
            data.setTalentFreeResetDate(today);
            data.setTalentFreeResetsUsed(0);
        }
        if (data.getTalentFreeResetsUsed() < freePerDay) {
            freeOk = true;
            data.setTalentFreeResetsUsed(data.getTalentFreeResetsUsed() + 1);
        } else {
            // pay ticket or crystal
            boolean paid = false;
            if (ni != null && ticketNiId != null && !ticketNiId.isEmpty()) {
                if (ni.countInInventory(player, ticketNiId) >= 1
                        && ni.consumeExact(player, ticketNiId, 1)) {
                    paid = true;
                    data.clearTalentAllocation();
                    dataStore.flushMutation(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "[天赋] 洗点完成（已扣重置券）");
                    return;
                }
                ni.warnMissingOnceIfAbsent(ticketNiId);
            }
            if (!data.takeCrystalCash(extraCrystalCash)) {
                player.sendMessage(ChatColor.RED + "[天赋] 费用不足（日免费已用尽；需晶钻 "
                        + extraCrystalCash + " 或天赋重置券）");
                return;
            }
            data.clearTalentAllocation();
            dataStore.flushMutation(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "[天赋] 洗点完成（已扣晶钻）");
            return;
        }
        if (freeOk) {
            data.clearTalentAllocation();
            dataStore.flushMutation(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "[天赋] 洗点完成（免费）");
        }
    }

    public void cmdGrant(org.bukkit.command.CommandSender sender, String playerName, String amountStr) {
        if (playerName == null || amountStr == null) {
            sender.sendMessage(ChatColor.YELLOW + "/corerpg talent grant <player> <n>");
            return;
        }
        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "[天赋] 玩家不在线: " + playerName);
            return;
        }
        int n;
        try { n = Integer.parseInt(amountStr); } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "[天赋] 数量无效");
            return;
        }
        if (n == 0) {
            sender.sendMessage(ChatColor.RED + "[天赋] 数量不能为 0");
            return;
        }
        PlayerData data = dataStore.get(target.getUniqueId());
        data.addTalentPointsEarned(n);
        dataStore.flushMutation(target.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "[天赋] 已给予 " + target.getName() + " 天赋点 "
                + (n > 0 ? "+" : "") + n + "（可用 " + data.getTalentPointsAvailable()
                + " / 已获 " + data.getTalentPointsEarned() + "）");
        target.sendMessage(ChatColor.GREEN + "[天赋] 获得天赋点 " + (n > 0 ? "+" : "") + n
                + ChatColor.GRAY + "（可用 " + data.getTalentPointsAvailable() + "）");
    }
}
