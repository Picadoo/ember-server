package town.sunshine.corecombat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pers.coresystem.paper.ShieldNmsHooks;
import pers.coresystem.paper.TotemNmsHooks;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Registers NI totem / shield validators into Paper TotemNmsHooks and ShieldNmsHooks.
 * Also marks cooldown on successful EntityResurrectEvent.
 */
public final class CoreCombatPlugin extends JavaPlugin implements Listener {

    private final Set<String> totemIds = new HashSet<String>();
    private final Set<String> shieldIds = new HashSet<String>();
    private final Map<UUID, Long> totemCooldownUntil = new HashMap<UUID, Long>();

    private boolean totemDisableVanilla = true;
    private boolean shieldDisableVanilla = true;
    private int totemCooldownSeconds = 0;
    private float shieldDurabilityMult = 1.0f;
    private boolean totemHooksAvailable;
    private boolean shieldHooksAvailable;
    private int resurrectOk;
    private int resurrectBlocked;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        totemHooksAvailable = probe("pers.coresystem.paper.TotemNmsHooks");
        shieldHooksAvailable = probe("pers.coresystem.paper.ShieldNmsHooks");
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                pushHooks();
            }
        });
        getLogger().info("CoreCombat scheduled (TotemHooks=" + totemHooksAvailable
                + " ShieldHooks=" + shieldHooksAvailable + ").");
    }

    private boolean probe(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ex) {
            getLogger().severe(className + " not found — run paper-custom.jar.");
            return false;
        }
    }

    private void loadFromConfig() {
        totemIds.clear();
        shieldIds.clear();
        totemDisableVanilla = getConfig().getBoolean("totem.disable_vanilla", true);
        shieldDisableVanilla = getConfig().getBoolean("shield.disable_vanilla", true);
        totemCooldownSeconds = getConfig().getInt("totem.cooldown_seconds", 0);
        shieldDurabilityMult = (float) getConfig().getDouble("shield.durability_loss_multiplier", 1.0);
        List<?> tList = getConfig().getList("totem.allowed_ni_ids");
        if (tList != null) {
            for (Object o : tList) {
                if (o != null) {
                    totemIds.add(String.valueOf(o));
                }
            }
        }
        List<?> sList = getConfig().getList("shield.allowed_ni_ids");
        if (sList != null) {
            for (Object o : sList) {
                if (o != null) {
                    shieldIds.add(String.valueOf(o));
                }
            }
        }
    }

    private void pushHooks() {
        reloadConfig();
        loadFromConfig();
        final Set<String> localTotems = new HashSet<String>(totemIds);
        final Set<String> localShields = new HashSet<String>(shieldIds);
        final int cdSec = totemCooldownSeconds;

        if (totemHooksAvailable) {
            TotemNmsHooks.setOverrideVanilla(totemDisableVanilla);
            TotemNmsHooks.setCooldownSeconds(cdSec);
            TotemNmsHooks.setAllowedCount(localTotems.size());
            TotemNmsHooks.setValidator(new TotemNmsHooks.TotemValidator() {
                @Override
                public boolean isValid(LivingEntity entity, ItemStack stack) {
                    if (stack == null) {
                        return false;
                    }
                    ItemInfo info = ItemManager.INSTANCE.isNiItem(stack);
                    if (info == null || !localTotems.contains(info.getId())) {
                        return false;
                    }
                    if (cdSec > 0 && entity instanceof Player) {
                        Long until = totemCooldownUntil.get(entity.getUniqueId());
                        if (until != null && until > System.currentTimeMillis()) {
                            return false;
                        }
                    }
                    return true;
                }
            });
            getLogger().info("Pushed TotemNmsHooks override=" + totemDisableVanilla
                    + " ids=" + localTotems + " cooldown=" + cdSec + "s active=" + TotemNmsHooks.isActive());
        }

        if (shieldHooksAvailable) {
            ShieldNmsHooks.setOverrideVanilla(shieldDisableVanilla);
            ShieldNmsHooks.setDurabilityLossMultiplier(shieldDurabilityMult);
            ShieldNmsHooks.setAllowedCount(localShields.size());
            ShieldNmsHooks.setValidator(new ShieldNmsHooks.ShieldValidator() {
                @Override
                public boolean canBlock(ItemStack stack) {
                    if (stack == null) {
                        return false;
                    }
                    ItemInfo info = ItemManager.INSTANCE.isNiItem(stack);
                    return info != null && localShields.contains(info.getId());
                }
            });
            getLogger().info("Pushed ShieldNmsHooks override=" + shieldDisableVanilla
                    + " ids=" + localShields + " durMult=" + shieldDurabilityMult
                    + " active=" + ShieldNmsHooks.isActive());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onResurrect(EntityResurrectEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        // Successful resurrect (not cancelled) — start cooldown
        if (totemCooldownSeconds > 0) {
            totemCooldownUntil.put(entity.getUniqueId(),
                    System.currentTimeMillis() + totemCooldownSeconds * 1000L);
        }
        resurrectOk++;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onResurrectHigh(EntityResurrectEvent event) {
        // Count blocked (cancelled) when override is on and no valid NI totem
        if (event.isCancelled() && totemDisableVanilla) {
            resurrectBlocked++;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("corecombat") && !label.equalsIgnoreCase("ccbt")) {
            return false;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("check")) {
            sender.sendMessage("[CoreCombat] totem.disable_vanilla=" + totemDisableVanilla
                    + " ids=" + totemIds + " cooldown=" + totemCooldownSeconds + "s");
            sender.sendMessage("[CoreCombat] shield.disable_vanilla=" + shieldDisableVanilla
                    + " ids=" + shieldIds + " durMult=" + shieldDurabilityMult);
            if (totemHooksAvailable) {
                sender.sendMessage("[CoreCombat] TotemNmsHooks.active=" + TotemNmsHooks.isActive()
                        + " hasOverride=" + TotemNmsHooks.hasOverride()
                        + " allowedCount=" + TotemNmsHooks.getAllowedCount());
            }
            if (shieldHooksAvailable) {
                sender.sendMessage("[CoreCombat] ShieldNmsHooks.active=" + ShieldNmsHooks.isActive()
                        + " hasOverride=" + ShieldNmsHooks.hasOverride()
                        + " durMult=" + ShieldNmsHooks.getDurabilityLossMultiplier()
                        + " allowedCount=" + ShieldNmsHooks.getAllowedCount());
            }
            for (String id : totemIds) {
                sender.sendMessage("[CoreCombat] totem " + id
                        + (ItemManager.INSTANCE.hasItem(id) ? " OK" : " MISSING"));
            }
            for (String id : shieldIds) {
                sender.sendMessage("[CoreCombat] shield " + id
                        + (ItemManager.INSTANCE.hasItem(id) ? " OK" : " MISSING"));
            }
            sender.sendMessage("[CoreCombat] resurrectOk=" + resurrectOk
                    + " resurrectBlockedApprox=" + resurrectBlocked);
            sender.sendMessage("[CoreCombat] give: /ni give <player> totem_ember_life 1");
            sender.sendMessage("[CoreCombat] give: /ni give <player> shield_ember_guard 1");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            pushHooks();
            sender.sendMessage("[CoreCombat] reloaded totem/shield hooks");
            return true;
        }
        sender.sendMessage("/corecombat check|reload");
        return true;
    }
}
