package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** 余烬同袍 2-piece set from set.yml (blade whitelist + raid ring). */
public final class SetService {

    private final CoreRpgPlugin plugin;
    private final NiBridge ni;

    private boolean enabled = true;
    private String display = "余烬同袍";
    private String loreMark = "§6套装：余烬同袍 ✓";
    private String onKill = "saturation_1";
    private double healPctMax = 2.0;
    private final List<String> blades = new ArrayList<String>();
    private final List<String> rings = new ArrayList<String>();

    public SetService(CoreRpgPlugin plugin, NiBridge ni) {
        this.plugin = plugin;
        this.ni = ni;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "set.yml");
        if (!file.exists()) {
            plugin.saveResource("set.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("set.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        enabled = cfg.getBoolean("enabled", true);
        ConfigurationSection set = cfg.getConfigurationSection("sets.ember_comrade");
        if (set == null) set = cfg.getConfigurationSection("sets.comrade");
        blades.clear();
        rings.clear();
        if (set != null) {
            display = set.getString("display", "余烬同袍");
            loreMark = set.getString("lore_mark", "§6套装：余烬同袍 ✓");
            ConfigurationSection req = set.getConfigurationSection("require");
            if (req != null) {
                addIds(blades, req.getStringList("blade"));
                addIds(rings, req.getStringList("ring"));
            }
            ConfigurationSection eff = set.getConfigurationSection("effect");
            if (eff != null) {
                onKill = eff.getString("on_kill", "saturation_1");
                healPctMax = eff.getDouble("heal_pct_max", 2.0);
            }
        }
        if (blades.isEmpty()) {
            blades.add("gear_ember_blade");
            blades.add("gear_ember_t1_blade");
            blades.add("gear_ember_t2_blade");
            blades.add("gear_ember_t3_blade");
        }
        if (rings.isEmpty()) {
            rings.add("acc_ember_raid_ring");
        }
        if (onKill == null || onKill.isEmpty()) onKill = "saturation_1";
        plugin.getLogger().info("SetService loaded: " + display + " blades=" + blades.size()
                + " on_kill=" + onKill);
    }

    public boolean cmdRoot(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.GRAY + "set 需玩家。enabled=" + enabled
                    + " display=" + display + " on_kill=" + onKill);
            return true;
        }
        Player p = (Player) sender;
        boolean active = isSetActive(p);
        boolean hasBlade = hasAnyNi(p, blades);
        boolean hasRing = hasAnyNi(p, rings);
        p.sendMessage(ChatColor.GOLD + "[套装] " + ChatColor.YELLOW + display
                + (active ? ChatColor.GREEN + "  已激活" : ChatColor.RED + "  未激活"));
        p.sendMessage(ChatColor.GRAY + "  刃 " + (hasBlade ? ChatColor.GREEN + "✓" : ChatColor.DARK_GRAY + "✗")
                + ChatColor.GRAY + "  戒 " + (hasRing ? ChatColor.GREEN + "✓" : ChatColor.DARK_GRAY + "✗")
                + ChatColor.DARK_GRAY + "  背包持有即计件");
        if (active) {
            p.sendMessage(colorize(loreMark));
            p.sendMessage(ChatColor.GRAY + "  击杀效果: " + onKill);
        } else {
            p.sendMessage(ChatColor.DARK_GRAY + "  需要余烬之刃 + 余烬团戒（背包）");
        }
        return true;
    }

    public boolean isSetActive(Player player) {
        if (!enabled || player == null) return false;
        return hasAnyNi(player, blades) && hasAnyNi(player, rings);
    }

    /** Light on-kill proc: Saturation I 1–2s and/or tiny heal, per effect.on_kill. */
    public void onKill(Player killer) {
        if (!isSetActive(killer)) return;
        String kind = onKill == null ? "" : onKill.toLowerCase();
        if (kind.contains("saturation")) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 40, 0, true, false), true);
        }
        if (kind.contains("heal")) {
            applyHeal(killer);
        }
    }

    private void applyHeal(Player killer) {
        double max = killer.getMaxHealth();
        double pct = Math.max(0.0, Math.min(2.0, healPctMax));
        double add = max * (pct / 100.0);
        if (add < 0.2) add = 0.2;
        double next = Math.min(max, killer.getHealth() + add);
        killer.setHealth(next);
    }

    private boolean hasAnyNi(Player player, List<String> ids) {
        if (player == null || ids == null || ids.isEmpty()) return false;
        if (ni == null) return false;
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) return false;
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack == null) continue;
            for (int j = 0; j < ids.size(); j++) {
                if (ni.matchesNiId(stack, ids.get(j))) return true;
            }
        }
        return false;
    }

    private static void addIds(List<String> dest, List<String> raw) {
        if (raw == null) return;
        for (int i = 0; i < raw.size(); i++) {
            String s = raw.get(i);
            if (s != null && !s.trim().isEmpty()) dest.add(s.trim());
        }
    }

    private static String colorize(String raw) {
        if (raw == null) return "";
        return ChatColor.translateAlternateColorCodes('&', raw.replace('§', '&'));
    }
}
