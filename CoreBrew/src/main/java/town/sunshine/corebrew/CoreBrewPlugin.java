package town.sunshine.corebrew;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pers.coresystem.paper.BrewNmsHooks;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Registers NI brewing recipes into Paper BrewNmsHooks and optionally disables vanilla PotionBrewer matches.
 * Also pushes raised stack limits (default 127) for brew NI items + brewing-stand slots.
 */
public final class CoreBrewPlugin extends JavaPlugin {

    private static final class BrewEntry {
        final String ingredientNiId;
        final String bottleNiId;
        final String resultNiId;
        final int brewTimeTicks;
        final int resultAmount;

        BrewEntry(String ingredientNiId, String bottleNiId, String resultNiId, int brewTimeTicks, int resultAmount) {
            this.ingredientNiId = ingredientNiId;
            this.bottleNiId = bottleNiId;
            this.resultNiId = resultNiId;
            this.brewTimeTicks = brewTimeTicks;
            this.resultAmount = resultAmount;
        }

        String ruleKey() {
            return ingredientNiId + "|" + bottleNiId;
        }
    }

    private final List<BrewEntry> recipes = new ArrayList<BrewEntry>();
    private final Map<String, BrewEntry> byKey = new HashMap<String, BrewEntry>();
    private final Set<String> ingredientIds = new HashSet<String>();
    private final Set<String> bottleIds = new HashSet<String>();
    private final Set<String> resultIds = new HashSet<String>();

    private int pushedHooks;
    private boolean hooksAvailable;
    private boolean disableVanilla = true;
    private int maxStack = BrewNmsHooks.DEFAULT_MAX_STACK;
    private boolean applyToIngredients = true;
    private boolean applyToBottles = true;
    private boolean applyToResults = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        hooksAvailable = probeHooks();
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                reloadAll();
            }
        });
        getLogger().info("CoreBrew HYBRID scheduled (BrewNmsHooks available=" + hooksAvailable + ").");
    }

    private boolean probeHooks() {
        try {
            Class.forName("pers.coresystem.paper.BrewNmsHooks");
            return true;
        } catch (ClassNotFoundException ex) {
            getLogger().severe("BrewNmsHooks not found — use custom Paper jar. Brew times/recipes will NOT enforce.");
            return false;
        }
    }

    private void loadFromConfig() {
        recipes.clear();
        byKey.clear();
        ingredientIds.clear();
        bottleIds.clear();
        resultIds.clear();
        disableVanilla = getConfig().getBoolean("disable_vanilla", true);
        maxStack = getConfig().getInt("stack.max_stack", BrewNmsHooks.DEFAULT_MAX_STACK);
        applyToIngredients = getConfig().getBoolean("stack.apply_to_ingredients", true);
        applyToBottles = getConfig().getBoolean("stack.apply_to_bottles", true);
        applyToResults = getConfig().getBoolean("stack.apply_to_results", true);
        if (maxStack < 1) {
            maxStack = 1;
        }
        if (maxStack > BrewNmsHooks.ABSOLUTE_MAX_STACK) {
            getLogger().warning("stack.max_stack=" + maxStack + " clamped to "
                    + BrewNmsHooks.ABSOLUTE_MAX_STACK + " (1.12 NBT Count is a signed byte).");
            maxStack = BrewNmsHooks.ABSOLUTE_MAX_STACK;
        }
        List<Map<?, ?>> list = getConfig().getMapList("recipes");
        if (list == null || list.isEmpty()) {
            getLogger().severe("config.yml recipes list is empty!");
            return;
        }
        for (Map<?, ?> map : list) {
            Object ing = map.get("ingredient_ni_id");
            Object bottle = map.get("input_bottle_ni_id");
            Object result = map.get("result_ni_id");
            if (ing == null || bottle == null || result == null) {
                getLogger().warning("Skipping incomplete brew recipe: " + map);
                continue;
            }
            int time = 400;
            int amount = 1;
            if (map.get("brew_time_ticks") != null) {
                time = Integer.parseInt(String.valueOf(map.get("brew_time_ticks")));
            }
            if (map.get("result_amount") != null) {
                amount = Integer.parseInt(String.valueOf(map.get("result_amount")));
            }
            BrewEntry entry = new BrewEntry(String.valueOf(ing), String.valueOf(bottle),
                    String.valueOf(result), time, amount);
            recipes.add(entry);
            byKey.put(entry.ruleKey(), entry);
            ingredientIds.add(entry.ingredientNiId);
            bottleIds.add(entry.bottleNiId);
            resultIds.add(entry.resultNiId);
        }
        getLogger().info("Loaded " + recipes.size() + " brew recipe definitions (disable_vanilla="
                + disableVanilla + ", max_stack=" + maxStack + ")");
    }

    private void reloadAll() {
        reloadConfig();
        loadFromConfig();
        pushedHooks = pushHooks();
        getLogger().info("Pushed " + pushedHooks + " brew rule(s) into BrewNmsHooks (active="
                + (hooksAvailable && BrewNmsHooks.isActive()) + ", max_stack=" + maxStack + ").");
    }

    private int pushHooks() {
        if (!hooksAvailable) {
            return 0;
        }
        BrewNmsHooks.clearRules();
        BrewNmsHooks.setVanillaEnabled(!disableVanilla);
        BrewNmsHooks.configureStack(maxStack, applyToIngredients, applyToBottles, applyToResults);
        int n = 0;
        for (BrewEntry entry : recipes) {
            if (!ItemManager.INSTANCE.hasItem(entry.ingredientNiId)
                    || !ItemManager.INSTANCE.hasItem(entry.bottleNiId)
                    || !ItemManager.INSTANCE.hasItem(entry.resultNiId)) {
                getLogger().severe("Missing NI brew item(s) for " + entry.ruleKey() + " -> " + entry.resultNiId);
                continue;
            }
            BrewNmsHooks.setRule(entry.ruleKey(), entry.brewTimeTicks, entry.resultAmount, entry.resultNiId);
            n++;
            getLogger().info("Hook brew: " + entry.ingredientNiId + " + " + entry.bottleNiId
                    + " -> " + entry.resultNiId + " time=" + entry.brewTimeTicks
                    + " amount=" + entry.resultAmount);
        }

        final Map<String, BrewEntry> local = new HashMap<String, BrewEntry>(byKey);
        final Set<String> localIngredients = new HashSet<String>(ingredientIds);
        final Set<String> localBottles = new HashSet<String>(bottleIds);
        final Set<String> localResults = new HashSet<String>(resultIds);

        BrewNmsHooks.setResolver(new BrewNmsHooks.BrewResolver() {
            @Override
            public boolean isCustomIngredient(ItemStack ingredient) {
                ItemInfo info = ItemManager.INSTANCE.isNiItem(ingredient);
                return info != null && localIngredients.contains(info.getId());
            }

            @Override
            public boolean isCustomBottle(ItemStack bottle) {
                ItemInfo info = ItemManager.INSTANCE.isNiItem(bottle);
                return info != null && localBottles.contains(info.getId());
            }

            @Override
            public boolean isCustomResult(ItemStack result) {
                ItemInfo info = ItemManager.INSTANCE.isNiItem(result);
                return info != null && localResults.contains(info.getId());
            }

            @Override
            public BrewNmsHooks.BrewMatch resolve(ItemStack ingredient, ItemStack bottle) {
                ItemInfo ingInfo = ItemManager.INSTANCE.isNiItem(ingredient);
                ItemInfo botInfo = ItemManager.INSTANCE.isNiItem(bottle);
                if (ingInfo == null || botInfo == null) {
                    return null;
                }
                BrewEntry entry = local.get(ingInfo.getId() + "|" + botInfo.getId());
                if (entry == null) {
                    return null;
                }
                ItemStack result = ItemManager.INSTANCE.getItemStack(entry.resultNiId);
                if (result == null) {
                    return null;
                }
                result = result.clone();
                result.setAmount(Math.max(1, entry.resultAmount));
                return new BrewNmsHooks.BrewMatch(result, entry.brewTimeTicks, entry.resultAmount);
            }
        });
        getLogger().info("Stack limits: max_stack=" + BrewNmsHooks.getDefaultMaxStack()
                + " ingredients=" + applyToIngredients
                + " bottles=" + applyToBottles
                + " results=" + applyToResults
                + " enabled=" + BrewNmsHooks.isStackLimitsEnabled());
        return n;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("corebrew")) {
            return false;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("check")) {
            sender.sendMessage("[CoreBrew] hooksAvailable=" + hooksAvailable
                    + " pushed=" + pushedHooks
                    + " disable_vanilla=" + disableVanilla
                    + " max_stack=" + maxStack);
            if (hooksAvailable) {
                sender.sendMessage("[CoreBrew] BrewNmsHooks.active=" + BrewNmsHooks.isActive()
                        + " ruleCount=" + BrewNmsHooks.ruleCount()
                        + " vanillaEnabled=" + BrewNmsHooks.isVanillaEnabled()
                        + " stackLimits=" + BrewNmsHooks.isStackLimitsEnabled()
                        + " defaultMaxStack=" + BrewNmsHooks.getDefaultMaxStack());
            }
            for (BrewEntry e : recipes) {
                boolean ok = ItemManager.INSTANCE.hasItem(e.ingredientNiId)
                        && ItemManager.INSTANCE.hasItem(e.bottleNiId)
                        && ItemManager.INSTANCE.hasItem(e.resultNiId);
                sender.sendMessage("[CoreBrew] " + e.ingredientNiId + " + " + e.bottleNiId
                        + " -> " + e.resultNiId + (ok ? " OK" : " MISSING")
                        + " time=" + e.brewTimeTicks + " amount=" + e.resultAmount);
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            reloadAll();
            sender.sendMessage("[CoreBrew] reloaded YAML + re-pushed BrewNmsHooks (" + pushedHooks
                    + " rules, max_stack=" + maxStack + ")");
            return true;
        }
        sender.sendMessage("/corebrew check|reload");
        return true;
    }
}
