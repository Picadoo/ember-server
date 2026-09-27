package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * B0.1 — 进本扣票按 NI id（consumeExact），再 console {@code dp start-console}。
 * 不再依赖 DP {@code <item:显示名>}；玩家主路径走 TrMenu → /corerpg enter &lt;kind&gt;。
 */
public final class TicketEntryService {

    public enum Kind {
        DAILY("daily", "EmberDaily", "ticket_ember_daily", "余烬日票", "daily", "日票"),
        WEEKLY("weekly", "EmberWeekly", "ticket_ember_weekly", "余烬周票", "weekly", "周票"),
        ABYSS("abyss", "EmberAbyss", "ticket_ember_abyss", "余烬深渊票", "abyss", "深渊票"),
        RAID("raid", "EmberRaid", "ticket_ember_raid", "余烬团本票", "raid", "团本票"),
        ELITE("elite", "EmberEliteWeekly", "ticket_ember_elite", "余烬精英票", "elite", "精英票");

        final String key;
        final String dungeonId;
        final String ticketNiId;
        final String displayHint;
        final String gateId;
        final String shortLabel;

        Kind(String key, String dungeonId, String ticketNiId, String displayHint, String gateId, String shortLabel) {
            this.key = key;
            this.dungeonId = dungeonId;
            this.ticketNiId = ticketNiId;
            this.displayHint = displayHint;
            this.gateId = gateId;
            this.shortLabel = shortLabel;
        }

        static Kind parse(String raw) {
            if (raw == null) return null;
            String s = raw.toLowerCase().trim();
            for (Kind k : values()) {
                if (k.key.equals(s) || k.dungeonId.equalsIgnoreCase(raw)
                        || k.ticketNiId.equalsIgnoreCase(raw)
                        || k.shortLabel.equals(raw)) {
                    return k;
                }
            }
            if ("日".equals(raw) || "日常".equals(raw)) return DAILY;
            if ("周".equals(raw) || "周常".equals(raw)) return WEEKLY;
            if ("深渊".equals(raw)) return ABYSS;
            if ("团".equals(raw) || "团本".equals(raw)) return RAID;
            if ("精英".equals(raw) || "eliteweekly".equals(s)) return ELITE;
            return null;
        }
    }

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge ni;

    public TicketEntryService(CoreRpgPlugin plugin, PlayerDataStore dataStore, NiBridge ni) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.ni = ni;
    }

    public void reload() { /* ticket ids from cash.yml via TicketGrantService; Kind 常量稳定 */ }

    /**
     * 校验门槛 →（非 OP）consumeExact → {@code dp start-console <player> <dungeon>}。
     * 启动失败则尝试退票。OP/admin 免票免等级（测本）。
     */
    public boolean tryEnter(Player player, Kind kind) {
        if (player == null || kind == null) return true;
        PlayerData data = dataStore.get(player.getUniqueId());
        boolean op = player.isOp() || player.hasPermission("corerpg.admin");

        if (!op) {
            ProgressService ps = plugin.getProgressService();
            int need = ps != null ? ps.gateLevel(kind.gateId) : 0;
            if (need > 0 && data.getEmberLevel() < need) {
                player.sendMessage(ChatColor.RED + kind.displayHint.replace("票", "本")
                        + "需要余烬 Lv." + need
                        + ChatColor.GRAY + "（当前 Lv." + data.getEmberLevel() + "）");
                return true;
            }
            if (kind == Kind.ELITE) {
                EliteService elite = plugin.getEliteService();
                if (elite != null && elite.isClearedThisWeek(data)) {
                    player.sendMessage(ChatColor.RED + "本周已通关精英试炼，下周再来");
                    return true;
                }
            }
            int have = ni == null ? 0 : ni.countInInventory(player, kind.ticketNiId);
            if (have < 1) {
                player.sendMessage(ChatColor.RED + "缺少" + kind.displayHint
                        + ChatColor.GRAY + "×1（进本即扣，不返还）");
                return true;
            }
            if (!ni.consumeExact(player, kind.ticketNiId, 1)) {
                player.sendMessage(ChatColor.RED + "扣票失败，请稍后重试");
                return true;
            }
        }

        player.sendMessage(ChatColor.YELLOW + "[" + kind.shortLabel + "] "
                + ChatColor.GRAY + "正在进入……");
        String cmd = "dp start-console " + player.getName() + " " + kind.dungeonId;
        boolean ok = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        if (!ok) {
            if (!op && ni != null) {
                ni.giveNiItem(player, kind.ticketNiId, 1);
            }
            player.sendMessage(ChatColor.RED + "无法启动副本，票已退回（若已扣）· 请稍后再试");
            plugin.getLogger().warning("[TicketEntry] start-console failed: " + cmd);
        }
        return true;
    }

    /** /corerpg enter &lt;daily|weekly|abyss|raid|elite&gt; */
    public boolean cmdEnter(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("玩家专用：/corerpg enter <daily|weekly|abyss|raid|elite>");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "/corerpg enter <daily|weekly|abyss|raid|elite>");
            return true;
        }
        Kind kind = Kind.parse(args[1]);
        if (kind == null) {
            sender.sendMessage(ChatColor.RED + "未知副本：" + args[1]
                    + ChatColor.GRAY + " · daily/weekly/abyss/raid/elite");
            return true;
        }
        return tryEnter((Player) sender, kind);
    }

    /**
     * /corerpg ticket consume &lt;niId&gt; [amount] — 管理测扣；玩家勿当进本入口。
     * 无 sub 时仍由 Plugin 走票览。
     */
    public boolean cmdConsume(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin") && !(sender instanceof org.bukkit.command.ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin（玩家进本请点菜单 /corerpg enter）");
            return true;
        }
        // args: ticket consume <player> <niId> [n]  OR  ticket consume <niId> [n] (self)
        if (args.length < 3) {
            sender.sendMessage(ChatColor.YELLOW + "/corerpg ticket consume [player] <niId> [amount]");
            return true;
        }
        Player target;
        String niId;
        int amount = 1;
        int idx = 2;
        Player maybe = Bukkit.getPlayerExact(args[2]);
        if (maybe != null && args.length >= 4) {
            target = maybe;
            niId = args[3];
            idx = 4;
        } else if (sender instanceof Player) {
            target = (Player) sender;
            niId = args[2];
            idx = 3;
        } else {
            sender.sendMessage(ChatColor.YELLOW + "控制台：/corerpg ticket consume <player> <niId> [amount]");
            return true;
        }
        if (args.length > idx) {
            try { amount = Integer.parseInt(args[idx]); } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "数量无效");
                return true;
            }
        }
        if (ni == null || target == null) {
            sender.sendMessage(ChatColor.RED + "NI/玩家未就绪");
            return true;
        }
        boolean ok = ni.consumeExact(target, niId, amount);
        sender.sendMessage((ok ? ChatColor.GREEN : ChatColor.RED)
                + "[ticket] consume " + target.getName() + " " + niId + " ×" + amount
                + " → " + (ok ? "OK" : "FAIL(不足)"));
        return true;
    }
}
