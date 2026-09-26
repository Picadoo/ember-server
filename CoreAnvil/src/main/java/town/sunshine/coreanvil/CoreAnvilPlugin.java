package town.sunshine.coreanvil;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pers.coresystem.paper.AnvilNmsHooks;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Loads YAML anvil policy and pushes it into AnvilNmsHooks.
 * Also registers {@link NiMaterialRepairListener} so require_ni_repair_ingredient
 * works when the right slot is an NI shard/fragment (not a vanilla repair mat).
 */
public final class CoreAnvilPlugin extends JavaPlugin {

    private boolean hooksAvailable;
    private boolean enabled = true;
    private int renameCost = 5;
    private int maxLevelCost = 30;
    private double priorWorkFactor = 2.0;
    private boolean blockEnchantCombines = true;
    private boolean materialRepairOnly = true;
    private boolean requireNiRepair = false;
    private int materialRepairExtra = 2;
    private final Set<String> repairNiIds = new HashSet<String>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        hooksAvailable = probeHooks();
        getServer().getPluginManager().registerEvents(new NiMaterialRepairListener(this), this);
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                reloadAll();
            }
        });
        getLogger().info("CoreAnvil scheduled (AnvilNmsHooks available=" + hooksAvailable
                + ", NiMaterialRepairListener registered).");
    }

    private boolean probeHooks() {
        try {
            Class.forName("pers.coresystem.paper.AnvilNmsHooks");
            return true;
        } catch (ClassNotFoundException ex) {
            getLogger().severe("AnvilNmsHooks not found — use custom Paper jar (paper-custom.jar).");
            return false;
        }
    }

    private void loadFromConfig() {
        repairNiIds.clear();
        enabled = getConfig().getBoolean("enabled", true);
        renameCost = getConfig().getInt("rename_cost", 5);
        maxLevelCost = getConfig().getInt("max_level_cost", 30);
        priorWorkFactor = getConfig().getDouble("prior_work_factor", 2.0);
        blockEnchantCombines = getConfig().getBoolean("block_enchant_combines", true);
        materialRepairOnly = getConfig().getBoolean("material_repair_only", true);
        requireNiRepair = getConfig().getBoolean("require_ni_repair_ingredient", false);
        materialRepairExtra = getConfig().getInt("material_repair_extra_cost", 2);
        List<?> list = getConfig().getList("repair_ingredient_ni_ids");
        if (list != null) {
            for (Object o : list) {
                if (o != null) {
                    repairNiIds.add(String.valueOf(o));
                }
            }
        }
        getLogger().info("Loaded anvil policy: rename=" + renameCost + " max=" + maxLevelCost
                + " priorFactor=" + priorWorkFactor
                + " blockEnchant=" + blockEnchantCombines
                + " materialOnly=" + materialRepairOnly
                + " requireNiRepair=" + requireNiRepair
                + " repairNiIds=" + repairNiIds);
    }

    private void reloadAll() {
        reloadConfig();
        loadFromConfig();
        pushHooks();
    }

    private void pushHooks() {
        if (!hooksAvailable) {
            return;
        }
        AnvilNmsHooks.setDefaults(renameCost, maxLevelCost, priorWorkFactor,
                blockEnchantCombines, materialRepairOnly);
        if (!enabled) {
            AnvilNmsHooks.setPolicy(null);
            AnvilNmsHooks.setActive(false);
            getLogger().info("CoreAnvil disabled via config — AnvilNmsHooks inactive.");
            return;
        }
        final int localRename = renameCost;
        final int localMax = maxLevelCost;
        final double localFactor = priorWorkFactor;
        final boolean localBlock = blockEnchantCombines;
        final boolean localMatOnly = materialRepairOnly;
        final boolean localRequireNi = requireNiRepair;
        final int localExtra = materialRepairExtra;
        final Set<String> localIds = new HashSet<String>(repairNiIds);

        AnvilNmsHooks.setPolicy(new AnvilNmsHooks.AnvilPolicy() {
            @Override
            public int renameCost() {
                return localRename;
            }

            @Override
            public int maxLevelCost() {
                return localMax;
            }

            @Override
            public int nextRepairPenalty(int maxIncomingRepairCost) {
                return (int) Math.floor(maxIncomingRepairCost * localFactor) + 1;
            }

            @Override
            public boolean blockEnchantCombines() {
                return localBlock;
            }

            @Override
            public boolean materialRepairOnly() {
                return localMatOnly;
            }

            @Override
            public Boolean allowRepairIngredient(ItemStack left, ItemStack right) {
                if (!localRequireNi) {
                    return null; // vanilla material check
                }
                if (right == null) {
                    return Boolean.FALSE;
                }
                ItemInfo info = ItemManager.INSTANCE.isNiItem(right);
                if (info == null) {
                    return Boolean.FALSE;
                }
                if (!localIds.isEmpty() && !localIds.contains(info.getId())) {
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }

            @Override
            public int adjustLevelCost(int computedCost, boolean renameOnly, boolean materialRepair) {
                int cost = computedCost;
                if (materialRepair && !renameOnly) {
                    cost += localExtra;
                }
                if (cost > localMax) {
                    cost = localMax;
                }
                return cost;
            }
        });
        AnvilNmsHooks.setActive(true);
        getLogger().info("Pushed anvil policy into AnvilNmsHooks (active=" + AnvilNmsHooks.isActive() + ").");
    }

    /** Config {@code enabled} — not Bukkit's isEnabled(). */
    public boolean isPluginEnabled() {
        return enabled;
    }

    public boolean isRequireNiRepair() {
        return requireNiRepair;
    }

    public Set<String> getRepairNiIds() {
        return Collections.unmodifiableSet(repairNiIds);
    }

    public int getMaterialRepairExtra() {
        return materialRepairExtra;
    }

    public int getMaxLevelCost() {
        return maxLevelCost;
    }

    public int getRenameCost() {
        return renameCost;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("coreanvil")) {
            return false;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("check")) {
            sender.sendMessage("[CoreAnvil] hooksAvailable=" + hooksAvailable + " enabled=" + enabled);
            sender.sendMessage("[CoreAnvil] rename=" + renameCost + " max=" + maxLevelCost
                    + " priorFactor=" + priorWorkFactor
                    + " blockEnchant=" + blockEnchantCombines
                    + " materialOnly=" + materialRepairOnly
                    + " requireNiRepair=" + requireNiRepair
                    + " extra=" + materialRepairExtra);
            sender.sendMessage("[CoreAnvil] repair_ingredient_ni_ids=" + repairNiIds);
            if (hooksAvailable) {
                sender.sendMessage("[CoreAnvil] AnvilNmsHooks.active=" + AnvilNmsHooks.isActive()
                        + " renameCost=" + AnvilNmsHooks.renameCost()
                        + " maxLevelCost=" + AnvilNmsHooks.maxLevelCost()
                        + " blockEnchant=" + AnvilNmsHooks.blockEnchantCombines()
                        + " materialOnly=" + AnvilNmsHooks.materialRepairOnly());
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            reloadAll();
            sender.sendMessage("[CoreAnvil] reloaded YAML + re-pushed AnvilNmsHooks");
            return true;
        }
        sender.sendMessage("/coreanvil check|reload");
        return true;
    }
}
