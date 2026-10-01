package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    /** 与菜单 lore 一致：出本后再进约等 5 秒（缓存冷却），不是进本坏了 */
    private static final String COOLDOWN_TELL =
            ChatColor.YELLOW + "出本后再进约等 5 秒（缓存冷却），不是进本坏了";
    /** DP start-interval 约 5s；本地短窗用于判断「短时拒进」提示 */
    private static final long COOLDOWN_WINDOW_MS = 5500L;
    private static final long ENTER_VERIFY_TICKS = 40L;

    private final Map<UUID, Long> lastTryEnterMs = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, Long> lastOkEnterMs = new ConcurrentHashMap<UUID, Long>();

    public TicketEntryService(CoreRpgPlugin plugin, PlayerDataStore dataStore, NiBridge ni) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.ni = ni;
    }

    public void reload() { /* costs live in StaminaService / cash.yml */ }

    /**
     * 校验门槛 → 扣体力（或周免费抵扣）→ {@code dp start-console}。
     * 启动失败则退还体力/抵扣。OP/admin 免扣免等级（测本）。
     * DP 冷却拒进常仍返回 dispatch=true：延迟校验未进本则退还，并人话提示缓存冷却（与菜单 lore 一致）。
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

        final UUID pid = player.getUniqueId();
        final long now = System.currentTimeMillis();
        Long prevTry = lastTryEnterMs.get(pid);
        Long prevOk = lastOkEnterMs.get(pid);
        final boolean recent = (prevTry != null && now - prevTry.longValue() < COOLDOWN_WINDOW_MS)
                || (prevOk != null && now - prevOk.longValue() < COOLDOWN_WINDOW_MS);
        lastTryEnterMs.put(pid, Long.valueOf(now));

        String cmd = "dp start-console " + player.getName() + " " + kind.dungeonId;
        boolean ok;
        try {
            ok = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        } catch (Throwable t) {
            plugin.getLogger().warning("[TicketEntry] start-console threw: " + cmd + " · " + t.getMessage());
            ok = false;
        }
        if (!ok) {
            boolean refunded = false;
            if (!op && stamina != null && consumed != null) {
                stamina.refundEnter(player, kind, consumed);
                refunded = true;
            }
            tellEnterFailed(player, recent, refunded ? consumed : null);
            plugin.getLogger().warning("[TicketEntry] start-console failed: " + cmd);
            return true;
        }

        // DP 可能异步进本 / 冷却拒进仍 dispatch=true —— 2s 后校验；无效果则退还并人话提示
        final boolean opF = op;
        final Kind kindF = kind;
        final StaminaService.ConsumeResult consumedF = consumed;
        final StaminaService staminaF = stamina;
        final boolean recentF = recent;
        if (looksInDungeon(player)) {
            lastOkEnterMs.put(pid, Long.valueOf(System.currentTimeMillis()));
            return true;
        }
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                Player p = Bukkit.getPlayer(pid);
                if (p == null || !p.isOnline()) return;
                if (looksInDungeon(p)) {
                    lastOkEnterMs.put(pid, Long.valueOf(System.currentTimeMillis()));
                    return;
                }
                boolean refunded = false;
                if (!opF && staminaF != null && consumedF != null) {
                    staminaF.refundEnter(p, kindF, consumedF);
                    refunded = true;
                }
                tellEnterFailed(p, recentF, refunded ? consumedF : null);
                plugin.getLogger().info("[TicketEntry] start no-effect (likely interval): "
                        + p.getName() + " " + kindF.dungeonId);
            }
        }, ENTER_VERIFY_TICKS);
        return true;
    }

    /**
     * B2.119: DP 无稳定回传。短时重试 → 缓存冷却提示；否则（多为 DP 条件拒进：人数 / 等级等，DP 已在上方说明）
     * 不再误报「缓存冷却」。退还时按实际扣法说明：本周免费抵扣 vs 体力；OP / 未扣则不提退还。
     */
    private void tellEnterFailed(Player player, boolean recentCooldown, StaminaService.ConsumeResult refunded) {
        if (player == null) return;
        if (recentCooldown) {
            player.sendMessage(COOLDOWN_TELL);
            player.sendMessage(ChatColor.GRAY + "若上方另有原因（如人数、等级），以上方为准");
        } else {
            player.sendMessage(ChatColor.YELLOW + "没能进本：原因见上方提示（如人数、等级）；出本后立刻再进需等约 5 秒");
        }
        if (refunded != null && refunded.usedCredit) {
            player.sendMessage(ChatColor.GRAY + "本周首次免费次数已退还");
        } else if (refunded != null && refunded.cost > 0) {
            player.sendMessage(ChatColor.GRAY + "体力 " + refunded.cost + " 已退还");
        }
    }

    /** DP 实例世界名一般为 dungeon_&lt;DungeonId&gt;_…；亦认 QuestService 非静态世界。 */
    private boolean looksInDungeon(Player player) {
        if (player == null) return false;
        World w = player.getWorld();
        if (w == null) return false;
        String n = w.getName();
        if (n != null && n.startsWith("dungeon_")) return true;
        QuestService qs = plugin.getQuestService();
        return qs != null && qs.isInstanceWorld(w);
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
