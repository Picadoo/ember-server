package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 1.12.0 (2026-09-27): 锻造保底 — T1→T2 / T2→T3 for the held gear, paid with core fragments, shards, 凝核,
 * 灾厄余烬 and coins (forge.yml). Keeps floor(enhance × keep_enhance), carries vanilla enchants (max level),
 * gems that still fit and affix lines; gems in slots the lower level can't open go back to the inventory.
 * docs/ember-master-plan.md §6.1.
 */
public final class ForgeService {

    static final class Recipe {
        String from, to, name;
        int coin;
        Map<String, Integer> cost = new LinkedHashMap<String, Integer>();
    }

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge ni;
    private boolean enabled = true;
    private double keepEnhance = 0.5;
    private boolean keepEnchants = true;
    private final Map<String, Recipe> recipes = new LinkedHashMap<String, Recipe>();

    public ForgeService(CoreRpgPlugin plugin, PlayerDataStore dataStore, NiBridge ni) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.ni = ni;
    }

    public void reload() {
        File f = new File(plugin.getDataFolder(), "forge.yml");
        if (!f.exists()) plugin.saveResource("forge.yml", false);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        enabled = c.getBoolean("enabled", true);
        keepEnhance = c.getDouble("keep_enhance", 0.5);
        keepEnchants = c.getBoolean("keep_enchants", true);
        recipes.clear();
        ConfigurationSection rs = c.getConfigurationSection("recipes");
        if (rs != null) for (String k : rs.getKeys(false)) {
            ConfigurationSection r = rs.getConfigurationSection(k);
            if (r == null) continue;
            Recipe re = new Recipe();
            re.from = k;
            re.to = r.getString("to");
            re.name = r.getString("name", k);
            re.coin = r.getInt("coin", 0);
            ConfigurationSection cs = r.getConfigurationSection("cost");
            if (cs != null) for (String id : cs.getKeys(false)) re.cost.put(id, Math.max(1, cs.getInt(id)));
            if (re.to == null) continue;
            recipes.put(k, re);
            ni.warnMissingOnceIfAbsent(re.from);
            ni.warnMissingOnceIfAbsent(re.to);
            for (String id : re.cost.keySet()) ni.warnMissingOnceIfAbsent(id);
        }
        plugin.getLogger().info("Forge: " + recipes.size() + " recipes, keep_enhance=" + keepEnhance);
    }

    /** /corerpg forge [confirm] — hold the gear in the main hand. */
    public boolean cmd(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("players only"); return true; }
        Player p = (Player) sender;
        if (!enabled) { p.sendMessage(ChatColor.RED + "[锻造] 未启用"); return true; }
        if (plugin.getQuestService() != null && plugin.getQuestService().isInstanceWorld(p.getWorld())) {
            p.sendMessage(ChatColor.RED + "[锻造] 副本内不能锻造，出本后再来。");
            return true;
        }
        ItemStack hand = p.getInventory().getItemInMainHand();
        String id = hand == null || hand.getType() == Material.AIR ? null : ni.getNiId(hand);
        Recipe r = id == null ? null : recipes.get(id);
        PlayerData d = dataStore.get(p.getUniqueId());
        boolean confirm = args.length > 1 && ("confirm".equalsIgnoreCase(args[1]) || "确认".equals(args[1]));
        if (r == null) {
            p.sendMessage(ChatColor.GOLD + "—— 余烬锻炉 · 锻造保底 ——");
            p.sendMessage(ChatColor.GRAY + "手持 T1/T2 装备，/corerpg forge 查看配方，/corerpg forge confirm 锻造。");
            for (Recipe x : recipes.values())
                p.sendMessage(ChatColor.YELLOW + " " + ni.displayName(x.from) + ChatColor.GRAY + " → " + ChatColor.WHITE
                        + ni.displayName(x.to) + ChatColor.GRAY + "：" + costText(p, x, false));
            p.sendMessage(ChatColor.DARK_GRAY + "保留 " + (int) Math.round(keepEnhance * 100) + "% 强化等级（向下取整）、附魔、词条与仍可用孔位的宝石；多余宝石退回背包。");
            return true;
        }
        if (hand.getAmount() != 1) { p.sendMessage(ChatColor.RED + "[锻造] 请只手持 1 件装备。"); return true; }
        int lv = GearLore.readEnhance(hand);
        int newLv = (int) Math.floor(lv * keepEnhance);
        if (!confirm) {
            p.sendMessage(ChatColor.GOLD + "[锻造] " + ChatColor.WHITE + ni.displayName(r.from) + " +" + lv + ChatColor.GRAY + " → "
                    + ChatColor.WHITE + ni.displayName(r.to) + " +" + newLv);
            p.sendMessage(ChatColor.GRAY + "  消耗：" + costText(p, r, true));
            p.sendMessage(ChatColor.YELLOW + "  确认锻造：/corerpg forge confirm");
            return true;
        }
        for (Map.Entry<String, Integer> e : r.cost.entrySet()) {
            int have = ni.countInInventory(p, e.getKey());
            if (have < e.getValue()) { p.sendMessage(ChatColor.RED + "[锻造] 材料不足：" + ni.displayName(e.getKey()) + " " + have + "/" + e.getValue()); return true; }
        }
        if (d.getCoin() < r.coin) { p.sendMessage(ChatColor.RED + "[锻造] 余烬币不足：需要 " + r.coin + "，拥有 " + d.getCoin()); return true; }
        ItemStack out = ni.createNiItem(r.to);
        if (out == null) { p.sendMessage(ChatColor.RED + "[锻造] 目标装备未定义：" + r.to); return true; }
        // consume before handing out (the held gear is not a cost item, so consuming can't touch it)
        for (Map.Entry<String, Integer> e : r.cost.entrySet()) {
            if (!ni.consumeExact(p, e.getKey(), e.getValue())) { p.sendMessage(ChatColor.RED + "[锻造] 扣除失败"); return true; }
        }
        if (r.coin > 0) d.takeCoin(r.coin);
        ItemStack old = hand.clone();
        List<String> returned = new ArrayList<String>();
        EnhanceService es = plugin.getEnhanceService();
        int got = es != null ? es.forgeTransfer(old, r.from, out, r.to, keepEnhance, returned) : 0;
        List<String> ench = new ArrayList<String>();
        if (keepEnchants) for (Map.Entry<Enchantment, Integer> e : old.getEnchantments().entrySet()) {
            int base = out.getEnchantmentLevel(e.getKey());
            if (e.getValue() > base) { out.addUnsafeEnchantment(e.getKey(), e.getValue()); ench.add(e.getKey().getName() + " " + e.getValue()); }
        }
        p.getInventory().setItemInMainHand(out);
        for (String g : returned) ni.giveNiItem(p, g, 1);
        p.updateInventory();
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(ChatColor.GREEN + "[锻造] 成功！" + ChatColor.WHITE + ni.displayName(r.to) + " +" + got
                + ChatColor.GRAY + (ench.isEmpty() ? "" : "，保留附魔 " + String.join(", ", ench))
                + (returned.isEmpty() ? "" : "，退回宝石 " + String.join(", ", returned)));
        plugin.getLogger().info("forge " + p.getName() + " " + r.from + "+" + lv + " -> " + r.to + "+" + got
                + " ench=" + ench + " returned=" + returned + " coin=-" + r.coin);
        return true;
    }

    private String costText(Player p, Recipe r, boolean withHave) {
        List<String> parts = new ArrayList<String>();
        for (Map.Entry<String, Integer> e : r.cost.entrySet()) {
            String s = ni.displayName(e.getKey()) + "×" + e.getValue();
            if (withHave) {
                int have = ni.countInInventory(p, e.getKey());
                s = (have >= e.getValue() ? ChatColor.GREEN : ChatColor.RED) + s + ChatColor.DARK_GRAY + "(" + have + ")" + ChatColor.GRAY;
            }
            parts.add(s);
        }
        if (r.coin > 0) parts.add(r.coin + " 币");
        return String.join(" + ", parts);
    }

    public boolean isEnabled() { return enabled; }
}
