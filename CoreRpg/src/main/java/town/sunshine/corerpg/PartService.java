package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 1.15.26 (2026-09-29): B-anvil-1 烬砧材料→部件短链 — part.yml recipes + /corerpg part craft &lt;key&gt;.
 * No coin, no stamina. Does not touch forge.yml upgrade recipes.
 * docs/design-ember-anvil-mat-to-part-pilot.md
 */
public final class PartService {

    static final class Recipe {
        String key, output, name;
        int amount = 1;
        Map<String, Integer> cost = new LinkedHashMap<String, Integer>();
    }

    private final CoreRpgPlugin plugin;
    private final NiBridge ni;
    private boolean enabled = true;
    private final Map<String, Recipe> recipes = new LinkedHashMap<String, Recipe>();

    public PartService(CoreRpgPlugin plugin, NiBridge ni) {
        this.plugin = plugin;
        this.ni = ni;
    }

    public void reload() {
        File f = new File(plugin.getDataFolder(), "part.yml");
        if (!f.exists()) plugin.saveResource("part.yml", false);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        enabled = c.getBoolean("enabled", true);
        recipes.clear();
        ConfigurationSection rs = c.getConfigurationSection("recipes");
        if (rs != null) for (String k : rs.getKeys(false)) {
            ConfigurationSection r = rs.getConfigurationSection(k);
            if (r == null) continue;
            Recipe re = new Recipe();
            re.key = k;
            re.output = r.getString("output");
            re.name = r.getString("name", k);
            re.amount = Math.max(1, r.getInt("amount", 1));
            ConfigurationSection cs = r.getConfigurationSection("cost");
            if (cs != null) for (String id : cs.getKeys(false)) re.cost.put(id, Math.max(1, cs.getInt(id)));
            if (re.output == null || re.output.isEmpty() || re.cost.isEmpty()) continue;
            recipes.put(k, re);
            ni.warnMissingOnceIfAbsent(re.output);
            for (String id : re.cost.keySet()) ni.warnMissingOnceIfAbsent(id);
        }
        plugin.getLogger().info("Part: " + recipes.size() + " recipes");
    }

    /** /corerpg part [craft &lt;key&gt;|info] — TrMenu calls craft; players use menu, not slash. */
    public boolean cmd(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("players only"); return true; }
        Player p = (Player) sender;
        if (!enabled) { p.sendMessage(ChatColor.RED + "[部件] 未启用"); return true; }
        if (plugin.getQuestService() != null && plugin.getQuestService().isInstanceWorld(p.getWorld())) {
            p.sendMessage(ChatColor.RED + "[部件] 副本内不能炼制，出本后再来。");
            return true;
        }
        String action = args.length > 1 ? args[1].toLowerCase() : "info";
        if ("craft".equals(action) || "炼".equals(action) || "炼制".equals(action)) {
            if (args.length < 3) {
                p.sendMessage(ChatColor.RED + "[部件] 请从烬砧菜单点「炼部件」炼制。");
                return true;
            }
            return craft(p, args[2]);
        }
        // info / default — list recipes (for debug; menu is primary UX)
        p.sendMessage(ChatColor.GOLD + "—— 烬砧 · 材料炼部件 ——");
        if (recipes.isEmpty()) {
            p.sendMessage(ChatColor.GRAY + "暂无配方。");
            return true;
        }
        for (Recipe r : recipes.values()) {
            p.sendMessage(ChatColor.YELLOW + " " + ChatColor.WHITE + r.name + ChatColor.GRAY + "：" + costText(p, r, true)
                    + ChatColor.GRAY + " → " + ni.displayName(r.output) + "×" + r.amount);
        }
        p.sendMessage(ChatColor.DARK_GRAY + "请在烬砧菜单点「炼部件」炼制（无需手打命令）。");
        return true;
    }

    private boolean craft(Player p, String keyOrOut) {
        Recipe r = recipes.get(keyOrOut);
        if (r == null) {
            for (Recipe x : recipes.values()) {
                if (keyOrOut.equalsIgnoreCase(x.output) || keyOrOut.equalsIgnoreCase(x.key)) { r = x; break; }
            }
        }
        if (r == null) {
            p.sendMessage(ChatColor.RED + "[部件] 未知配方。请从烬砧菜单点「炼部件」。");
            return true;
        }
        List<String> missing = new ArrayList<String>();
        for (Map.Entry<String, Integer> e : r.cost.entrySet()) {
            int have = ni.countInInventory(p, e.getKey());
            if (have < e.getValue()) {
                missing.add(ni.displayName(e.getKey()) + " " + have + "/" + e.getValue());
            }
        }
        if (!missing.isEmpty()) {
            p.sendMessage(ChatColor.RED + "[部件] 材料不足，未扣除：");
            for (String m : missing) p.sendMessage(ChatColor.GRAY + "  · " + ChatColor.WHITE + m);
            p.sendMessage(ChatColor.DARK_GRAY + "需要：" + costText(p, r, false) + " → " + r.name);
            return true;
        }
        ItemStack sample = ni.createNiItem(r.output);
        if (sample == null) {
            p.sendMessage(ChatColor.RED + "[部件] 产物未定义：" + r.output + "（请稍后再试或联系管理）");
            return true;
        }
        for (Map.Entry<String, Integer> e : r.cost.entrySet()) {
            if (!ni.consumeExact(p, e.getKey(), e.getValue())) {
                p.sendMessage(ChatColor.RED + "[部件] 扣除失败，未发放产物。");
                return true;
            }
        }
        if (!ni.giveNiItem(p, r.output, r.amount)) {
            // refund best-effort
            for (Map.Entry<String, Integer> e : r.cost.entrySet()) ni.giveNiItem(p, e.getKey(), e.getValue());
            p.sendMessage(ChatColor.RED + "[部件] 发放失败，材料已退回。");
            return true;
        }
        p.updateInventory();
        p.sendMessage(ChatColor.GREEN + "[部件] 炼成 " + ChatColor.WHITE + ni.displayName(r.output)
                + ChatColor.GRAY + " ×" + r.amount + ChatColor.DARK_GRAY + "（放副手槽生效）");
        plugin.getLogger().info("part craft " + p.getName() + " " + r.key + " -> " + r.output + "x" + r.amount);
        return true;
    }

    private String costText(Player p, Recipe r, boolean withHave) {
        List<String> parts = new ArrayList<String>();
        for (Map.Entry<String, Integer> e : r.cost.entrySet()) {
            String s = ni.displayName(e.getKey()) + "×" + e.getValue();
            if (withHave) {
                int have = ni.countInInventory(p, e.getKey());
                s = (have >= e.getValue() ? ChatColor.GREEN : ChatColor.RED) + s
                        + ChatColor.DARK_GRAY + "(" + have + ")" + ChatColor.GRAY;
            }
            parts.add(s);
        }
        return String.join(" + ", parts);
    }

    public boolean isEnabled() { return enabled; }
}
