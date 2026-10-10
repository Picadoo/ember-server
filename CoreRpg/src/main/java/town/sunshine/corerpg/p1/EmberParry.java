package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.event.inventory.InventoryType;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D301 / DESIGN-ember-guard-skill-pivot T1: 守招·余烬招架 (G2_parry_B2).
 * After Q03 first-clear, bare Q (drop) binds to parry instead of discard.
 * Boss tele land opens a short window; press in-window → flat ×0.45B to that boss (tele damage still lands).
 * Independent CD; no shared dash tax / slash charge / uniform taken_red.
 * <p>D476: rising-edge ActionBar when independent CD ends (combat ready feel; CD length unchanged).
 */
public final class EmberParry implements Listener {

    private static final String P = ChatColor.GOLD + "[余烬] " + ChatColor.GRAY;
    private static volatile EmberParry INSTANCE;

    private final CoreRpgPlugin plugin;
    /** player → window end epoch ms */
    private final ConcurrentHashMap<UUID, Long> windowUntil = new ConcurrentHashMap<UUID, Long>();
    /** player → boss entity uuid for the open window */
    private final ConcurrentHashMap<UUID, UUID> windowBoss = new ConcurrentHashMap<UUID, UUID>();
    /** player → parry CD end epoch ms */
    private final ConcurrentHashMap<UUID, Long> cdUntil = new ConcurrentHashMap<UUID, Long>();
    /** D476: scheduled CD-ready ActionBar task id per player */
    private final ConcurrentHashMap<UUID, Integer> readyTask = new ConcurrentHashMap<UUID, Integer>();

    public EmberParry(CoreRpgPlugin plugin) {
        this.plugin = plugin;
        INSTANCE = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static EmberParry get() { return INSTANCE; }

    /**
     * Boss telegraph just landed on this player (they were inside the shape). Opens the parry window.
     * Share / non-boss skills must not call this.
     */
    public void openWindow(Player player, LivingEntity boss) {
        if (player == null || boss == null || !player.isOnline()) return;
        PlayerData d = plugin.getDataStore().get(player.getUniqueId());
        if (!EmberSkillKit.parryUnlocked(d, plugin.getEmberRuns())) return;
        long until = System.currentTimeMillis() + EmberSkillKit.PARRY_WINDOW_MS;
        windowUntil.put(player.getUniqueId(), until);
        windowBoss.put(player.getUniqueId(), boss.getUniqueId());
    }

    /** Remaining CD seconds (0 = ready). For PAPI / kit info. */
    public int cdLeftSeconds(UUID id) {
        if (id == null) return 0;
        Long u = cdUntil.get(id);
        if (u == null) return 0;
        long left = u - System.currentTimeMillis();
        return left <= 0 ? 0 : (int) Math.ceil(left / 1000.0);
    }

    public boolean isReady(UUID id) {
        return cdLeftSeconds(id) <= 0;
    }

    /**
     * Bare Q (not sneak): when unlocked, cancel drop and attempt parry.
     * Sneak+Q stays flex/身法 ({@link town.sunshine.corerpg.FlexSkillService}).
     * Priority HIGHEST so we beat {@link EmberBindGuard}'s bind-drop cancel message.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDropParry(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player == null || !player.isOnline()) return;
        if (player.isSneaking()) return;
        if (!EmberMode.active()) return;
        PlayerData data = plugin.getDataStore().get(player.getUniqueId());
        EmberRunService runs = plugin.getEmberRuns();
        if (!EmberSkillKit.parryUnlocked(data, runs)) return;
        InventoryType top = player.getOpenInventory().getType();
        if (top != InventoryType.CRAFTING && top != InventoryType.CREATIVE) return;
        event.setCancelled(true);
        tryParry(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (e.getPlayer() == null) return;
        UUID id = e.getPlayer().getUniqueId();
        windowUntil.remove(id);
        windowBoss.remove(id);
        cancelReadyTask(id);
        // keep cdUntil so a quick reconnect cannot bypass a fresh CD mid-fight
    }

    void tryParry(Player player) {
        long now = System.currentTimeMillis();
        UUID id = player.getUniqueId();
        int left = cdLeftSeconds(id);
        if (left > 0) {
            player.sendMessage(P + ChatColor.RED + EmberSkillKit.DISPLAY_PARRY + "冷却中，剩余 " + left + "s");
            return;
        }
        Long win = windowUntil.get(id);
        UUID bossId = windowBoss.get(id);
        boolean inWindow = win != null && EmberSkillKit.parryWindowOpen(now, win) && bossId != null;
        EmberRunService runs = plugin.getEmberRuns();
        boolean inRun = EmberSkillKit.inDungeon(player, runs);
        if (!inWindow) {
            if (inRun) {
                // Match T0b: outside-window press burns independent CD, no utility
                armCd(player, now + EmberSkillKit.PARRY_CD_MS); // D476 ready cue
                player.sendMessage(P + ChatColor.YELLOW + EmberSkillKit.DISPLAY_PARRY
                        + "空按 · 窗外无效（独立冷却 " + EmberSkillKit.PARRY_CD_SECONDS + "s）");
            } else {
                player.sendMessage(P + "没有可招架的预警（副本里首领预警砸中你后再按 Q）");
            }
            return;
        }
        windowUntil.remove(id);
        windowBoss.remove(id);
        armCd(player, now + EmberSkillKit.PARRY_CD_MS); // D476 ready cue

        LivingEntity boss = resolveBoss(player, bossId);
        EmberLoadoutService ls = plugin.getEmberLoadouts();
        EmberLoadout lo = ls == null ? null : ls.get(player);
        double flat = EmberSkillKit.parryFlat(lo == null ? 0.0 : lo.b);
        if (boss == null || !boss.isValid() || boss.isDead() || flat <= 0) {
            player.sendMessage(P + ChatColor.YELLOW + EmberSkillKit.DISPLAY_PARRY + "时机对了，但目标已不在");
            return;
        }
        String tag = String.format(java.util.Locale.ROOT,
                "D301 %s flat×%.2fB(%.2f)=%.2f", EmberSkillKit.DISPLAY_PARRY,
                EmberSkillKit.PARRY_FLAT_MULT, lo == null ? 0.0 : lo.b, flat);
        EmberCombatListener.dealP1(player, boss, flat, EmberSetEngine.Kind.SKILL, tag);
        player.getWorld().spawnParticle(Particle.CRIT_MAGIC, boss.getLocation().add(0, 1.2, 0), 18, 0.4, 0.5, 0.4, 0.02);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.55f, 1.6f);
        player.sendMessage(P + ChatColor.GREEN + EmberSkillKit.DISPLAY_PARRY + "成功 · 反打 "
                + ChatColor.WHITE + String.format(java.util.Locale.ROOT, "%.0f", flat)
                + ChatColor.GRAY + "（预警伤仍生效 · CD " + EmberSkillKit.PARRY_CD_SECONDS + "s）");
    }


    /** D476 ActionBar line when parry independent CD ends. */
    public static String readyActionBar() {
        return "§a守招 · §f" + EmberSkillKit.DISPLAY_PARRY + " §a就绪";
    }

    /**
     * Start / refresh independent CD and schedule a one-shot ready ActionBar (D476).
     * Does not change {@link EmberSkillKit#PARRY_CD_MS}.
     */
    void armCd(Player player, long untilMs) {
        if (player == null) return;
        UUID id = player.getUniqueId();
        cdUntil.put(id, untilMs);
        scheduleReadyCue(player, untilMs, false);
    }

    /** Admin smoke: arm a short CD then fire the same rising-edge cue (skips unlock gate). */
    public void adminArmCdSeconds(Player player, int seconds) {
        if (player == null) return;
        int sec = Math.max(0, Math.min(60, seconds));
        long until = System.currentTimeMillis() + sec * 1000L;
        cdUntil.put(player.getUniqueId(), until);
        scheduleReadyCue(player, until, true);
    }

    private void cancelReadyTask(UUID id) {
        Integer tid = readyTask.remove(id);
        if (tid != null) {
            try { Bukkit.getScheduler().cancelTask(tid.intValue()); } catch (Throwable ignored) { }
        }
    }

    private void scheduleReadyCue(final Player player, final long untilMs, final boolean force) {
        if (player == null) return;
        final UUID id = player.getUniqueId();
        cancelReadyTask(id);
        long delayMs = Math.max(0L, untilMs - System.currentTimeMillis());
        long delayTicks = Math.max(1L, (delayMs + 49L) / 50L);
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() {
                readyTask.remove(id);
                Player q = Bukkit.getPlayer(id);
                if (q == null || !q.isOnline()) return;
                Long u = cdUntil.get(id);
                // only skip if CD was re-armed past this schedule (avoid early-tick race vs untilMs)
                if (u != null && u.longValue() > untilMs) return;
                if (!force) {
                    PlayerData d = plugin.getDataStore().get(id);
                    if (!EmberSkillKit.parryUnlocked(d, plugin.getEmberRuns())) return;
                }
                try { q.sendActionBar(readyActionBar()); } catch (Throwable ignored) { }
                // chat echo once so combat ready is not ActionBar-only (and smokeable)
                q.sendMessage(P + ChatColor.GREEN + EmberSkillKit.DISPLAY_PARRY + "就绪");
                try {
                    q.playSound(q.getLocation(), Sound.BLOCK_NOTE_PLING, 0.35f, 1.6f);
                } catch (Throwable ignored) { }
            }
        }, delayTicks);
        readyTask.put(id, Integer.valueOf(task.getTaskId()));
    }

    private static LivingEntity resolveBoss(Player player, UUID bossId) {
        if (bossId == null) return null;
        Entity e = Bukkit.getEntity(bossId);
        if (e instanceof LivingEntity) return (LivingEntity) e;
        // fallback: nearest living non-player in same world (rare if entity unloaded)
        if (player.getWorld() == null) return null;
        for (Entity near : player.getNearbyEntities(16, 16, 16)) {
            if (near instanceof LivingEntity && !(near instanceof Player) && near.getUniqueId().equals(bossId)) {
                return (LivingEntity) near;
            }
        }
        return null;
    }
}
