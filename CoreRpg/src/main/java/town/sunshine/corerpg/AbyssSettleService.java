package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Memory-session abyss progress + settle rewards (1.4.6).
 * Stage 4.1: settle(floor≥12) grants weekly first mat_ember_stable_charm via lootWeekMarks abyss_weekly12.
 * Session is in memory (Map&lt;UUID, AbyssSession&gt;); B2.140 keeps it across a relog and mirrors an unsettled floor
 * into PlayerData so it is paid out after the grace window or a restart.
 */
public final class AbyssSettleService {

    private static final String PREFIX = ChatColor.DARK_PURPLE + "[深渊] " + ChatColor.RESET;
    /** lootWeekMarks key — same format as LootService (key=weekId). Stage 4.1 weekly stable charm. */
    private static final String WEEKLY12_LOOT_KEY = "abyss_weekly12";
    private static final String STABLE_CHARM_ID = "mat_ember_stable_charm";
    private static final int WEEKLY12_MIN_FLOOR = 12;

    private final JavaPlugin plugin;
    private final NiBridge ni;
    private final PlayerDataStore dataStore;
    private final Map<UUID, AbyssSession> sessions = new HashMap<UUID, AbyssSession>();

    private boolean enabled = true;
    private final List<Tier> tiers = new ArrayList<Tier>();
    private String gemId = "gem_ember_sharp";
    private double gemChance = 1.0;
    private String shardId = "mat_ember_shard";
    private String boneId = "mat_ember_bone_dust";
    private String coreId = "mat_ember_core_fragment";
    private String t2Id = "gear_ember_t2_blade";
    private int grantT2At = 0;

    static final class Tier {
        final int min;
        final int max;
        final int shard;
        final int bone;
        final int core;
        final int gem;
        Tier(int min, int max, int shard, int bone, int core, int gem) {
            this.min = min; this.max = max;
            this.shard = shard; this.bone = bone; this.core = core; this.gem = gem;
        }
        boolean matches(int floor) {
            return floor >= min && floor <= max;
        }
    }

    public AbyssSettleService(JavaPlugin plugin, NiBridge ni, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.ni = ni;
        this.dataStore = dataStore;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "abyss.yml");
        if (!file.exists()) {
            plugin.saveResource("abyss.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("abyss.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        enabled = cfg.getBoolean("enabled", true);
        ConfigurationSection settle = cfg.getConfigurationSection("settle");
        tiers.clear();
        if (settle != null) {
            gemId = settle.getString("gem_id", "gem_ember_sharp");
            gemChance = settle.getDouble("gem_chance", 1.0);
            shardId = settle.getString("shard_id", "mat_ember_shard");
            boneId = settle.getString("bone_id", "mat_ember_bone_dust");
            coreId = settle.getString("core_id", "mat_ember_core_fragment");
            t2Id = settle.getString("t2_id", "gear_ember_t2_blade");
            grantT2At = settle.getInt("grant_t2_at", 0);
            List<?> raw = settle.getList("tiers");
            if (raw != null) {
                for (Object o : raw) {
                    if (!(o instanceof Map)) continue;
                    Map<?, ?> m = (Map<?, ?>) o;
                    tiers.add(new Tier(
                            toInt(m.get("min"), 1),
                            toInt(m.get("max"), 4),
                            toInt(m.get("shard"), 0),
                            toInt(m.get("bone"), 0),
                            toInt(m.get("core"), 0),
                            toInt(m.get("gem"), 0)));
                }
            }
        }
        if (tiers.isEmpty()) {
            tiers.add(new Tier(1, 4, 8, 2, 0, 0));
            tiers.add(new Tier(5, 9, 12, 4, 1, 1));
            tiers.add(new Tier(10, 999, 16, 6, 2, 1));
        }
        if (ni != null) {
            ni.warnMissingOnceIfAbsent(shardId);
            ni.warnMissingOnceIfAbsent(boneId);
            ni.warnMissingOnceIfAbsent(coreId);
            ni.warnMissingOnceIfAbsent(gemId);
            if (grantT2At > 0) ni.warnMissingOnceIfAbsent(t2Id);
        }
    }

    private static int toInt(Object o, int def) {
        if (o instanceof Number) return ((Number) o).intValue();
        if (o != null) {
            try { return Integer.parseInt(String.valueOf(o).trim()); }
            catch (NumberFormatException ignored) {}
        }
        return def;
    }

    public boolean isEnabled() { return enabled; }

    public AbyssSession getOrCreate(UUID uuid) {
        AbyssSession s = sessions.get(uuid);
        if (s == null) {
            s = new AbyssSession();
            sessions.put(uuid, s);
        }
        return s;
    }

    public AbyssSession getSession(UUID uuid) {
        return sessions.get(uuid);
    }

    public void clearSession(UUID uuid) {
        sessions.remove(uuid);
    }

    /** B2.140: an unsettled run survives a relog within this window (DP keeps the instance); later it is paid out. */
    static final long RELOG_GRACE_MS = 10L * 60L * 1000L;
    static final String PENDING_KEY = "abyss_run_floor";

    private void mirror(UUID id, int floor) {
        PlayerData d = dataStore.get(id);
        if (d == null) return;
        int cur = d.periodCount(PENDING_KEY, "all");
        if (cur != floor) d.addPeriodCount(PENDING_KEY, "all", floor - cur);
    }

    public void onPlayerQuit(Player player) {
        if (player == null) return;
        UUID id = player.getUniqueId();
        AbyssSession s = sessions.get(id);
        if (s == null || s.isSettled() || s.getFloor() < 1) { // nothing to keep
            sessions.remove(id);
            mirror(id, 0);
            return;
        }
        s.quitAt = System.currentTimeMillis();
        mirror(id, s.getFloor()); // saved by dataStore.unload right after
        plugin.getLogger().info("[深渊] " + player.getName() + " 断线，保留本局进度 层 " + s.getFloor() + "（" + (RELOG_GRACE_MS / 60000) + " 分钟内重连可继续）");
    }

    private static boolean inAbyssWorld(Player p) {
        String w = p.getWorld().getName().toLowerCase(java.util.Locale.ROOT);
        return w.startsWith("dungeon_") && w.contains("abyss");
    }

    /** B2.140: called ~2 s after join. Resume inside the instance within the grace window, otherwise pay out. */
    public void onPlayerJoin(final Player player) {
        if (player == null || !enabled) return;
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                if (!player.isOnline()) return;
                UUID id = player.getUniqueId();
                AbyssSession s = sessions.get(id);
                PlayerData d = dataStore.get(id);
                int pending = d == null ? 0 : d.periodCount(PENDING_KEY, "all");
                if (s == null && pending < 1) return;
                if (s == null) { // restart or memory lost: rebuild from the mirror
                    s = getOrCreate(id);
                    s.raiseFloor(pending);
                    s.quitAt = 1L; // treat as expired
                }
                if (s.isSettled() || s.getFloor() < 1) { mirror(id, 0); return; }
                boolean fresh = s.quitAt > 1L && System.currentTimeMillis() - s.quitAt <= RELOG_GRACE_MS;
                if (fresh && inAbyssWorld(player)) {
                    s.quitAt = 0;
                    player.sendMessage(PREFIX + ChatColor.LIGHT_PURPLE + "已恢复本局深渊进度：最高层 " + ChatColor.AQUA + s.getFloor()
                            + ChatColor.GRAY + "（可继续下潜，或菜单 → 深渊 → 撤离结算）");
                    plugin.getLogger().info("[深渊] " + player.getName() + " 重连，恢复本局进度 层 " + s.getFloor());
                    return;
                }
                player.sendMessage(PREFIX + ChatColor.YELLOW + "你在深渊中断线后未能回到本局，按断线前最高层 " + s.getFloor() + " 结算：");
                SettleResult r = settle(player);
                player.sendMessage(formatSettleMessage(player, r));
                plugin.getLogger().info("[深渊] " + player.getName() + " 重连补结算 层 " + r.floor + " → " + r.outcome);
                if (inAbyssWorld(player)) {
                    player.sendMessage(PREFIX + ChatColor.GRAY + "本局已结束，正在离开深渊……");
                    player.performCommand("dp leave");
                }
            }
        }, 40L);
    }

    /** Command root: /corerpg abyss [progress|settle|evacuate|…] */
    public boolean cmdRoot(CommandSender sender, String[] args) {
        if (!enabled) {
            sender.sendMessage(PREFIX + ChatColor.RED + "深渊结算未启用 (abyss.yml enabled=false)");
            return true;
        }
        String act = args.length >= 2 ? args[1].toLowerCase() : "";
        if (act.isEmpty() || "status".equals(act) || "info".equals(act)) {
            return cmdStatus(sender);
        }
        if ("progress".equals(act)) {
            return cmdProgress(sender, args);
        }
        if ("settle".equals(act)) {
            return cmdSettle(sender, args);
        }
        if ("evacuate".equals(act) || "leave".equals(act) || "上浮".equals(act)) {
            return cmdEvacuate(sender);
        }
        sender.sendMessage(PREFIX + ChatColor.YELLOW
                + "/corerpg abyss | progress <玩家> <层> | settle [玩家] | evacuate");
        return true;
    }

    private boolean cmdStatus(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "控制台请用 progress / settle <玩家>");
            return true;
        }
        Player p = (Player) sender;
        AbyssSession session = getOrCreate(p.getUniqueId());
        PlayerData data = dataStore.get(p.getUniqueId());
        int tickets = ni == null ? 0 : ni.countInInventory(p, "ticket_ember_abyss");
        p.sendMessage(PREFIX + ChatColor.LIGHT_PURPLE + "本局状态");
        p.sendMessage(ChatColor.GRAY + "  本局最高层: " + ChatColor.AQUA + session.getFloor()
                + ChatColor.GRAY + " · 已结算: "
                + (session.isSettled() ? ChatColor.GREEN + "是" : ChatColor.YELLOW + "否"));
        p.sendMessage(ChatColor.GRAY + "  历史最高 abyssBest: " + ChatColor.LIGHT_PURPLE + data.getAbyssBest()
                + ChatColor.GRAY + " · 本周: " + ChatColor.LIGHT_PURPLE + data.effectiveAbyssWeek());
        p.sendMessage(ChatColor.GRAY + "  背包深渊票: " + ChatColor.WHITE + tickets);
        p.sendMessage(ChatColor.DARK_GRAY + "  提示: /corerpg abyss evacuate 上浮结算 · 或 settle");
        return true;
    }

    private boolean cmdProgress(CommandSender sender, String[] args) {
        if (!isAdmin(sender)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "需要 corerpg.admin 或控制台");
            return true;
        }
        if (args.length < 4) {
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "用法: /corerpg abyss progress <玩家> <层>");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "玩家不在线: " + args[2]);
            return true;
        }
        int floor;
        try {
            floor = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(PREFIX + ChatColor.RED + "层数无效: " + args[3]);
            return true;
        }
        if (floor < 0) floor = 0;
        AbyssSession session = getOrCreate(target.getUniqueId());
        int before = session.getFloor();
        if (floor == 0) { // B2.141: DP start script → new run (was a no-op: a 2nd run in one login settled as ALREADY)
            if (before > 0 && !session.isSettled())
                plugin.getLogger().warning("[深渊] " + target.getName() + " 新开一局时上一局（层 " + before + "）尚未结算");
            session.reset();
            mirror(target.getUniqueId(), 0);
        }
        session.raiseFloor(floor);
        sender.sendMessage(PREFIX + ChatColor.GREEN + target.getName()
                + " 本局层 " + before + " → " + session.getFloor());
        // optional notify target
        if (target != sender) {
            target.sendMessage(PREFIX + ChatColor.GRAY + "本局最高层更新为 "
                    + ChatColor.AQUA + session.getFloor());
        }
        return true;
    }

    private boolean cmdSettle(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 3) {
            if (!isAdmin(sender)) {
                sender.sendMessage(PREFIX + ChatColor.RED + "需要 corerpg.admin 或控制台才能指定玩家");
                return true;
            }
            target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage(PREFIX + ChatColor.RED + "玩家不在线: " + args[2]);
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(PREFIX + ChatColor.YELLOW + "用法: /corerpg abyss settle <玩家>");
                return true;
            }
            target = (Player) sender;
        }
        SettleResult r = settle(target);
        String msg = formatSettleMessage(target, r);
        sender.sendMessage(msg);
        if (target != sender) target.sendMessage(msg);
        return true;
    }

    private boolean cmdEvacuate(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "仅玩家可用 evacuate");
            return true;
        }
        Player p = (Player) sender;
        SettleResult r = settle(p);
        p.sendMessage(formatSettleMessage(p, r));
        p.sendMessage(PREFIX + ChatColor.AQUA + "上浮撤离 · 前往 hub");
        try {
            boolean ok = p.performCommand("dp leave");
            if (!ok) {
                Bukkit.dispatchCommand(p, "dp leave");
            }
        } catch (Throwable t) {
            plugin.getLogger().warning("[深渊] dp leave failed for " + p.getName() + ": " + t.getMessage());
            p.sendMessage(PREFIX + ChatColor.GRAY + "自动离本失败，请手动 /dp leave");
        }
        return true;
    }

    public enum SettleOutcome {
        ALREADY, NO_CHEST, GRANTED
    }

    public static final class SettleResult {
        public final SettleOutcome outcome;
        public final int floor;
        public final Tier tier; // may be null
        public final boolean grantedT2;

        SettleResult(SettleOutcome outcome, int floor, Tier tier, boolean grantedT2) {
            this.outcome = outcome;
            this.floor = floor;
            this.tier = tier;
            this.grantedT2 = grantedT2;
        }
    }

    /**
     * Idempotent settle for online player. Grants NI rewards by tier, records ladder, marks settled.
     */
    public SettleResult settle(Player player) {
        if (player == null) {
            return new SettleResult(SettleOutcome.ALREADY, 0, null, false);
        }
        AbyssSession session = getOrCreate(player.getUniqueId());
        if (session.isSettled()) {
            return new SettleResult(SettleOutcome.ALREADY, session.getFloor(), null, false);
        }
        mirror(player.getUniqueId(), 0); // B2.140: settled now (flushMutation below / on quit)
        int floor = session.getFloor();
        if (floor < 1) {
            session.setSettled(true);
            return new SettleResult(SettleOutcome.NO_CHEST, floor, null, false);
        }
        Tier tier = findTier(floor);
        if (tier == null) {
            // no matching tier — still mark settled + record
            PlayerData data = dataStore.get(player.getUniqueId());
            data.recordAbyssFloor(floor);
            tryGrantWeekly12Charm(player, data, floor);
            dataStore.flushMutation(player.getUniqueId());
            session.setSettled(true);
            refreshQuestAbyss(player);
            return new SettleResult(SettleOutcome.NO_CHEST, floor, null, false);
        }
        grantRewards(player, tier);
        boolean gaveT2 = false;
        if (grantT2At > 0 && floor >= grantT2At && t2Id != null && !t2Id.isEmpty()) {
            if (ni != null) ni.giveNiItem(player, t2Id, 1);
            gaveT2 = true;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        data.recordAbyssFloor(floor);
        // Stage 4.1: weekly first reach floor ≥12 → stable charm (idempotent via lootWeekMarks)
        tryGrantWeekly12Charm(player, data, floor);
        dataStore.flushMutation(player.getUniqueId());
        session.setSettled(true);
        refreshQuestAbyss(player);
        return new SettleResult(SettleOutcome.GRANTED, floor, tier, gaveT2);
    }

    /**
     * First reach of floor ≥12 this ISO week (Asia/Shanghai): give mat_ember_stable_charm ×1.
     * Uses lootWeekMarks key abyss_weekly12=&lt;weekId&gt; — same as LootService; second call same week is silent.
     * Does NOT go through LootService.cmd (avoids "本周首通保底已领" noise).
     */
    private void tryGrantWeekly12Charm(Player player, PlayerData data, int floor) {
        if (player == null || data == null || floor < WEEKLY12_MIN_FLOOR) return;
        String week = DailyService.weekId();
        String mark = WEEKLY12_LOOT_KEY + "=" + week;
        if (data.getLootWeekMarks().contains(mark)) {
            return; // already claimed this week — silent
        }
        data.addLootWeekMark(WEEKLY12_LOOT_KEY, week);
        boolean given = ni != null && ni.giveNiItem(player, STABLE_CHARM_ID, 1);
        if (given) {
            player.sendMessage(ChatColor.GREEN + "[深渊] 本周首次抵达第 12 层，获得稳定符 ×1");
        } else {
            plugin.getLogger().warning("[深渊] weekly12 charm grant failed for " + player.getName()
                    + " (NI missing or give failed); mark still recorded for " + week);
        }
    }

    /** Stage 4.5: after historical best updates, re-check abyss_floor state steps. */
    private void refreshQuestAbyss(Player player) {
        if (player == null) return;
        if (!(plugin instanceof CoreRpgPlugin)) return;
        QuestService qs = ((CoreRpgPlugin) plugin).getQuestService();
        if (qs == null) return;
        try {
            qs.checkPassive(player);
            qs.onEvent(player, "abyss_floor");
        } catch (Throwable t) {
            plugin.getLogger().warning("[深渊] quest abyss_floor refresh: " + t.getMessage());
        }
    }

    private Tier findTier(int floor) {
        for (Tier t : tiers) {
            if (t.matches(floor)) return t;
        }
        return null;
    }

    private void grantRewards(Player player, Tier tier) {
        if (ni == null) return;
        if (tier.shard > 0) ni.giveNiItem(player, shardId, tier.shard);
        if (tier.bone > 0) ni.giveNiItem(player, boneId, tier.bone);
        if (tier.core > 0) ni.giveNiItem(player, coreId, tier.core);
        if (tier.gem > 0 && Math.random() < gemChance) ni.giveNiItem(player, gemId, tier.gem);
    }

    private String formatSettleMessage(Player target, SettleResult r) {
        if (r.outcome == SettleOutcome.ALREADY) {
            return PREFIX + ChatColor.YELLOW + target.getName() + " 本局已结算，不再重复发放";
        }
        if (r.outcome == SettleOutcome.NO_CHEST) {
            return PREFIX + ChatColor.GRAY + target.getName() + " 最高层 "
                    + r.floor + " <1，无结算箱（已标记结算）";
        }
        Tier t = r.tier;
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX).append(ChatColor.GREEN).append(target.getName())
                .append(" 结算完成 · 层 ").append(ChatColor.AQUA).append(r.floor)
                .append(ChatColor.GREEN).append(" → ");
        sb.append(ChatColor.WHITE).append("碎片×").append(t.shard)
                .append(ChatColor.GRAY).append(" 骨尘×").append(t.bone);
        if (t.core > 0) sb.append(ChatColor.GRAY).append(" 核心×").append(t.core);
        if (t.gem > 0) sb.append(ChatColor.GRAY).append(" 孔石×").append(t.gem).append(gemChance < 1.0 ? "（" + Math.round(gemChance * 100) + "%）" : "");
        if (r.grantedT2) sb.append(ChatColor.GOLD).append(" +T2刃");
        return sb.toString();
    }

    private static boolean isAdmin(CommandSender sender) {
        return !(sender instanceof Player) || sender.hasPermission("corerpg.admin");
    }
}
