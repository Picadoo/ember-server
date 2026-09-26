package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** 1.8.1: clear-box loot with first-clear-per-week guarantees + chance rolls (loot.yml). */
public final class LootService {
    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge ni;
    private final Random rnd = new Random();
    private FileConfiguration cfg = new YamlConfiguration();

    public LootService(CoreRpgPlugin plugin, PlayerDataStore dataStore, NiBridge ni) {
        this.plugin = plugin; this.dataStore = dataStore; this.ni = ni;
    }

    public void reload() {
        File f = new File(plugin.getDataFolder(), "loot.yml");
        if (!f.exists()) plugin.saveResource("loot.yml", false);
        cfg = YamlConfiguration.loadConfiguration(f);
    }

    public boolean cmd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) { sender.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        if (args.length < 3) { sender.sendMessage("/corerpg loot <player> <key>"); return true; }
        Player p = Bukkit.getPlayerExact(args[1]);
        ConfigurationSection sec = cfg.getConfigurationSection(args[2]);
        if (p == null || sec == null) { sender.sendMessage(ChatColor.RED + "玩家不在线或无此 loot key"); return true; }
        PlayerData d = dataStore.get(p.getUniqueId());
        String week = DailyService.weekId();
        String mark = args[2] + "=" + week;
        List<String> got = new ArrayList<String>();
        ConfigurationSection first = sec.getConfigurationSection("first_per_week");
        boolean firstNow = first != null && !d.getLootWeekMarks().contains(mark);
        if (firstNow) {
            d.addLootWeekMark(args[2], week);
            for (String id : first.getKeys(false)) if (ni.giveNiItem(p, id, first.getInt(id, 1))) got.add(ni.displayName(id));
        } else {
            ConfigurationSection ch = sec.getConfigurationSection("chance");
            if (ch != null) for (String id : ch.getKeys(false)) if (rnd.nextDouble() < ch.getDouble(id)) { if (ni.giveNiItem(p, id, 1)) got.add(ni.displayName(id)); }
        }
        dataStore.flushMutation(p.getUniqueId());
        String label = sec.getString("label", args[2]);
        if (!got.isEmpty()) p.sendMessage(ChatColor.GOLD + "[" + label + "] " + (firstNow ? "本周首通保底：" : "幸运掉落：") + ChatColor.WHITE + String.join("，", got));
        else if (first != null) p.sendMessage(ChatColor.GRAY + "[" + label + "] 本周首通保底已领；本次无额外掉落。");
        sender.sendMessage("[CoreRpg] loot " + p.getName() + " " + args[2] + " → " + (got.isEmpty() ? "-" : String.join(",", got)) + (firstNow ? " (weekly first)" : ""));
        return true;
    }
}
