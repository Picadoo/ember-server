package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * D233 / ARCH S3 step 4: 团本 (P2-5 raids R01–R03) logic extracted from {@link EmberRunService}
 * with <b>no behaviour change</b>. Owns the {@code p2_raid_<capKey>} weekly counter (P2-5 / P2-6
 * cap_group), entry problem lines, menu / PAPI labels, settle grants ({@code raid_item} +
 * {@code raid_mark}, S12), the failed-raid "count not burned" line (D133), the raid start line, and the
 * D106 fallen-member flow (watch a teammate, auto-revive at room / boss / phase, leash, held /dp leave,
 * {@code /corerpg p1 watch}).
 * <p>{@link EmberRunService} keeps thin delegates and the shared lifecycle: {@code enter} still checks
 * party / stamina / cap per member (calling {@link #entryProblem}), settle still owns the shared
 * bounty weight / cosmetics / season calls, the Bukkit {@code @EventHandler}s stay registered there and
 * forward here. The 招募 board (Recruit, D104 / E-review #5) is <b>not</b> part of this cut.
 * <p>{@link #capKey}, {@link #counterKey}, {@link #weekCount}, {@link #capReached}, {@link #entryProblemText},
 * {@link #labelText}, {@link #failNoBurnText}, {@link #settleGrants}, {@link #applyClearCount},
 * {@link #startText} are Bukkit-free so unit tests can pin counter / grant shapes against the bundled
 * {@code ember-v1-runs.yml} (bv58 unchanged).
 */
public final class EmberRaidService {

    /** + capKey (cap_group or raid key), period = rotation week key: settled raid clears this week (P2-5 / P2-6) */
    static final String C_RAID = "p2_raid_";
    /** settle ledger keys (S12) */
    static final String G_ITEM = "raid_item";
    static final String G_MARK = "raid_mark";

    private final EmberRunService runs;

    EmberRaidService(EmberRunService runs) {
        this.runs = runs;
    }

    private static String P() { return EmberRunService.P; }

    // ------------------------------------------------------------------ Bukkit-free counter / text / grants

    /** P2-6 (D78): the weekly counter of a raid = its cap_group, else its own key */
    static String capKey(EmberRunMaps.MapDef m) { return m.capGroup == null || m.capGroup.isEmpty() ? m.key : m.capGroup; }

    /** full counter key {@code p2_raid_<capKey>} */
    static String counterKey(EmberRunMaps.MapDef m) { return C_RAID + capKey(m); }

    /** settled clears of this raid (its cap group) in week {@code weekKey} */
    public static int weekCount(PlayerData d, EmberRunMaps.MapDef m, String weekKey) {
        return d.periodCount(counterKey(m), weekKey);
    }

    /** P2-5: the weekly cap blocks a new entry (cap 0 = no limit) */
    public static boolean capReached(EmberRunMaps.MapDef m, int used) {
        return m.weeklyCap > 0 && used >= m.weeklyCap;
    }

    /**
     * P2-5 entry problem for one member: own first clear of {@code requires}, then the weekly cap.
     * {@code used} is only read when {@code open}. Null = no problem.
     */
    public static String entryProblemText(String name, EmberRunMaps.MapDef m, boolean open, int used) {
        if (!open) return name + " 未开放团本（需本人首通 " + m.requires.toUpperCase(Locale.ROOT) + "）";
        if (capReached(m, used)) return name + " 本周团本次数已满（" + used + "/" + m.weeklyCap + "，周一 0 点重置）";
        return null;
    }

    /** menu / PAPI label (P2-5 %corerpg_p1_raid_r01%) */
    public static String labelText(EmberRunMaps.MapDef m, boolean open, int used, int partyMin, int partyMax, int cost) {
        if (!open) return "需本人首通 " + m.requires.toUpperCase(Locale.ROOT);
        return "本周 " + used + "/" + m.weeklyCap + (capKey(m).equals(m.key) ? "" : "（团本合计）") + " · " + partyMin + "～" + partyMax + " 人 · " + cost + " 体力";
    }

    /** recheck #3 (D133): the weekly raid count only moves on a clear — second line of the fail message */
    public static String failNoBurnText(EmberRunMaps.MapDef m, int used) {
        return "§a本周团本次数没有扣§7：还是 " + used + "/" + m.weeklyCap + (capKey(m).equals(m.key) ? "" : "（团本合计）")
                + (used < m.weeklyCap ? "，体力够就可以再来（只有通关才算一次）" : "");
    }

    /** P2-5 + P2-9 (D82): one targeted T3 roll (floor 精良) + 1 mark of the run tier; titles / trail are cosmetic */
    public static List<EmberRunRules.Grant> settleGrants(EmberRunRules.SettleInput in, EmberRunMaps.MapDef m, int qualityFloor, int tier) {
        List<EmberRunRules.Grant> out = new ArrayList<EmberRunRules.Grant>(2);
        out.add(EmberRunRules.raidItem(in, G_ITEM, m.lootFamily, qualityFloor)); // P2-9 (D82)
        out.add(new EmberRunRules.Grant("raid_mark", EmberRunRules.Kind.MARK, String.valueOf(tier), 1, null)); // literal key: EmberEconomyTest ledger-key scan (D228)
        return out;
    }

    /**
     * P2-5 weekly cap (P2-6: per cap_group): a newly recorded {@code raid_mark} ledger row counts one clear.
     * Returns true when the counter moved. Mutates {@code pd} only (no flush).
     */
    public static boolean applyClearCount(PlayerData pd, EmberRunMaps.MapDef m, String weekKey, String grantKey, boolean created) {
        if (!created || !G_MARK.equals(grantKey)) return false;
        pd.addPeriodCount(counterKey(m), weekKey, 1);
        return true;
    }

    /** the raid start line (after the party HP / damage factors are locked) */
    public static String startText(int partySize, double hpFactor, double dmgFactor) {
        return "§6团本开始 §7· " + partySize + " 人 · 掉落 T3 · 敌方生命 ×" + String.format(Locale.ROOT, "%.2f", hpFactor)
                + " 伤害 ×" + String.format(Locale.ROOT, "%.2f", dmgFactor) + " · 倒下后观战队友，下一个房间开打、首领转阶段时自动复活（50% 生命），首领最后 20% 生命再复活一次 · 走进前方房间开战 · 首领死后统一结算";
    }

    // ------------------------------------------------------------------ live counter / text wrappers

    static String weekKey() {
        return EmberRunRules.rotationWeekKey(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()));
    }

    /** P2-5: settled clears of this raid in the current Monday-based week */
    int week(PlayerData d, EmberRunMaps.MapDef m) { return weekCount(d, m, weekKey()); }

    String label(PlayerData d, EmberRunMaps.MapDef m) {
        EmberRunMaps maps = runs.maps();
        boolean open = runs.progressFlag(d, m.requires);
        return labelText(m, open, open ? week(d, m) : 0, maps.partyMin(m), maps.partyMax(m), maps.cost(m));
    }

    /** P2-5: own Q07 first clear + weekly cap of settled clears (null = ok) */
    String entryProblem(Player p, PlayerData d, EmberRunMaps.MapDef m) {
        boolean open = runs.progressFlag(d, m.requires);
        return entryProblemText(p.getName(), m, open, open && m.weeklyCap > 0 ? week(d, m) : 0);
    }

    /** recheck #3 (D133): a failed raid says the weekly count was not used */
    void tellFailNoBurn(EmberRunSession s, EmberRunMaps.MapDef fm, String why) {
        for (UUID u : s.participants) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline()) continue;
            int used = week(runs.dataOf(u), fm);
            p.sendMessage(P() + ChatColor.RED + "本局失败：" + why + "（已开战不退体力；未结算的额外奖励作废）");
            p.sendMessage(P() + failNoBurnText(fm, used));
        }
    }

    /** raid start lines (verifyEntry) */
    void onStart(EmberRunSession s, EmberRunMaps.MapDef vm) {
        runs.tellRun(s, startText(s.partySize, s.hpFactor, s.dmgFactor));
        if (!vm.partyHint.isEmpty()) runs.tellRun(s, "§e" + vm.partyHint); // D166
    }

    /** /corerpg p1 runs: one [开本] line per raid */
    void menuButtons(Player p, PlayerData d) {
        for (EmberRunMaps.MapDef rm : runs.maps().raids.values())
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P() + "§6团本 " + rm.key.toUpperCase(Locale.ROOT) + " " + rm.name + " §7" + label(d, rm) + " ",
                    new String[]{"[开本]", "/corerpg p1 enter " + rm.key, "队长点：全队需各自首通 Q07，3～5 人", "GOLD"});
    }

    /** P2-5 %corerpg_p1_raid_&lt;key&gt;% */
    String papi(PlayerData d, String raidKey) {
        EmberRunMaps.MapDef rm = runs.maps().raids.get(raidKey);
        return rm == null ? "" : label(d, rm);
    }

    // ------------------------------------------------------------------ D106 raid falls: watch a teammate, revive later

    boolean isRaid(EmberRunSession s) {
        EmberRunMaps.MapDef m = runs.maps().byKey(s.mapKey);
        return m != null && m.raid;
    }

    /** committed members standing in the instance (not fallen, not left, not spectating) */
    List<Player> livingIn(EmberRunSession s) {
        List<Player> out = new ArrayList<Player>();
        for (UUID u : s.committed) {
            if (s.died.contains(u) || s.left.contains(u)) continue;
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline() || p.isDead() || p.getGameMode() == GameMode.SPECTATOR) continue;
            if (s.world != null && !s.world.equals(p.getWorld().getName())) continue;
            out.add(p);
        }
        return out;
    }

    Player nearestLiving(EmberRunSession s, Player from) {
        Player best = null;
        double bd = Double.MAX_VALUE;
        for (Player o : livingIn(s)) {
            double d = o.getWorld() == from.getWorld() ? o.getLocation().distanceSquared(from.getLocation()) : Double.MAX_VALUE / 2;
            if (best == null || d < bd) { best = o; bd = d; }
        }
        return best;
    }

    String nextReviveText(EmberRunSession s) {
        EmberRunDirector d = s.world == null ? null : runs.director(s.world);
        String n = d == null ? null : d.nextRevive();
        return n == null ? "本局没有复活点了，等队友打完（首领死后照常结算）" : n + "自动复活（50% 生命）";
    }

    /** a fallen raid member: spectator mode, camera on a living teammate, plus the [观战队友] button and next revive */
    void watchTeammate(Player p, EmberRunSession s, boolean tell) {
        p.setGameMode(GameMode.SPECTATOR);
        Player t = nearestLiving(s, p);
        if (t != null) {
            if (p.getWorld() != t.getWorld() || p.getLocation().distanceSquared(t.getLocation()) > 4) p.teleport(t.getLocation());
            try { p.setSpectatorTarget(t); } catch (Throwable ignored) { }
        }
        if (tell) {
            p.sendMessage(P() + "§c你已倒下§7：观战队友" + (t == null ? "" : " §f" + t.getName()) + "§7 · 下一次复活：§e" + nextReviveText(s));
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P(), new String[]{"[观战队友]", "/corerpg p1 watch", "换下一个还站着的队友", "AQUA"});
        }
    }

    /** D106: once the fallen raider is back on their feet (DP auto-respawn), switch to watching a teammate. */
    void watchLater(final Player p, final EmberRunSession rs, long delay, final int tries) {
        Bukkit.getScheduler().runTaskLater(runs.plugin(), () -> {
            if (!p.isOnline() || !rs.open() || !rs.died.contains(p.getUniqueId()) || rs.world == null
                    || !rs.world.equals(p.getWorld().getName())) return;
            if (p.isDead()) { if (tries > 0) watchLater(p, rs, 20L, tries - 1); return; }
            watchTeammate(p, rs, true);
        }, delay);
    }

    /** D106: a raid member fell with teammates still standing — tell the run and put the camera on a teammate. */
    void onFall(Player p, EmberRunSession s) {
        runs.tellRun(s, "§c" + p.getName() + " 倒下 §7· 下一次复活：§e" + nextReviveText(s));
        watchLater(p, s, 10L, 3); // DP keeps the fallen raider in the instance (dead state) — put the camera on a teammate
    }

    /** D106: revive every fallen raid member still in the instance at 50 % HP next to a living teammate. */
    void reviveFallen(EmberRunSession s, String why) {
        if (!s.open() || s.died.isEmpty() || !isRaid(s)) return;
        List<Player> alive = livingIn(s);
        if (alive.isEmpty()) return;
        List<String> names = new ArrayList<String>();
        for (UUID u : new ArrayList<UUID>(s.died)) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline() || s.left.contains(u) || p.isDead()) continue;
            if (s.world == null || !s.world.equals(p.getWorld().getName())) continue;
            Player a0 = nearestLiving(s, p);
            final Player a = a0 == null ? alive.get(0) : a0;
            try { p.setSpectatorTarget(null); } catch (Throwable ignored) { }
            // DP keeps a fallen raider in its own "dead" state (revive=true, number=0): clear it first, or DP would
            // count the revived player as dead and end the dungeon when the last DP-alive member falls.
            try { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dp revive " + p.getName() + " true true"); }
            catch (RuntimeException ex) { runs.log().warning("[P1 run] dp revive " + p.getName() + ": " + ex); }
            s.died.remove(u);
            names.add(p.getName());
            final String world = s.world;
            Bukkit.getScheduler().runTaskLater(runs.plugin(), () -> { // after DP's own respawn teleport
                if (!p.isOnline() || !world.equals(p.getWorld().getName())) return;
                Location to = a.isOnline() && a.getWorld() == p.getWorld() ? a.getLocation() : p.getLocation();
                p.teleport(to);
                p.setGameMode(GameMode.ADVENTURE);
                double max = EmberHeal.maxHp(p);
                p.setHealth(Math.max(1.0, Math.min(max, max * 0.5)));
                EmberHeal.rebase(p); // sanctioned HP change: the B2.144 guard must not revert it
                p.setFireTicks(0);
                p.setFallDistance(0f);
                p.sendMessage(P() + "§a" + why + "：你已复活（50% 生命），回到 §f" + a.getName() + " §a身边");
            }, 3L);
            Bukkit.getScheduler().runTaskLater(runs.plugin(), () -> { // DP may put the player back on the death point a bit later
                if (!p.isOnline() || !a.isOnline() || a.getWorld() != p.getWorld() || !world.equals(p.getWorld().getName())) return;
                if (p.getLocation().distanceSquared(a.getLocation()) > 64) p.teleport(a.getLocation());
            }, 20L);
        }
        if (names.isEmpty()) return;
        runs.store().save(s);
        runs.tellRun(s, "§a" + why + " · 复活：§f" + String.join("、", names));
        runs.log().info("[P1 run] " + s.runId + " raid revive (" + why + "): " + names);
    }

    private final Map<UUID, Long> leashTold = new java.util.concurrent.ConcurrentHashMap<UUID, Long>();

    /** D106: once a second — a fallen raid member who drifts more than 24 blocks from every living teammate (or out of
     *  the instance world) is put back on a teammate's camera. */
    void leashFallen(EmberRunDirector d) {
        EmberRunSession s = d.s;
        if (!s.open() || s.died.isEmpty()) return;
        for (UUID u : s.died) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline() || p.getGameMode() != GameMode.SPECTATOR || s.left.contains(u)) continue;
            if (p.getWorld() != d.w) continue; // left the instance: handled by onChangedWorld (counts as left)
            if (p.getSpectatorTarget() != null) continue;
            Player t = nearestLiving(s, p);
            if (t == null) continue;
            if (t.getLocation().distanceSquared(p.getLocation()) > 24 * 24) {
                watchTeammate(p, s, false);
                Long last = leashTold.get(u); // F-review #7: once every 10 s, not every second
                long now = System.currentTimeMillis();
                if (last == null || now - last >= 10_000L) {
                    leashTold.put(u, now);
                    p.sendMessage(P() + "§7倒下时只能在队友身边 24 格内观战 · 下一次复活：§e" + nextReviveText(s));
                }
            }
        }
    }

    /** D106: spectator-menu teleports out of the raid instance are blocked for fallen members (event stays in EmberRunService). */
    void onSpectateTeleport(org.bukkit.event.player.PlayerTeleportEvent e) {
        if (e.getCause() != org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.SPECTATE || e.getTo() == null) return;
        Player p = e.getPlayer();
        EmberRunDirector d = runs.director(p.getWorld().getName());
        if (d == null || !d.def.raid || !d.s.died.contains(p.getUniqueId())) return;
        if (e.getTo().getWorld() != d.w) {
            e.setCancelled(true);
            p.sendMessage(P() + "§7倒下时只能观战本局队友。");
        }
    }

    private final Map<UUID, Long> leaveAsked = new HashMap<UUID, Long>();

    /** D106: a fallen raid member cannot walk out of the instance — /dp leave is held once; a second /dp leave within
     *  10 s is a deliberate give-up (counts as leaving: no revive, no settlement). Hub commands are blocked by QuestService. */
    void onFallenLeave(org.bukkit.event.player.PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        EmberRunDirector d = runs.director(p.getWorld().getName());
        if (d == null || !d.def.raid || !d.s.open() || !d.s.died.contains(p.getUniqueId())) return;
        String[] a = e.getMessage().replaceFirst("^/", "").trim().toLowerCase(Locale.ROOT).split("\\s+");
        String root = a[0].contains(":") ? a[0].substring(a[0].indexOf(':') + 1) : a[0];
        if ((root.equals("dp") || root.startsWith("dungeon")) && a.length >= 2 && "revive".equals(a[1])) { // F-review #7
            e.setCancelled(true);
            p.sendMessage(P() + "§7团本里倒下后不能自己复活 · 下一次复活：§e" + nextReviveText(d.s));
            if (runs.plugin().getQuestService() != null) runs.plugin().getQuestService().quietHint(p.getUniqueId()); // no second "不可用" line
            return;
        }
        if (!(root.equals("dp") || root.startsWith("dungeon")) || a.length < 2 || !"leave".equals(a[1])) return;
        Long t = leaveAsked.get(p.getUniqueId());
        long now = System.currentTimeMillis();
        if (t != null && now - t < 10000L) { leaveAsked.remove(p.getUniqueId()); return; } // confirmed give-up
        leaveAsked.put(p.getUniqueId(), now);
        e.setCancelled(true);
        if (runs.plugin().getQuestService() != null) runs.plugin().getQuestService().quietHint(p.getUniqueId());
        p.sendMessage(P() + "§c倒下后不能离开团本§7：下一次复活 §e" + nextReviveText(d.s)
                + "§7；本局结束会自动送回。§c确实要放弃本局结算§7：10 秒内再输一次 /dp leave");
    }

    /** /corerpg p1 watch — a fallen raid member cycles the camera through the living teammates. */
    boolean cmdWatch(CommandSender sender) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        EmberRunDirector d = runs.director(p.getWorld().getName());
        if (d == null || !d.s.died.contains(p.getUniqueId()) || !d.s.open()) { p.sendMessage(P() + "只有团本里倒下的人可以观战队友。"); return true; }
        List<Player> alive = livingIn(d.s);
        if (alive.isEmpty()) { p.sendMessage(P() + "没有还站着的队友。"); return true; }
        Entity cur = p.getSpectatorTarget();
        int i = 0;
        for (int k = 0; k < alive.size(); k++) if (alive.get(k).equals(cur)) { i = (k + 1) % alive.size(); break; }
        Player t = alive.get(i);
        p.setGameMode(GameMode.SPECTATOR);
        try { p.setSpectatorTarget(null); } catch (Throwable ignored) { }
        p.teleport(t.getLocation());
        try { p.setSpectatorTarget(t); } catch (Throwable ignored) { }
        p.sendMessage(P() + "§7正在观战 §f" + t.getName() + " §7· 下一次复活：§e" + nextReviveText(d.s));
        return true;
    }
}
