package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Stage 4.4 EmberEliteWeekly — gate / start / weekly-first stable charm.
 * Player entry: TrMenu → /corerpg elite start（不暴露 /dp start）.
 */
public final class EliteService {

    public static final String DUNGEON_ID = "EmberEliteWeekly";
    public static final String TICKET_NI = "ticket_ember_elite";
    public static final String CLEAR_MARK = "elite_weekly_clear";
    public static final String FIRST_MARK = "elite_weekly_first";
    public static final String STABLE_CHARM = "mat_ember_stable_charm";
    public static final int GATE_LEVEL = 40;

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge ni;

    public EliteService(CoreRpgPlugin plugin, PlayerDataStore dataStore, NiBridge ni) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.ni = ni;
    }

    public void reload() {
        // config lives in cash.yml (ticket grant) + progress.yml (level_gates.elite / sources)
    }

    /** Placeholder / DP gate: Lv≥40 + 本周未通关 + 持有精英票. */
    public boolean passesGate(Player player, PlayerData data) {
        if (player == null || data == null) return false;
        ProgressService ps = plugin.getProgressService();
        int need = ps != null ? ps.gateLevel("elite") : GATE_LEVEL;
        if (data.getEmberLevel() < need) return false;
        if (isClearedThisWeek(data)) return false;
        int tickets = ni == null ? 0 : ni.countInInventory(player, TICKET_NI);
        return tickets >= 1;
    }

    public boolean isClearedThisWeek(PlayerData data) {
        if (data == null) return false;
        String mark = CLEAR_MARK + "=" + DailyService.weekId();
        return data.getLootWeekMarks().contains(mark);
    }

    public void markClearedThisWeek(PlayerData data) {
        if (data == null) return;
        data.addLootWeekMark(CLEAR_MARK, DailyService.weekId());
    }

    /**
     * /corerpg elite start — 校验后以玩家身份启动 DP（由 DP 扣票）.
     * OP 可绕过等级/通关/票门槛（仍走 dp start，便于管理测试）.
     */
    public boolean cmdStart(Player player) {
        if (player == null) return true;
        PlayerData data = dataStore.get(player.getUniqueId());
        boolean op = player.isOp() || player.hasPermission("corerpg.admin");
        if (!op) {
            ProgressService ps = plugin.getProgressService();
            int need = ps != null ? ps.gateLevel("elite") : GATE_LEVEL;
            if (data.getEmberLevel() < need) {
                player.sendMessage(ChatColor.RED + "精英试炼需要余烬 Lv." + need
                        + ChatColor.GRAY + "（当前 Lv." + data.getEmberLevel() + "）");
                return true;
            }
            if (isClearedThisWeek(data)) {
                player.sendMessage(ChatColor.RED + "本周已通关精英试炼，下周再来");
                return true;
            }
            int tickets = ni == null ? 0 : ni.countInInventory(player, TICKET_NI);
            if (tickets < 1) {
                player.sendMessage(ChatColor.RED + "缺少余烬精英票"
                        + ChatColor.GRAY + "（每周一发放 1 张，持有上限 1，进本即扣）");
                return true;
            }
        }
        player.sendMessage(ChatColor.YELLOW + "[精英试炼] " + ChatColor.GRAY + "正在进入……");
        boolean ok = Bukkit.dispatchCommand(player, "dp start " + DUNGEON_ID);
        if (!ok) {
            player.sendMessage(ChatColor.RED + "无法启动精英试炼，请稍后再试或联系管理");
        }
        return true;
    }

    /**
     * /corerpg elite weekly-first &lt;player&gt; — 本周首通稳定符 ×1（lootWeekMarks elite_weekly_first）.
     * 静默跳过已领；不进 MM 掉落。
     */
    public boolean cmdWeeklyFirst(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin") && !(sender instanceof org.bukkit.command.ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage("/corerpg elite weekly-first <player>");
            return true;
        }
        Player p = Bukkit.getPlayerExact(args[2]);
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "玩家不在线：" + args[2]);
            return true;
        }
        PlayerData data = dataStore.get(p.getUniqueId());
        String week = DailyService.weekId();
        String mark = FIRST_MARK + "=" + week;
        if (data.getLootWeekMarks().contains(mark)) {
            sender.sendMessage("[CoreRpg] elite weekly-first " + p.getName() + " → already (" + week + ")");
            return true;
        }
        data.addLootWeekMark(FIRST_MARK, week);
        boolean given = ni != null && ni.giveNiItem(p, STABLE_CHARM, 1);
        dataStore.flushMutation(p.getUniqueId());
        if (given) {
            p.sendMessage(ChatColor.GREEN + "[精英试炼] 本周首通，获得稳定符 ×1");
        } else {
            plugin.getLogger().warning("[精英试炼] weekly-first charm grant failed for " + p.getName());
        }
        sender.sendMessage("[CoreRpg] elite weekly-first " + p.getName() + " → " + (given ? "charm×1" : "FAIL") + " (" + week + ")");
        return true;
    }

    /** /corerpg elite [start|weekly-first|status] */
    public boolean cmdRoot(CommandSender sender, String[] args) {
        if (args.length < 2) {
            if (sender instanceof Player) return cmdStart((Player) sender);
            sender.sendMessage("/corerpg elite start|weekly-first <player>|status");
            return true;
        }
        String sub = args[1].toLowerCase();
        if ("start".equals(sub)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("玩家专用：/corerpg elite start");
                return true;
            }
            return cmdStart((Player) sender);
        }
        if ("weekly-first".equals(sub) || "weeklyfirst".equals(sub) || "first".equals(sub)) {
            return cmdWeeklyFirst(sender, args);
        }
        if ("status".equals(sub)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("玩家专用");
                return true;
            }
            Player p = (Player) sender;
            PlayerData d = dataStore.get(p.getUniqueId());
            ProgressService ps = plugin.getProgressService();
            int need = ps != null ? ps.gateLevel("elite") : GATE_LEVEL;
            int tickets = ni == null ? 0 : ni.countInInventory(p, TICKET_NI);
            p.sendMessage(ChatColor.GOLD + "[精英试炼] " + ChatColor.GRAY + "本周 " + DailyService.weekId()
                    + " · 门槛 Lv." + need + " · 当前 Lv." + d.getEmberLevel());
            p.sendMessage(ChatColor.AQUA + "  精英票 §f" + tickets
                    + ChatColor.GRAY + " · 本周已通关："
                    + (isClearedThisWeek(d) ? ChatColor.RED + "是" : ChatColor.GREEN + "否")
                    + ChatColor.GRAY + " · 门控："
                    + (passesGate(p, d) ? ChatColor.GREEN + "yes" : ChatColor.YELLOW + "no"));
            return true;
        }
        sender.sendMessage("/corerpg elite start|weekly-first <player>|status");
        return true;
    }
}
