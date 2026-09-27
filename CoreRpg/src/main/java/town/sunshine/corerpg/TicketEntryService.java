package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * S0 — 进本扣余烬体力（StaminaService），失败退还；替换原 NI 票路径。
 * 玩家主路径：TrMenu → /corerpg enter &lt;kind&gt;。
 */
public final class TicketEntryService {

    public enum Kind {
        DAILY("daily", "EmberDaily", "ticket_ember_daily", "余烬日常", "daily", "日常"),
        DAILY_ASH("daily_ash", "EmberDailyAsh", "ticket_ember_daily", "余烬窟·焦骨甬道", "daily", "焦骨"),
        DAILY_CRYPT("daily_crypt", "EmberDailyCrypt", "ticket_ember_daily", "余烬窟·残誓地窖", "daily", "残誓"),
        DAILY_TIDE("daily_tide", "EmberDailyTide", "ticket_ember_daily", "余烬窟·潮蚀水道", "daily", "潮蚀"),
        DAILY_SPIRE("daily_spire", "EmberDailySpire", "ticket_ember_daily", "余烬窟·断塔回廊", "daily", "断塔"),
        DAILY_FROST("daily_frost", "EmberDailyFrost", "ticket_ember_daily", "余烬窟·霜晶裂隙", "daily", "霜晶"),
        DAILY_RAIL("daily_rail", "EmberDailyRail", "ticket_ember_daily", "余烬窟·锈轨矿道", "daily", "锈轨"),
        WEEKLY("weekly", "EmberWeekly", "ticket_ember_weekly", "余烬周本", "weekly", "周本"),
        ABYSS("abyss", "EmberAbyss", "ticket_ember_abyss", "余烬深渊", "abyss", "深渊"),
        RAID("raid", "EmberRaid", "ticket_ember_raid", "余烬团本", "raid", "团本"),
        ELITE("elite", "EmberEliteWeekly", "ticket_ember_elite", "余烬精英", "elite", "精英");

        final String key;
        final String dungeonId;
        final String ticketNiId; // legacy id (migration only)
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
            if ("ash".equals(s) || "焦骨".equals(raw) || "焦骨甬道".equals(raw)) return DAILY_ASH;
            if ("crypt".equals(s) || "残誓".equals(raw) || "残誓地窖".equals(raw)) return DAILY_CRYPT;
            if ("tide".equals(s) || "潮蚀".equals(raw) || "潮蚀水道".equals(raw)) return DAILY_TIDE;
            if ("spire".equals(s) || "断塔".equals(raw) || "断塔回廊".equals(raw)) return DAILY_SPIRE;
            if ("frost".equals(s) || "霜晶".equals(raw) || "霜晶裂隙".equals(raw)) return DAILY_FROST;
            if ("rail".equals(s) || "锈轨".equals(raw) || "锈轨矿道".equals(raw)) return DAILY_RAIL;
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

    public void reload() { /* costs live in StaminaService / cash.yml */ }

    /**
     * 校验门槛 → 扣体力（或周免费抵扣）→ {@code dp start-console}。
     * 启动失败则退还体力/抵扣。OP/admin 免扣免等级（测本）。
     */
    public boolean tryEnter(Player player, Kind kind) {
        if (player == null || kind == null) return true;
        PlayerData data = dataStore.get(player.getUniqueId());
        boolean op = player.isOp() || player.hasPermission("corerpg.admin");
        StaminaService stamina = plugin.getStaminaService();

        if (!op) {
            ProgressService ps = plugin.getProgressService();
            int need = ps != null ? ps.gateLevel(kind.gateId) : 0;
            if (need > 0 && data.getEmberLevel() < need) {
                player.sendMessage(ChatColor.RED + kind.displayHint
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
        }

        StaminaService.ConsumeResult consumed = null;
        if (!op) {
            if (stamina == null) {
                player.sendMessage(ChatColor.RED + "体力服务未就绪");
                return true;
            }
            consumed = stamina.consumeForEnter(player, kind);
            if (!consumed.ok) {
                player.sendMessage(consumed.failMessage != null ? consumed.failMessage
                        : ChatColor.RED + "体力不足");
                return true;
            }
        }

        String costHint;
        if (op) {
            costHint = "管理免扣";
        } else if (consumed != null && consumed.usedCredit) {
            costHint = "本周首次免费";
        } else if (consumed != null && consumed.cost > 0) {
            costHint = "体力 -" + consumed.cost;
        } else {
            costHint = "无消耗";
        }
        player.sendMessage(ChatColor.YELLOW + "[" + kind.shortLabel + "] "
                + ChatColor.GRAY + "正在进入……（" + costHint + "）");
        String cmd = "dp start-console " + player.getName() + " " + kind.dungeonId;
        boolean ok = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        if (!ok) {
            if (!op && stamina != null && consumed != null) {
                stamina.refundEnter(player, kind, consumed);
            }
            player.sendMessage(ChatColor.RED + "无法启动副本，体力已退还（若已扣）· 请稍后再试");
            plugin.getLogger().warning("[TicketEntry] start-console failed: " + cmd);
        }
        return true;
    }

    /** /corerpg enter &lt;daily|daily_ash|daily_crypt|daily_tide|daily_spire|daily_frost|daily_rail|weekly|abyss|raid|elite&gt; */
    public boolean cmdEnter(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("玩家专用：/corerpg enter <daily|daily_ash|daily_crypt|daily_tide|daily_spire|daily_frost|daily_rail|weekly|abyss|raid|elite>");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "/corerpg enter <daily|daily_ash|daily_crypt|daily_tide|daily_spire|daily_frost|daily_rail|weekly|abyss|raid|elite>");
            return true;
        }
        Kind kind = Kind.parse(args[1]);
        if (kind == null) {
            sender.sendMessage(ChatColor.RED + "未知副本：" + args[1]
                    + ChatColor.GRAY + " · daily/daily_ash/daily_crypt/daily_tide/daily_spire/daily_frost/daily_rail/weekly/abyss/raid/elite");
            return true;
        }
        return tryEnter((Player) sender, kind);
    }

    /**
     * /corerpg ticket consume &lt;niId&gt; [amount] — 管理测扣旧票；玩家勿当进本入口。
     */
    public boolean cmdConsume(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin") && !(sender instanceof org.bukkit.command.ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin（玩家进本请点菜单）");
            return true;
        }
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
