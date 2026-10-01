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

    /**
     * Placeholder / DP gate: Lv≥40 + 本周未通关。
     * S0: 体力 / 本周免费抵扣由 TicketEntryService 在 start 前扣；此处只判等级 + 本周未通关
     *（否则扣过后再跑 %corerpg_gate_elite% 会假失败）。
     */
    public boolean passesGate(Player player, PlayerData data) {
        if (player == null || data == null) return false;
        ProgressService ps = plugin.getProgressService();
        int need = ps != null ? ps.gateLevel("elite") : GATE_LEVEL;
        if (data.getEmberLevel() < need) return false;
        if (isClearedThisWeek(data)) return false;
        return true;
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
     * /corerpg elite start — S0：TicketEntryService 扣体力 / 本周免费抵扣后 console start-console.
     */
    public boolean cmdStart(Player player) {
        TicketEntryService entry = plugin.getTicketEntryService();
        if (entry == null) {
            player.sendMessage(ChatColor.RED + "[精英试炼] 进本服务未就绪");
            return true;
        }
        return entry.tryEnter(player, TicketEntryService.Kind.ELITE);
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
            StaminaService st = plugin.getStaminaService();
            p.sendMessage(ChatColor.GOLD + "[精英试炼] " + ChatColor.GRAY + "本周 " + DailyService.weekId()
                    + " · 门槛 Lv." + need + " · 当前 Lv." + d.getEmberLevel());
            String cost = st == null ? "体力服务未就绪"
                    : "进本 §f" + st.costOf("elite") + "§7 体力 · 体力 §f" + st.getStamina(d) + "§7/§f" + st.getMax(d)
                    + "§7 · 本周免费 §f×" + d.getWeeklyGrantCreditElite();
            p.sendMessage(ChatColor.GRAY + "  " + cost);
            p.sendMessage(ChatColor.GRAY + "  本周已通关："
                    + (isClearedThisWeek(d) ? ChatColor.RED + "是" : ChatColor.GREEN + "否")
                    + ChatColor.GRAY + " · 门控："
                    + (passesGate(p, d) ? ChatColor.GREEN + "yes" : ChatColor.YELLOW + "no"));
            return true;
        }
        sender.sendMessage("/corerpg elite start|weekly-first <player>|status");
        return true;
    }
}
