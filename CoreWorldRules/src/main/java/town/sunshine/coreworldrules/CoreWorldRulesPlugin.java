package town.sunshine.coreworldrules;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Bans villager trading, clears vanilla mob item drops, cancels vanilla
 * natural/spawner CreatureSpawnEvent reasons, and removes experience orb
 * entities. CUSTOM / SPAWNER_EGG allowed by config for MythicMobs and admin
 * testing. Does not touch block hardness or block drops.
 *
 * Note (Paper 1.12.2): Experience orbs do not fire CreatureSpawnEvent via
 * World.addEntity — NMS rejects EntityExperienceOrb (CoreSystem-PERF). This
 * plugin also cancels EntitySpawnEvent for EXPERIENCE_ORB and sweeps leftovers.
 * Orb-based XP is not granted silently (giveExp skipped by design).
 *
 * Note: /summon uses SpawnReason.DEFAULT via World.addEntity; with
 * server.properties spawn-animals/monsters=false, non-CUSTOM living adds are
 * rejected before the event. MythicMobs / Bukkit spawnEntity use CUSTOM and
 * still work. SPAWNER_EGG also allowed for admin testing.
 */
public final class CoreWorldRulesPlugin extends JavaPlugin implements Listener {

    private boolean disableTrading = true;
    private boolean clearItemDrops = true;
    private boolean clearXp = true;
    private boolean removeExperienceOrbs = true;
    private int orbSweepIntervalTicks = 100;
    private boolean disableNaturalSpawns = true;
    private boolean disableSpawners = true;
    private boolean allowCustomSpawns = true;
    private boolean allowSpawnerEggs = true;
    private boolean allowBuildSpawns = true;
    private final List<String> excludeNameContains = new ArrayList<String>();
    private int clearedDeaths;
    private int blockedTradeOpens;
    private int cancelledSpawns;
    private int allowedCustomSpawns;
    private int cancelledOrbSpawns;
    private int sweptOrbs;
    private BukkitTask orbSweepTask;

    /** Reasons treated as natural / ecosystem (not CUSTOM / SPAWNER / egg / build). */
    private static final Set<SpawnReason> NATURAL_ECOSYSTEM = EnumSet.of(
            SpawnReason.NATURAL,
            SpawnReason.CHUNK_GEN,
            SpawnReason.JOCKEY,
            SpawnReason.MOUNT,
            SpawnReason.EGG,
            SpawnReason.DISPENSE_EGG,
            SpawnReason.LIGHTNING,
            SpawnReason.VILLAGE_DEFENSE,
            SpawnReason.VILLAGE_INVASION,
            SpawnReason.BREEDING,
            SpawnReason.SLIME_SPLIT,
            SpawnReason.REINFORCEMENTS,
            SpawnReason.NETHER_PORTAL,
            SpawnReason.INFECTION,
            SpawnReason.CURED,
            SpawnReason.OCELOT_BABY,
            SpawnReason.SILVERFISH_BLOCK,
            SpawnReason.TRAP,
            SpawnReason.ENDER_PEARL,
            SpawnReason.SHOULDER_ENTITY,
            SpawnReason.DEFAULT
    );

    private static final Set<SpawnReason> BUILD_REASONS = EnumSet.of(
            SpawnReason.BUILD_SNOWMAN,
            SpawnReason.BUILD_IRONGOLEM,
            SpawnReason.BUILD_WITHER
    );

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadLocal();
        Bukkit.getPluginManager().registerEvents(this, this);
        logSpawnPolicy();
        if (disableTrading) {
            getLogger().info("Villager trading DISABLED — trade UI cancelled, recipes cleared (no trade items).");
        } else {
            getLogger().info("Villager trading allowed (config).");
        }
        getLogger().info("Mob vanilla item drops "
                + (clearItemDrops ? "CLEARED" : "kept")
                + " (clear_xp=" + clearXp + "). Blocks untouched. MythicMobs/NI rewards later.");
        if (removeExperienceOrbs) {
            getLogger().info("Experience orbs REMOVED — no floating XP entities; orb-based XP not granted silently "
                    + "(NMS World.addEntity also rejects; sweep every " + orbSweepIntervalTicks + " ticks).");
        } else {
            getLogger().info("Experience orbs allowed (config experience_orbs.remove=false).");
        }
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                if (!disableTrading) {
                    return;
                }
                int n = 0;
                for (World world : Bukkit.getWorlds()) {
                    for (LivingEntity le : world.getLivingEntities()) {
                        if (le instanceof Villager) {
                            clearVillagerTrades((Villager) le);
                            n++;
                        }
                    }
                }
                getLogger().info("Cleared trade recipes on " + n + " existing villager(s).");
                if (removeExperienceOrbs) {
                    int removed = sweepExperienceOrbs();
                    if (removed > 0) {
                        getLogger().info("Removed " + removed + " existing experience orb(s) on enable.");
                    }
                }
            }
        });
        rescheduleOrbSweep();
    }

    @Override
    public void onDisable() {
        if (orbSweepTask != null) {
            orbSweepTask.cancel();
            orbSweepTask = null;
        }
    }

    private void logSpawnPolicy() {
        getLogger().info("Spawn policy: disable_natural_spawns=" + disableNaturalSpawns
                + " disable_spawners=" + disableSpawners
                + " allow_custom_spawns=" + allowCustomSpawns
                + " allow_spawner_eggs=" + allowSpawnerEggs
                + " allow_build_spawns=" + allowBuildSpawns);
        if (disableNaturalSpawns) {
            getLogger().info("Vanilla natural/ecosystem spawns CANCELLED "
                    + "(NATURAL, CHUNK_GEN, JOCKEY, MOUNT, BREEDING, SLIME_SPLIT, "
                    + "REINFORCEMENTS, VILLAGE_*, NETHER_PORTAL, DEFAULT, …).");
        }
        if (disableSpawners) {
            getLogger().info("Vanilla SPAWNER cage spawns CANCELLED.");
        }
        if (allowCustomSpawns) {
            getLogger().info("CUSTOM spawns ALLOWED (MythicMobs / plugins / Bukkit spawnEntity).");
        } else {
            getLogger().info("CUSTOM spawns CANCELLED (config).");
        }
        if (allowSpawnerEggs) {
            getLogger().info("SPAWNER_EGG ALLOWED (admin/testing).");
        } else {
            getLogger().info("SPAWNER_EGG CANCELLED (config).");
        }
        getLogger().info("Note: Paper 1.12.2 has no RAID/PATROL SpawnReason (N/A). "
                + "/summon uses DEFAULT and may be blocked by spawn-animals/monsters=false; use CUSTOM or SPAWNER_EGG.");
    }

    private void reloadLocal() {
        reloadConfig();
        disableTrading = getConfig().getBoolean("villager.disable_trading", true);
        clearItemDrops = getConfig().getBoolean("mob_loot.clear_item_drops", true);
        clearXp = getConfig().getBoolean("mob_loot.clear_xp", true);
        removeExperienceOrbs = getConfig().getBoolean("experience_orbs.remove", true);
        orbSweepIntervalTicks = getConfig().getInt("experience_orbs.sweep_interval_ticks", 100);
        if (orbSweepIntervalTicks < 0) {
            orbSweepIntervalTicks = 0;
        }
        disableNaturalSpawns = getConfig().getBoolean("spawns.disable_natural_spawns", true);
        disableSpawners = getConfig().getBoolean("spawns.disable_spawners", true);
        allowCustomSpawns = getConfig().getBoolean("spawns.allow_custom_spawns", true);
        allowSpawnerEggs = getConfig().getBoolean("spawns.allow_spawner_eggs", true);
        allowBuildSpawns = getConfig().getBoolean("spawns.allow_build_spawns", true);
        excludeNameContains.clear();
        List<?> list = getConfig().getList("mob_loot.exclude_name_contains");
        if (list != null) {
            for (Object o : list) {
                if (o != null) {
                    excludeNameContains.add(String.valueOf(o));
                }
            }
        }
        if (isEnabled()) {
            rescheduleOrbSweep();
        }
    }

    private void rescheduleOrbSweep() {
        if (orbSweepTask != null) {
            orbSweepTask.cancel();
            orbSweepTask = null;
        }
        if (!removeExperienceOrbs || orbSweepIntervalTicks <= 0) {
            return;
        }
        orbSweepTask = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                sweepExperienceOrbs();
            }
        }, orbSweepIntervalTicks, orbSweepIntervalTicks);
    }

    private int sweepExperienceOrbs() {
        int removed = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(ExperienceOrb.class)) {
                if (entity != null && entity.isValid()) {
                    entity.remove();
                    removed++;
                }
            }
        }
        sweptOrbs += removed;
        return removed;
    }

    private void clearVillagerTrades(Villager villager) {
        try {
            Merchant merchant = villager;
            merchant.setRecipes(Collections.<MerchantRecipe>emptyList());
        } catch (Throwable t) {
            getLogger().warning("Failed to clear villager recipes: " + t.getMessage());
        }
    }

    /**
     * Decide whether this spawn reason should be cancelled under current config.
     */
    boolean shouldCancelSpawn(SpawnReason reason) {
        if (reason == null) {
            return disableNaturalSpawns;
        }
        if (reason == SpawnReason.CUSTOM) {
            return !allowCustomSpawns;
        }
        if (reason == SpawnReason.SPAWNER_EGG) {
            return !allowSpawnerEggs;
        }
        if (reason == SpawnReason.SPAWNER) {
            return disableSpawners;
        }
        if (BUILD_REASONS.contains(reason)) {
            return !allowBuildSpawns;
        }
        if (NATURAL_ECOSYSTEM.contains(reason)) {
            return disableNaturalSpawns;
        }
        // Unknown / future reasons: treat as natural ecosystem when that flag is on
        return disableNaturalSpawns;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawnBan(CreatureSpawnEvent event) {
        SpawnReason reason = event.getSpawnReason();
        if (shouldCancelSpawn(reason)) {
            event.setCancelled(true);
            cancelledSpawns++;
            return;
        }
        if (reason == SpawnReason.CUSTOM) {
            allowedCustomSpawns++;
        }
    }

    /**
     * Defense-in-depth: cancel EXPERIENCE_ORB if any path fires EntitySpawnEvent.
     * Primary block is NMS World.addEntity (Paper 1.12.2 does not fire spawn events for orbs).
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpawnOrb(EntitySpawnEvent event) {
        if (!removeExperienceOrbs) {
            return;
        }
        if (event.getEntityType() == EntityType.EXPERIENCE_ORB || event.getEntity() instanceof ExperienceOrb) {
            event.setCancelled(true);
            cancelledOrbSpawns++;
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (!disableTrading) {
            return;
        }
        Entity clicked = event.getRightClicked();
        if (clicked instanceof Villager) {
            clearVillagerTrades((Villager) clicked);
            event.setCancelled(true);
            blockedTradeOpens++;
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!disableTrading) {
            return;
        }
        if (event.getInventory().getType() == InventoryType.MERCHANT) {
            event.setCancelled(true);
            blockedTradeOpens++;
            if (event.getInventory().getHolder() instanceof Villager) {
                clearVillagerTrades((Villager) event.getInventory().getHolder());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawnVillager(CreatureSpawnEvent event) {
        if (!disableTrading) {
            return;
        }
        if (event.getEntity() instanceof Villager) {
            final Villager v = (Villager) event.getEntity();
            Bukkit.getScheduler().runTask(this, new Runnable() {
                @Override
                public void run() {
                    if (v.isValid()) {
                        clearVillagerTrades(v);
                    }
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            return;
        }
        if (isExcluded(entity)) {
            return;
        }
        if (clearItemDrops) {
            event.getDrops().clear();
            clearedDeaths++;
        }
        if (clearXp || removeExperienceOrbs) {
            event.setDroppedExp(0);
        }
    }

    private boolean isExcluded(LivingEntity entity) {
        if (excludeNameContains.isEmpty()) {
            return false;
        }
        String name = entity.getName();
        String custom = entity.getCustomName();
        for (String marker : excludeNameContains) {
            if (marker == null || marker.isEmpty()) {
                continue;
            }
            if (name != null && name.contains(marker)) {
                return true;
            }
            if (custom != null && custom.contains(marker)) {
                return true;
            }
        }
        return false;
    }

    /** Spawn a zombie via Bukkit API (SpawnReason.CUSTOM) for verification. */
    private boolean testCustomSpawn(CommandSender sender) {
        Location loc;
        if (sender instanceof Player) {
            loc = ((Player) sender).getLocation().add(0, 1, 0);
        } else {
            World w = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
            if (w == null) {
                sender.sendMessage("[CoreWorldRules] testspawn: no world");
                return true;
            }
            loc = w.getSpawnLocation().add(0, 1, 0);
        }
        try {
            Entity e = loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
            if (e != null && e.isValid()) {
                sender.sendMessage("[CoreWorldRules] testspawn OK — CUSTOM zombie id="
                        + e.getEntityId() + " at " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()
                        + " (allowedCustomSpawns=" + allowedCustomSpawns + ")");
                getLogger().info("testspawn CUSTOM zombie succeeded entityId=" + e.getEntityId());
                // remove shortly so worlds stay clean
                final Entity doomed = e;
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        if (doomed.isValid()) {
                            doomed.remove();
                        }
                    }
                }, 40L);
            } else {
                sender.sendMessage("[CoreWorldRules] testspawn FAILED — spawnEntity returned null/invalid (CUSTOM blocked?)");
                getLogger().warning("testspawn CUSTOM zombie failed");
            }
        } catch (Throwable t) {
            sender.sendMessage("[CoreWorldRules] testspawn ERROR: " + t.getMessage());
            getLogger().warning("testspawn error: " + t.getMessage());
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("coreworldrules") && !label.equalsIgnoreCase("cwr")) {
            return false;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("check")) {
            sender.sendMessage("[CoreWorldRules] tradingDisabled=" + disableTrading
                    + " clearItemDrops=" + clearItemDrops
                    + " clearXp=" + clearXp
                    + " removeOrbs=" + removeExperienceOrbs
                    + " blockedTradeOpens=" + blockedTradeOpens
                    + " clearedDeaths=" + clearedDeaths);
            sender.sendMessage("[CoreWorldRules] orbs: cancelledOrbSpawns=" + cancelledOrbSpawns
                    + " sweptOrbs=" + sweptOrbs
                    + " sweepInterval=" + orbSweepIntervalTicks
                    + " (orb-based XP gone; no silent giveExp)");
            sender.sendMessage("[CoreWorldRules] spawnBans: natural=" + disableNaturalSpawns
                    + " spawners=" + disableSpawners
                    + " allowCustom=" + allowCustomSpawns
                    + " allowEggs=" + allowSpawnerEggs
                    + " allowBuild=" + allowBuildSpawns
                    + " cancelledSpawns=" + cancelledSpawns
                    + " allowedCustomSpawns=" + allowedCustomSpawns);
            sender.sendMessage("[CoreWorldRules] exclude_name_contains=" + excludeNameContains
                    + " (blocks untouched)");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            reloadLocal();
            sender.sendMessage("[CoreWorldRules] reloaded — tradingDisabled=" + disableTrading
                    + " clearItemDrops=" + clearItemDrops + " clearXp=" + clearXp
                    + " removeOrbs=" + removeExperienceOrbs);
            sender.sendMessage("[CoreWorldRules] spawnBans: natural=" + disableNaturalSpawns
                    + " spawners=" + disableSpawners
                    + " allowCustom=" + allowCustomSpawns
                    + " allowEggs=" + allowSpawnerEggs
                    + " allowBuild=" + allowBuildSpawns);
            return true;
        }
        if (args[0].equalsIgnoreCase("testspawn")) {
            return testCustomSpawn(sender);
        }
        sender.sendMessage("/coreworldrules check|reload|testspawn");
        return true;
    }
}
