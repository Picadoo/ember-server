package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 1.5.0 (2026-09-26): second vanilla-level source + season-pass XP. Config: progress.yml.
 *  - /corerpg xpreward <player> <elite|boss>  (console/admin; MM onDeath) → vanilla levels, daily cap
 *  - /corerpg passxp <player> <source>          (console/admin; DP clear rewards) → pass XP, daily cap
 *  - sign-in grants pass XP internally.
 */
public final class ProgressService {

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;

    private boolean killLevelsEnabled = true;
    private final Map<String, Integer> killLevels = new LinkedHashMap<String, Integer>();
    private int killLevelsDailyCap = 6;

    private boolean passEnabled = true;
    private final Map<String, Integer> passSources = new LinkedHashMap<String, Integer>();
    private int passDailyCap = 100;
    private int passXpPerLevel = 100;
    private int passMaxLevel = 30;

    public ProgressService(CoreRpgPlugin plugin, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "progress.yml");
        if (!file.exists()) {
            plugin.saveResource("progress.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        killLevelsEnabled = cfg.getBoolean("kill_levels.enabled", true);
        killLevelsDailyCap = Math.max(0, cfg.getInt("kill_levels.daily_cap", 6));
        killLevels.clear();
        ConfigurationSection kl = cfg.getConfigurationSection("kill_levels.levels");
        if (kl != null) {
            for (String k : kl.getKeys(false)) killLevels.put(k.toLowerCase(), Math.max(0, kl.getInt(k)));
        } else {
            killLevels.put("elite", 1);
            killLevels.put("boss", 2);
        }
        passEnabled = cfg.getBoolean("pass_xp.enabled", true);
        passDailyCap = Math.max(0, cfg.getInt("pass_xp.daily_cap", 100));
        passXpPerLevel = Math.max(1, cfg.getInt("pass_xp.xp_per_level", 100));
        passMaxLevel = Math.max(1, cfg.getInt("pass_xp.max_level", 30));
        passSources.clear();
        ConfigurationSection ps = cfg.getConfigurationSection("pass_xp.sources");
        if (ps != null) {
            for (String k : ps.getKeys(false)) passSources.put(k.toLowerCase(), Math.max(0, ps.getInt(k)));
        } else {
            passSources.put("sign", 10);
            passSources.put("daily_clear", 20);
            passSources.put("weekly_clear", 40);
        }
    }

    // ---------------- kill levels ----------------

    /** @return levels actually granted (after cap). */
    public int grantKillLevels(Player p, String kind) {
        if (!killLevelsEnabled || p == null) return 0;
        Integer want = killLevels.get(kind == null ? "" : kind.toLowerCase());
        if (want == null || want <= 0) return 0;
        PlayerData d = dataStore.get(p.getUniqueId());
        String today = DailyService.today();
        if (!today.equals(d.getKillLevelsDate())) {
            d.setKillLevelsDate(today);
            d.setKillLevelsToday(0);
        }
        int left = killLevelsDailyCap - d.getKillLevelsToday();
        int give = Math.min(want, Math.max(0, left));
        if (give <= 0) {
            dataStore.flushMutation(p.getUniqueId());
            return 0;
        }
        d.setKillLevelsToday(d.getKillLevelsToday() + give);
        dataStore.flushMutation(p.getUniqueId());
        p.giveExpLevels(give);
        p.sendMessage(ChatColor.GREEN + "[余烬] 击杀" + ("boss".equalsIgnoreCase(kind) ? "首领" : "精英")
                + " · 经验等级 +" + give + ChatColor.GRAY + "（今日 " + d.getKillLevelsToday() + "/" + killLevelsDailyCap + "）");
        return give;
    }

    // ---------------- pass xp ----------------

    public int passLevel(PlayerData d) {
        return Math.min(passMaxLevel, d.getPassXp() / passXpPerLevel);
    }

    /** @return xp actually granted (after cap). */
    public int grantPassXp(Player p, String source) {
        if (!passEnabled || p == null) return 0;
        Integer want = passSources.get(source == null ? "" : source.toLowerCase());
        if (want == null || want <= 0) return 0;
        PlayerData d = dataStore.get(p.getUniqueId());
        String today = DailyService.today();
        if (!today.equals(d.getPassXpDate())) {
            d.setPassXpDate(today);
            d.setPassXpToday(0);
        }
        int give = Math.min(want, Math.max(0, passDailyCap - d.getPassXpToday()));
        if (give <= 0) {
            p.sendMessage(ChatColor.GRAY + "[战令] 今日战令经验已达上限 " + passDailyCap);
            dataStore.flushMutation(p.getUniqueId());
            return 0;
        }
        int before = passLevel(d);
        d.setPassXpToday(d.getPassXpToday() + give);
        d.setPassXp(d.getPassXp() + give);
        dataStore.flushMutation(p.getUniqueId());
        int after = passLevel(d);
        p.sendMessage(ChatColor.AQUA + "[战令] 经验 +" + give + ChatColor.GRAY + "（Lv." + after + " · "
                + (d.getPassXp() % passXpPerLevel) + "/" + passXpPerLevel + " · 今日 " + d.getPassXpToday() + "/" + passDailyCap + "）");
        if (after > before) {
            p.sendMessage(ChatColor.GOLD + "[战令] 升级！赛季等级 Lv." + after);
        }
        return give;
    }

    public String passLine(PlayerData d) {
        return "赛季等级 Lv." + passLevel(d) + " · 经验 " + (d.getPassXp() % passXpPerLevel) + "/" + passXpPerLevel
                + " · 今日 " + (DailyService.today().equals(d.getPassXpDate()) ? d.getPassXpToday() : 0) + "/" + passDailyCap;
    }

    // ---------------- commands ----------------

    /** /corerpg xpreward <player> <elite|boss> · /corerpg passxp <player> <source> */
    public boolean cmdAdminGrant(CommandSender sender, String[] args, boolean pass) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(pass ? "/corerpg passxp <player> <" + String.join("|", passSources.keySet()) + ">"
                    : "/corerpg xpreward <player> <" + String.join("|", killLevels.keySet()) + ">");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "玩家不在线：" + args[1]);
            return true;
        }
        int got = pass ? grantPassXp(target, args[2]) : grantKillLevels(target, args[2]);
        if (!(sender instanceof Player) || sender != target) {
            sender.sendMessage("[CoreRpg] " + (pass ? "passxp " : "xpreward ") + target.getName() + " " + args[2] + " → +" + got);
        }
        return true;
    }
}
