package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Arena — 1v1 teleport + settle; 2v2 team-wipe on pads (or stub if world missing).
 * docs/design/DESIGN-ember-arena-auction.md · CoreRpg 1.3.11
 */
public final class ArenaService implements Listener {

    public static final String PREFIX = ChatColor.RED + "[竞技] " + ChatColor.RESET;

    private static final class RankDef {
        final String id;
        final String name;
        final int minPoints;
        RankDef(String id, String name, int minPoints) {
            this.id = id; this.name = name; this.minPoints = minPoints;
        }
    }

    private static final class PadDef {
        final double x, y, z;
        final float yaw;
        PadDef(double x, double y, double z, float yaw) {
            this.x = x; this.y = y; this.z = z; this.yaw = yaw;
        }
        Location toLocation(World w) {
            return new Location(w, x, y, z, yaw, 0f);
        }
    }

    /** In-memory active match. */
    private static final class ActiveMatch {
        final String mode;
        final List<UUID> participants;
        /** team index per participant: 0 = A, 1 = B */
        final Map<UUID, Integer> teamOf = new HashMap<UUID, Integer>();
        final Map<UUID, Location> returnLocations = new HashMap<UUID, Location>();
        final Set<UUID> dead = new HashSet<UUID>();
        final long startMillis;
        volatile boolean settled;
        BukkitTask timeoutTask;

        ActiveMatch(String mode, List<UUID> participants) {
            this.mode = mode;
            this.participants = new ArrayList<UUID>(participants);
            this.startMillis = System.currentTimeMillis();
            this.settled = false;
        }
    }

    private final JavaPlugin plugin;
    private final PlayerDataStore dataStore;
    private final NiBridge niBridge;
    private final Random random = new Random();

    private boolean enabled = true;
    private final List<RankDef> ranks = new ArrayList<RankDef>();
    private int dailyCoin = 80;
    private String dailyCosmeticId = "cosmetic_calamity_shard";
    private int dailyCosmeticAmount = 1;
    private int winPoints = 15;
    private int losePoints = 5;
    private int participateCoin = 10;
    private int winCoin = 25;
    private String matchWorldName = "world";
    private PadDef padA = new PadDef(100.5, 65, 100.5, 0f);
    private PadDef padB = new PadDef(110.5, 65, 100.5, 180f);
    private int timeoutSeconds = 120;
    private int returnDelayTicks = 40;

    /** mode -> ordered queue of player UUIDs */
    private final Map<String, List<UUID>> queues = new LinkedHashMap<String, List<UUID>>();
    /** player UUID -> active match */
    private final Map<UUID, ActiveMatch> activeByPlayer = new HashMap<UUID, ActiveMatch>();
    private final List<ActiveMatch> activeMatches = new ArrayList<ActiveMatch>();

    public ArenaService(JavaPlugin plugin, PlayerDataStore dataStore, NiBridge niBridge) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.niBridge = niBridge;
        queues.put("1v1", new ArrayList<UUID>());
        queues.put("2v2", new ArrayList<UUID>());
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "arena.yml");
        if (!file.exists()) {
            plugin.saveResource("arena.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("arena.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        enabled = cfg.getBoolean("enabled", true);
        ranks.clear();
        List<?> rawRanks = cfg.getList("ranks");
        if (rawRanks != null) {
            for (Object o : rawRanks) {
                if (!(o instanceof Map)) continue;
                Map<?, ?> m = (Map<?, ?>) o;
                String id = String.valueOf(m.get("id"));
                String name = m.containsKey("name") ? String.valueOf(m.get("name")) : id;
                int min = 0;
                Object mp = m.get("min_points");
                if (mp instanceof Number) min = ((Number) mp).intValue();
                else if (mp != null) {
                    try { min = Integer.parseInt(String.valueOf(mp)); } catch (NumberFormatException ignored) {}
                }
                ranks.add(new RankDef(id, name, Math.max(0, min)));
            }
        }
        if (ranks.isEmpty()) {
            ranks.add(new RankDef("bronze", "青铜", 0));
            ranks.add(new RankDef("silver", "白银", 100));
            ranks.add(new RankDef("gold", "黄金", 250));
        }
        ConfigurationSection daily = cfg.getConfigurationSection("daily");
        if (daily != null) {
            dailyCoin = Math.max(0, daily.getInt("coin", 80));
            dailyCosmeticId = daily.getString("cosmetic_ni_id", "cosmetic_calamity_shard");
            if (dailyCosmeticId == null || dailyCosmeticId.isEmpty()) {
                dailyCosmeticId = "cosmetic_calamity_shard";
            }
            dailyCosmeticAmount = Math.max(1, daily.getInt("cosmetic_amount", 1));
        }
        ConfigurationSection match = cfg.getConfigurationSection("match");
        if (match != null) {
            winPoints = Math.max(0, match.getInt("win_points", 15));
            losePoints = Math.max(0, match.getInt("lose_points", 5));
            participateCoin = Math.max(0, match.getInt("participate_coin", 10));
            winCoin = Math.max(0, match.getInt("win_coin", 25));
            String wn = match.getString("world", "world");
            matchWorldName = (wn == null || wn.isEmpty()) ? "world" : wn;
            padA = readPad(match.getConfigurationSection("pad_a"), 100.5, 65, 100.5, 0f);
            padB = readPad(match.getConfigurationSection("pad_b"), 110.5, 65, 100.5, 180f);
            timeoutSeconds = Math.max(10, match.getInt("timeout_seconds", 120));
            returnDelayTicks = Math.max(0, match.getInt("return_delay_ticks", 40));
        }
    }

    private static PadDef readPad(ConfigurationSection sec, double dx, double dy, double dz, float dyaw) {
        if (sec == null) return new PadDef(dx, dy, dz, dyaw);
        double x = sec.getDouble("x", dx);
        double y = sec.getDouble("y", dy);
        double z = sec.getDouble("z", dz);
        float yaw = (float) sec.getDouble("yaw", dyaw);
        return new PadDef(x, y, z, yaw);
    }

    public boolean isEnabled() { return enabled; }

    public void onPlayerQuit(Player player) {
        if (player == null) return;
        leaveQueueSilent(player);
        ActiveMatch m = activeByPlayer.get(player.getUniqueId());
        if (m != null && !m.settled) {
            settleQuit(m, player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        if (victim == null) return;
        ActiveMatch m = activeByPlayer.get(victim.getUniqueId());
        if (m == null || m.settled) return;
        handleDeath(m, victim);
    }

    public void cmdRoot(CommandSender sender, String[] args) {
        if (!enabled) {
            sender.sendMessage(PREFIX + ChatColor.RED + "竞技系统未启用。");
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only");
            return;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("corerpg.arena") && !p.hasPermission("corerpg.use")) {
            p.sendMessage(PREFIX + ChatColor.RED + "需要 corerpg.arena");
            return;
        }
        if (args.length < 2) {
            cmdStatus(p);
            return;
        }
        String sub = args[1].toLowerCase(Locale.ROOT);
        if ("queue".equals(sub)) {
            if (args.length < 3) {
                p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg arena queue 1v1|2v2");
                return;
            }
            cmdQueue(p, args[2]);
            return;
        }
        if ("leave".equals(sub) || "cancel".equals(sub)) {
            cmdLeave(p);
            return;
        }
        if ("stats".equals(sub) || "stat".equals(sub)) {
            cmdStats(p);
            return;
        }
        if ("claim".equals(sub)) {
            cmdClaim(p);
            return;
        }
        if ("forfeit".equals(sub) || "ff".equals(sub) || "投降".equals(sub)) {
            cmdForfeit(p);
            return;
        }
        if ("help".equals(sub)) {
            sendHelp(p);
            return;
        }
        cmdStatus(p);
    }

    private void sendHelp(Player p) {
        p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg arena|pvp [queue|leave|stats|claim|forfeit]");
        p.sendMessage(ChatColor.GRAY + "  queue 1v1|2v2 · leave · stats · claim（日奖励箱）· forfeit（对战中）");
    }

    private RankDef resolveRank(int points) {
        RankDef best = ranks.get(0);
        for (RankDef r : ranks) {
            if (points >= r.minPoints) best = r;
        }
        return best;
    }

    private void cmdStatus(Player p) {
        PlayerData d = dataStore.get(p.getUniqueId());
        RankDef rank = resolveRank(d.getArenaPoints());
        String today = DailyService.today();
        boolean claimed = today.equals(d.getArenaDailyClaimDate());
        String mode = d.getArenaQueueMode();
        ActiveMatch am = activeByPlayer.get(p.getUniqueId());
        String queueLine;
        if (am != null && !am.settled) {
            queueLine = ChatColor.LIGHT_PURPLE + "对战中 · " + am.mode;
        } else if (mode == null || mode.isEmpty()) {
            queueLine = ChatColor.GRAY + "未排队";
        } else {
            queueLine = ChatColor.AQUA + "排队中 · " + mode + "（队列 " + queueSize(mode) + "）";
        }
        p.sendMessage(PREFIX + ChatColor.YELLOW + "积分 §f" + d.getArenaPoints()
                + ChatColor.YELLOW + " · 段位 §e" + rank.name
                + ChatColor.DARK_GRAY + " (" + rank.id + ")");
        p.sendMessage(ChatColor.GRAY + "  日奖励箱："
                + (claimed ? ChatColor.RED + "今日已领" : ChatColor.GREEN + "可领取 /corerpg arena claim"));
        p.sendMessage(ChatColor.GRAY + "  " + queueLine);
        p.sendMessage(ChatColor.DARK_GRAY + "  /corerpg arena|pvp queue 1v1|2v2 · leave · stats · claim · forfeit");
    }

    private void cmdStats(Player p) {
        PlayerData d = dataStore.get(p.getUniqueId());
        RankDef rank = resolveRank(d.getArenaPoints());
        int w = d.getArenaWins();
        int l = d.getArenaLosses();
        int total = w + l;
        String wr = total <= 0 ? "—" : String.format(Locale.ROOT, "%.0f%%", (100.0 * w) / total);
        p.sendMessage(PREFIX + ChatColor.YELLOW + "战绩");
        p.sendMessage(ChatColor.GRAY + "  胜 §a" + w + ChatColor.GRAY + " · 负 §c" + l
                + ChatColor.GRAY + " · 胜率 §f" + wr);
        p.sendMessage(ChatColor.GRAY + "  积分 §f" + d.getArenaPoints()
                + ChatColor.GRAY + " · 段位 §e" + rank.name);
    }

    private void cmdClaim(Player p) {
        PlayerData d = dataStore.get(p.getUniqueId());
        String today = DailyService.today();
        if (today.equals(d.getArenaDailyClaimDate())) {
            p.sendMessage(PREFIX + ChatColor.RED + "今日日奖励箱已领取，明日再来。");
            return;
        }
        d.setArenaDailyClaimDate(today);
        if (dailyCoin > 0) d.addCoin(dailyCoin);
        dataStore.flushMutation(p.getUniqueId());
        boolean given = false;
        if (dailyCosmeticAmount > 0 && dailyCosmeticId != null && !dailyCosmeticId.isEmpty()) {
            given = niBridge.giveNiItem(p, dailyCosmeticId, dailyCosmeticAmount);
        }
        p.sendMessage(PREFIX + ChatColor.GREEN + "日奖励箱已领取："
                + ChatColor.YELLOW + "余烬币 ×" + dailyCoin
                + (given ? ChatColor.LIGHT_PURPLE + " · 外观碎片 ×" + dailyCosmeticAmount : ChatColor.GRAY + " · 外观发放失败"));
    }

    private void cmdQueue(Player p, String rawMode) {
        if (activeByPlayer.containsKey(p.getUniqueId())) {
            p.sendMessage(PREFIX + ChatColor.RED + "你已在对战中，可用 /corerpg arena forfeit 认输。");
            return;
        }
        String mode = normalizeMode(rawMode);
        if (mode == null) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "/corerpg arena queue 1v1|2v2");
            return;
        }
        PlayerData d = dataStore.get(p.getUniqueId());
        String cur = d.getArenaQueueMode();
        if (mode.equals(cur)) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "你已在 " + mode + " 队列中。");
            return;
        }
        if (cur != null && !cur.isEmpty()) {
            leaveQueueSilent(p);
        }
        List<UUID> q = queues.get(mode);
        if (q == null) {
            q = new ArrayList<UUID>();
            queues.put(mode, q);
        }
        if (!q.contains(p.getUniqueId())) q.add(p.getUniqueId());
        d.setArenaQueueMode(mode);
        dataStore.flushMutation(p.getUniqueId());
        p.sendMessage(PREFIX + ChatColor.GREEN + "已加入 §e" + mode + ChatColor.GREEN
                + " 队列（当前 " + q.size() + " 人）。");
        tryMatch(mode);
    }

    private void cmdLeave(Player p) {
        if (activeByPlayer.containsKey(p.getUniqueId())) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "对战中请用 /corerpg arena forfeit 认输。");
            return;
        }
        PlayerData d = dataStore.get(p.getUniqueId());
        String cur = d.getArenaQueueMode();
        if (cur == null || cur.isEmpty()) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "你不在任何队列中。");
            return;
        }
        leaveQueueSilent(p);
        p.sendMessage(PREFIX + ChatColor.GREEN + "已离开 " + cur + " 队列。");
    }

    private void cmdForfeit(Player p) {
        ActiveMatch m = activeByPlayer.get(p.getUniqueId());
        if (m == null || m.settled) {
            p.sendMessage(PREFIX + ChatColor.YELLOW + "你不在对战中。");
            return;
        }
        p.sendMessage(PREFIX + ChatColor.GRAY + "你已认输。");
        settleQuit(m, p.getUniqueId());
    }

    private void leaveQueueSilent(Player p) {
        UUID id = p.getUniqueId();
        for (List<UUID> q : queues.values()) {
            q.remove(id);
        }
        PlayerData d = dataStore.get(id);
        if (d.getArenaQueueMode() != null && !d.getArenaQueueMode().isEmpty()) {
            d.setArenaQueueMode("");
            dataStore.flushMutation(id);
        }
    }

    private int queueSize(String mode) {
        List<UUID> q = queues.get(mode);
        return q == null ? 0 : q.size();
    }

    private static String normalizeMode(String raw) {
        if (raw == null) return null;
        String s = raw.trim().toLowerCase(Locale.ROOT);
        if ("1v1".equals(s) || "1".equals(s) || "solo".equals(s)) return "1v1";
        if ("2v2".equals(s) || "2".equals(s) || "duo".equals(s)) return "2v2";
        return null;
    }

    private void tryMatch(String mode) {
        List<UUID> q = queues.get(mode);
        if (q == null) return;
        int need = "2v2".equals(mode) ? 4 : 2;
        while (q.size() >= need) {
            List<UUID> matched = new ArrayList<UUID>();
            for (int i = 0; i < need; i++) matched.add(q.remove(0));
            startMatch(mode, matched);
        }
    }

    private void startMatch(String mode, List<UUID> matched) {
        // clear queue flags
        for (UUID id : matched) {
            PlayerData d = dataStore.get(id);
            d.setArenaQueueMode("");
            dataStore.flushMutation(id);
        }

        World world = Bukkit.getWorld(matchWorldName);
        if (world == null) {
            plugin.getLogger().warning("[Arena] match world '" + matchWorldName
                    + "' missing — falling back to stub settle.");
            resolveStubMatch(mode, matched);
            return;
        }

        List<Player> online = new ArrayList<Player>();
        for (UUID id : matched) {
            Player pl = Bukkit.getPlayer(id);
            if (pl != null && pl.isOnline()) online.add(pl);
        }
        if (online.size() < ("2v2".equals(mode) ? 4 : 2)) {
            // not enough online — stub award those present or abort quietly
            if (!online.isEmpty()) {
                resolveStubMatch(mode, matched);
            }
            return;
        }

        ActiveMatch match = new ActiveMatch(mode, matched);
        // assign teams: first half A, second half B
        int half = matched.size() / 2;
        for (int i = 0; i < matched.size(); i++) {
            match.teamOf.put(matched.get(i), i < half ? 0 : 1);
        }

        // save return locs + teleport
        int aIdx = 0, bIdx = 0;
        for (Player pl : online) {
            match.returnLocations.put(pl.getUniqueId(), pl.getLocation().clone());
            Integer team = match.teamOf.get(pl.getUniqueId());
            Location dest;
            if (team != null && team.intValue() == 0) {
                dest = offsetPad(world, padA, aIdx++, "1v1".equals(mode) ? 0 : 2);
            } else {
                dest = offsetPad(world, padB, bIdx++, "1v1".equals(mode) ? 0 : 2);
            }
            pl.teleport(dest);
            pl.setFireTicks(0);
            try {
                pl.setHealth(pl.getMaxHealth());
            } catch (Throwable ignored) {}
            pl.setFoodLevel(20);
            pl.sendMessage(PREFIX + ChatColor.YELLOW + "对战开始 · 击杀对方获胜 · 超时平局");
        }

        activeMatches.add(match);
        for (UUID id : matched) {
            activeByPlayer.put(id, match);
        }

        final ActiveMatch timeoutMatch = match;
        long ticks = Math.max(20L, timeoutSeconds * 20L);
        match.timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                if (!timeoutMatch.settled) {
                    settleDraw(timeoutMatch);
                }
            }
        }, ticks);
    }

    private Location offsetPad(World world, PadDef pad, int index, int spread) {
        Location base = pad.toLocation(world);
        if (spread <= 0 || index == 0) return base;
        // simple ±2 offsets along X for teammates
        double ox = (index % 2 == 0) ? -spread : spread;
        return new Location(world, pad.x + ox, pad.y, pad.z, pad.yaw, 0f);
    }

    private void handleDeath(ActiveMatch m, Player victim) {
        if (m.settled) return;
        m.dead.add(victim.getUniqueId());
        Integer victimTeam = m.teamOf.get(victim.getUniqueId());
        if (victimTeam == null) return;

        if ("1v1".equals(m.mode) || m.participants.size() == 2) {
            // other participant wins
            UUID winnerId = null;
            for (UUID id : m.participants) {
                if (!id.equals(victim.getUniqueId())) {
                    winnerId = id;
                    break;
                }
            }
            // prefer getKiller if Player and in match
            Player killer = victim.getKiller();
            if (killer != null && m.teamOf.containsKey(killer.getUniqueId())
                    && !killer.getUniqueId().equals(victim.getUniqueId())) {
                winnerId = killer.getUniqueId();
            }
            if (winnerId == null) {
                settleDraw(m);
                return;
            }
            settleWinLoss(m, Collections.singleton(winnerId),
                    Collections.singleton(victim.getUniqueId()), "击杀");
            return;
        }

        // 2v2: team wipe when both on a team dead
        boolean teamWiped = true;
        for (UUID id : m.participants) {
            Integer t = m.teamOf.get(id);
            if (t != null && t.intValue() == victimTeam.intValue() && !m.dead.contains(id)) {
                teamWiped = false;
                break;
            }
        }
        if (!teamWiped) {
            for (UUID id : m.participants) {
                Player pl = Bukkit.getPlayer(id);
                if (pl != null && pl.isOnline()) {
                    pl.sendMessage(PREFIX + ChatColor.GRAY + victim.getName() + " 阵亡（需团灭才结算）");
                }
            }
            return;
        }
        Set<UUID> winners = new HashSet<UUID>();
        Set<UUID> losers = new HashSet<UUID>();
        for (UUID id : m.participants) {
            Integer t = m.teamOf.get(id);
            if (t != null && t.intValue() == victimTeam.intValue()) losers.add(id);
            else winners.add(id);
        }
        settleWinLoss(m, winners, losers, "团灭");
    }

    private void settleQuit(ActiveMatch m, UUID quitterId) {
        if (m.settled) return;
        Integer quitTeam = m.teamOf.get(quitterId);
        if (quitTeam == null) {
            settleDraw(m);
            return;
        }
        if ("1v1".equals(m.mode) || m.participants.size() == 2) {
            Set<UUID> winners = new HashSet<UUID>();
            Set<UUID> losers = new HashSet<UUID>();
            losers.add(quitterId);
            for (UUID id : m.participants) {
                if (!id.equals(quitterId)) winners.add(id);
            }
            settleWinLoss(m, winners, losers, "对手离开");
            return;
        }
        // 2v2: quitting counts as that player dead; if team wiped → settle
        m.dead.add(quitterId);
        boolean teamWiped = true;
        for (UUID id : m.participants) {
            Integer t = m.teamOf.get(id);
            if (t != null && t.intValue() == quitTeam.intValue() && !m.dead.contains(id)) {
                teamWiped = false;
                break;
            }
        }
        if (!teamWiped) {
            for (UUID id : m.participants) {
                Player pl = Bukkit.getPlayer(id);
                if (pl != null && pl.isOnline()) {
                    pl.sendMessage(PREFIX + ChatColor.GRAY + "有选手离开，队友仍在场。");
                }
            }
            return;
        }
        Set<UUID> winners = new HashSet<UUID>();
        Set<UUID> losers = new HashSet<UUID>();
        for (UUID id : m.participants) {
            Integer t = m.teamOf.get(id);
            if (t != null && t.intValue() == quitTeam.intValue()) losers.add(id);
            else winners.add(id);
        }
        settleWinLoss(m, winners, losers, "对手离开");
    }

    private void settleDraw(ActiveMatch m) {
        if (m.settled) return;
        m.settled = true;
        cancelTimeout(m);
        for (UUID id : m.participants) {
            PlayerData d = dataStore.get(id);
            d.setArenaPoints(d.getArenaPoints() + losePoints);
            if (participateCoin > 0) d.addCoin(participateCoin);
            dataStore.flushMutation(id);
            Player pl = Bukkit.getPlayer(id);
            if (pl != null && pl.isOnline()) {
                RankDef rank = resolveRank(d.getArenaPoints());
                pl.sendMessage(PREFIX + ChatColor.YELLOW + "平局（超时）"
                        + ChatColor.GRAY + " · 参与 +" + losePoints + " 积分 · 余烬币 ×" + participateCoin
                        + ChatColor.DARK_GRAY + "（积分 " + d.getArenaPoints() + " · " + rank.name + "）");
            }
        }
        finishMatch(m);
    }

    private void settleWinLoss(ActiveMatch m, Set<UUID> winners, Set<UUID> losers, String reason) {
        if (m.settled) return;
        m.settled = true;
        cancelTimeout(m);

        String winnerNames = namesOf(winners);
        for (UUID id : winners) {
            PlayerData d = dataStore.get(id);
            d.setArenaWins(d.getArenaWins() + 1);
            d.setArenaPoints(d.getArenaPoints() + winPoints);
            if (winCoin > 0) d.addCoin(winCoin);
            dataStore.flushMutation(id);
            Player pl = Bukkit.getPlayer(id);
            if (pl != null && pl.isOnline()) {
                RankDef rank = resolveRank(d.getArenaPoints());
                pl.sendMessage(PREFIX + ChatColor.GREEN + "本场胜出！"
                        + ChatColor.GRAY + "（" + reason + "）"
                        + ChatColor.YELLOW + " +" + winPoints + " 积分 · 余烬币 ×" + winCoin
                        + ChatColor.GRAY + "（积分 " + d.getArenaPoints() + " · " + rank.name + "）");
            }
        }
        for (UUID id : losers) {
            PlayerData d = dataStore.get(id);
            d.setArenaLosses(d.getArenaLosses() + 1);
            d.setArenaPoints(d.getArenaPoints() + losePoints);
            if (participateCoin > 0) d.addCoin(participateCoin);
            dataStore.flushMutation(id);
            Player pl = Bukkit.getPlayer(id);
            if (pl != null && pl.isOnline()) {
                pl.sendMessage(PREFIX + ChatColor.GRAY + "本场落败"
                        + ChatColor.YELLOW + " +" + losePoints + " 积分 · 余烬币 ×" + participateCoin
                        + ChatColor.GRAY + "（胜方 " + winnerNames
                        + " · 积分 " + d.getArenaPoints() + "）");
            }
        }
        finishMatch(m);
    }

    private String namesOf(Set<UUID> ids) {
        StringBuilder sb = new StringBuilder();
        for (UUID id : ids) {
            Player pl = Bukkit.getPlayer(id);
            String name = pl != null ? pl.getName() : Bukkit.getOfflinePlayer(id).getName();
            if (name == null) name = id.toString().substring(0, 8);
            if (sb.length() > 0) sb.append("/");
            sb.append(name);
        }
        return sb.length() == 0 ? "?" : sb.toString();
    }

    private void cancelTimeout(ActiveMatch m) {
        if (m.timeoutTask != null) {
            try { m.timeoutTask.cancel(); } catch (Throwable ignored) {}
            m.timeoutTask = null;
        }
    }

    private void finishMatch(final ActiveMatch m) {
        final Map<UUID, Location> returns = new HashMap<UUID, Location>(m.returnLocations);
        final List<UUID> parts = new ArrayList<UUID>(m.participants);
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                for (UUID id : parts) {
                    Player pl = Bukkit.getPlayer(id);
                    if (pl == null || !pl.isOnline()) continue;
                    try {
                        if (pl.isDead()) {
                            pl.spigot().respawn();
                        }
                    } catch (Throwable ignored) {}
                    Location home = returns.get(id);
                    if (home != null && home.getWorld() != null) {
                        pl.teleport(home);
                    }
                    pl.setFireTicks(0);
                    try {
                        if (!pl.isDead()) {
                            pl.setHealth(Math.min(pl.getMaxHealth(), pl.getMaxHealth()));
                        }
                    } catch (Throwable ignored) {}
                    try { pl.setFoodLevel(20); } catch (Throwable ignored) {}
                }
            }
        }, Math.max(1L, returnDelayTicks));

        for (UUID id : parts) {
            activeByPlayer.remove(id);
        }
        activeMatches.remove(m);
    }

    /**
     * Fallback stub when arena world is missing: random winner + points/coins.
     */
    private void resolveStubMatch(String mode, List<UUID> matched) {
        List<Player> online = new ArrayList<Player>();
        for (UUID id : matched) {
            Player pl = Bukkit.getPlayer(id);
            if (pl != null && pl.isOnline()) online.add(pl);
            PlayerData d = dataStore.get(id);
            d.setArenaQueueMode("");
            dataStore.flushMutation(id);
        }
        if (online.isEmpty()) return;

        Player winner = online.get(random.nextInt(online.size()));
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < online.size(); i++) {
            if (i > 0) names.append(ChatColor.GRAY).append(" / ");
            names.append(ChatColor.WHITE).append(online.get(i).getName());
        }

        for (Player pl : online) {
            PlayerData d = dataStore.get(pl.getUniqueId());
            boolean win = pl.getUniqueId().equals(winner.getUniqueId());
            if (win) {
                d.setArenaWins(d.getArenaWins() + 1);
                d.setArenaPoints(d.getArenaPoints() + winPoints);
                if (winCoin > 0) d.addCoin(winCoin);
            } else {
                d.setArenaLosses(d.getArenaLosses() + 1);
                d.setArenaPoints(d.getArenaPoints() + losePoints);
                if (participateCoin > 0) d.addCoin(participateCoin);
            }
            dataStore.flushMutation(pl.getUniqueId());
            RankDef rank = resolveRank(d.getArenaPoints());
            pl.sendMessage(PREFIX + ChatColor.YELLOW + mode + " 匹配成功（世界缺失·即时结算）§7 · 选手 "
                    + names);
            if (win) {
                pl.sendMessage(PREFIX + ChatColor.GREEN + "本场胜出！"
                        + ChatColor.YELLOW + " +" + winPoints + " 积分 · 余烬币 ×" + winCoin
                        + ChatColor.GRAY + "（积分 " + d.getArenaPoints() + " · " + rank.name + "）");
            } else {
                pl.sendMessage(PREFIX + ChatColor.GRAY + "本场参与奖励"
                        + ChatColor.YELLOW + " +" + losePoints + " 积分 · 余烬币 ×" + participateCoin
                        + ChatColor.GRAY + "（胜者 " + winner.getName()
                        + " · 积分 " + d.getArenaPoints() + "）");
            }
        }
    }
}
