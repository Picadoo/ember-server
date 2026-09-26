package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Weekly first-clear raid ring (raidRingWeek = DailyService.weekId()).
 * Reads raid_ring.* from set.yml.
 */
public final class RaidService {

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge ni;

    private String ringNiId = "acc_ember_raid_ring";
    private boolean weeklyFirst = true;

    public RaidService(CoreRpgPlugin plugin, PlayerDataStore dataStore, NiBridge ni) {
        this.plugin = plugin;
        this.dataStore = dataStore;
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
        ringNiId = cfg.getString("raid_ring.ni_id", "acc_ember_raid_ring");
        if (ringNiId == null || ringNiId.isEmpty()) ringNiId = "acc_ember_raid_ring";
        weeklyFirst = cfg.getBoolean("raid_ring.weekly_first", true);
    }

    public boolean cmdRoot(CommandSender sender, String[] args) {
        String act = args.length >= 2 ? args[1].toLowerCase() : "status";
        if ("grant-ring".equals(act) || "grantring".equals(act)) {
            if (!sender.hasPermission("corerpg.admin")) {
                sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.YELLOW + "/corerpg raid grant-ring <player>");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[2]);
            if (target == null) target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "玩家不在线: " + args[2]);
                return true;
            }
            grantRing(sender, target);
            return true;
        }
        if ("ring".equals(act) || "claim-ring".equals(act) || "claim".equals(act)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.YELLOW + "控制台请用 /corerpg raid grant-ring <player>");
                return true;
            }
            Player p = (Player) sender;
            if (sender.hasPermission("corerpg.admin") || isInRaidWorld(p)) {
                grantRing(sender, p);
            } else {
                sendRingStatus(p);
                p.sendMessage(ChatColor.DARK_GRAY + "团戒由通关箱结算（或在本内 /corerpg raid claim-ring）");
            }
            return true;
        }
        if (sender instanceof Player) sendRingStatus((Player) sender);
        else sender.sendMessage(ChatColor.GRAY + "raid grant-ring <player> · week=" + DailyService.weekId());
        return true;
    }

    public boolean grantRing(CommandSender notifier, Player target) {
        if (target == null || !target.isOnline()) {
            if (notifier != null) notifier.sendMessage(ChatColor.RED + "玩家不在线");
            return false;
        }
        String week = DailyService.weekId();
        PlayerData data = dataStore.get(target.getUniqueId());
        if (weeklyFirst && week.equals(data.getRaidRingWeek())) {
            String msg = ChatColor.DARK_GRAY + "本周团戒已领取";
            target.sendMessage(msg);
            if (notifier != null && notifier != target) {
                notifier.sendMessage(ChatColor.YELLOW + target.getName() + " " + msg);
            }
            return false;
        }
        boolean ok = ni != null && ni.giveNiItem(target, ringNiId, 1);
        if (!ok) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "ni give " + target.getName() + " " + ringNiId + " 1");
        }
        data.setRaidRingWeek(week);
        dataStore.flushMutation(target.getUniqueId());
        target.sendMessage(ChatColor.GOLD + "[团本] " + ChatColor.YELLOW
                + "获得余烬团戒（周首通 " + week + "）");
        if (notifier != null && notifier != target) {
            notifier.sendMessage(ChatColor.GREEN + "已发放 " + ringNiId + " → " + target.getName()
                    + " week=" + week);
        }
        plugin.getLogger().info("raid grant-ring " + target.getName() + " week=" + week);
        return true;
    }

    public void sendRingStatus(Player player) {
        String week = DailyService.weekId();
        PlayerData data = dataStore.get(player.getUniqueId());
        boolean claimed = week.equals(data.getRaidRingWeek());
        player.sendMessage(ChatColor.AQUA + "[团本] " + ChatColor.GRAY + "本周 " + week
                + (claimed ? ChatColor.YELLOW + "  团戒已领" : ChatColor.GREEN + "  团戒未领"));
    }

    public static boolean isInRaidWorld(Player player) {
        if (player == null) return false;
        World w = player.getWorld();
        if (w == null) return false;
        String n = w.getName().toLowerCase().replace("-", "").replace("_", "");
        return n.contains("emberraid");
    }
}
