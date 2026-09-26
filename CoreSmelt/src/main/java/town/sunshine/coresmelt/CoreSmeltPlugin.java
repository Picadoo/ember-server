package town.sunshine.coresmelt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import pers.coresystem.paper.FurnaceNmsHooks;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Removes every Bukkit FurnaceRecipe, registers NI-only smelt recipes from config.yml,
 * and pushes cook_time_ticks / result_amount into Paper FurnaceNmsHooks.
 */
public final class CoreSmeltPlugin extends JavaPlugin implements Listener {

    private static final class SmeltEntry {
        final String inputNiId;
        final String resultNiId;
        final int cookTimeTicks;
        final int resultAmount;
        final float exp;

        SmeltEntry(String inputNiId, String resultNiId, int cookTimeTicks, int resultAmount, float exp) {
            this.inputNiId = inputNiId;
            this.resultNiId = resultNiId;
            this.cookTimeTicks = cookTimeTicks;
            this.resultAmount = resultAmount;
            this.exp = exp;
        }
    }

    private final List<SmeltEntry> chain = new ArrayList<SmeltEntry>();
    private final Map<Material, Set<String>> allowedInputsByMaterial = new HashMap<Material, Set<String>>();
    private final Map<String, SmeltEntry> byInputId = new HashMap<String, SmeltEntry>();

    private int removedFurnaceRecipes;
    private int registeredNiRecipes;
    private int pushedHooks;
    private boolean hooksAvailable;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        hooksAvailable = probeHooks();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                wipeAndRegister();
            }
        });
        getLogger().info("CoreSmelt HYBRID scheduled (FurnaceNmsHooks available=" + hooksAvailable + ").");
    }

    private boolean probeHooks() {
        try {
            Class.forName("pers.coresystem.paper.FurnaceNmsHooks");
            return true;
        } catch (ClassNotFoundException ex) {
            getLogger().severe("FurnaceNmsHooks not found — run custom Paper jar (paper-custom.jar). cook_time/amount will NOT enforce.");
            return false;
        }
    }

    private void loadChainFromConfig() {
        chain.clear();
        byInputId.clear();
        List<Map<?, ?>> list = getConfig().getMapList("recipes");
        if (list == null || list.isEmpty()) {
            getLogger().severe("config.yml recipes list is empty!");
            return;
        }
        for (Map<?, ?> map : list) {
            Object inObj = map.get("input_ni_id");
            Object outObj = map.get("result_ni_id");
            if (inObj == null || outObj == null) {
                getLogger().warning("Skipping recipe missing input_ni_id/result_ni_id: " + map);
                continue;
            }
            String in = String.valueOf(inObj);
            String out = String.valueOf(outObj);
            int cook = 200;
            int amount = 1;
            float exp = 0.1f;
            if (map.get("cook_time_ticks") != null) {
                cook = Integer.parseInt(String.valueOf(map.get("cook_time_ticks")));
            }
            if (map.get("result_amount") != null) {
                amount = Integer.parseInt(String.valueOf(map.get("result_amount")));
            }
            if (map.get("exp") != null) {
                exp = Float.parseFloat(String.valueOf(map.get("exp")));
            }
            SmeltEntry entry = new SmeltEntry(in, out, cook, amount, exp);
            chain.add(entry);
            byInputId.put(in, entry);
        }
        getLogger().info("Loaded " + chain.size() + " recipe definitions from config.yml");
    }

    private void wipeAndRegister() {
        reloadConfig();
        loadChainFromConfig();
        removedFurnaceRecipes = wipeAllFurnaceRecipes();
        getLogger().info("Removed " + removedFurnaceRecipes + " vanilla/other FurnaceRecipe(s).");
        registeredNiRecipes = registerNiChain();
        getLogger().info("Registered " + registeredNiRecipes + " custom NI FurnaceRecipe(s).");
        pushedHooks = pushHooks();
        getLogger().info("Pushed " + pushedHooks + " rule(s) into FurnaceNmsHooks (active=" + (hooksAvailable && FurnaceNmsHooks.isActive()) + ").");
    }

    private int pushHooks() {
        if (!hooksAvailable) {
            return 0;
        }
        FurnaceNmsHooks.clearRules();
        int n = 0;
        for (SmeltEntry entry : chain) {
            FurnaceNmsHooks.setRule(entry.inputNiId, entry.cookTimeTicks, entry.resultAmount);
            n++;
            getLogger().info("Hook rule: " + entry.inputNiId + " cook=" + entry.cookTimeTicks
                    + " amount=" + entry.resultAmount + " -> " + entry.resultNiId);
        }
        final Map<String, SmeltEntry> local = new HashMap<String, SmeltEntry>(byInputId);
        FurnaceNmsHooks.setResolver(new FurnaceNmsHooks.RuleResolver() {
            @Override
            public FurnaceNmsHooks.Rule resolve(ItemStack bukkitInput) {
                if (bukkitInput == null) {
                    return null;
                }
                ItemInfo info = ItemManager.INSTANCE.isNiItem(bukkitInput);
                if (info == null) {
                    return null;
                }
                SmeltEntry entry = local.get(info.getId());
                if (entry == null) {
                    return FurnaceNmsHooks.getRule(info.getId());
                }
                return new FurnaceNmsHooks.Rule(entry.cookTimeTicks, entry.resultAmount);
            }
        });
        return n;
    }

    private int wipeAllFurnaceRecipes() {
        List<Recipe> keep = new ArrayList<Recipe>();
        int removed = 0;
        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            Recipe recipe = it.next();
            if (recipe instanceof FurnaceRecipe) {
                removed++;
            } else {
                keep.add(recipe);
            }
        }
        Bukkit.clearRecipes();
        for (Recipe recipe : keep) {
            try {
                Bukkit.addRecipe(recipe);
            } catch (Exception ex) {
                getLogger().log(Level.WARNING, "Failed to re-add non-furnace recipe: " + recipe, ex);
            }
        }
        return removed;
    }

    private int registerNiChain() {
        allowedInputsByMaterial.clear();
        int count = 0;
        for (SmeltEntry entry : chain) {
            if (!ItemManager.INSTANCE.hasItem(entry.inputNiId) || !ItemManager.INSTANCE.hasItem(entry.resultNiId)) {
                getLogger().severe("Missing NI item: " + entry.inputNiId + " -> " + entry.resultNiId);
                continue;
            }
            ItemStack inputSample = ItemManager.INSTANCE.getItemStack(entry.inputNiId);
            ItemStack output = ItemManager.INSTANCE.getItemStack(entry.resultNiId);
            if (inputSample == null || output == null) {
                getLogger().severe("getItemStack returned null for " + entry.inputNiId + " / " + entry.resultNiId);
                continue;
            }
            // Bake result_amount into the Bukkit recipe output as a soft hint; NMS hooks enforce at burn().
            output = output.clone();
            output.setAmount(Math.max(1, entry.resultAmount));
            Material inputMat = inputSample.getType();
            short data = inputSample.getDurability();
            FurnaceRecipe fr = new FurnaceRecipe(output, inputMat, data, entry.exp);
            if (Bukkit.addRecipe(fr)) {
                count++;
                Set<String> set = allowedInputsByMaterial.get(inputMat);
                if (set == null) {
                    set = new HashSet<String>();
                    allowedInputsByMaterial.put(inputMat, set);
                }
                set.add(entry.inputNiId);
                getLogger().info("Registered furnace: " + entry.inputNiId + " (" + inputMat + ":" + data
                        + ") -> " + entry.resultNiId + "x" + entry.resultAmount
                        + " cook=" + entry.cookTimeTicks + " exp=" + entry.exp);
            } else {
                getLogger().warning("addRecipe returned false for " + entry.inputNiId);
            }
        }
        return count;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        ItemStack source = event.getSource();
        if (source == null) {
            event.setCancelled(true);
            return;
        }
        Set<String> allowed = allowedInputsByMaterial.get(source.getType());
        if (allowed == null || allowed.isEmpty()) {
            event.setCancelled(true);
            return;
        }
        ItemInfo info = ItemManager.INSTANCE.isNiItem(source);
        if (info == null || !allowed.contains(info.getId())) {
            event.setCancelled(true);
            return;
        }
        SmeltEntry entry = byInputId.get(info.getId());
        if (entry != null) {
            ItemStack out = ItemManager.INSTANCE.getItemStack(entry.resultNiId);
            if (out != null) {
                out = out.clone();
                out.setAmount(Math.max(1, entry.resultAmount));
                event.setResult(out);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("coresmelt")) {
            return false;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("check")) {
            int furnaceCount = 0;
            Iterator<Recipe> it = Bukkit.recipeIterator();
            while (it.hasNext()) {
                if (it.next() instanceof FurnaceRecipe) {
                    furnaceCount++;
                }
            }
            sender.sendMessage("[CoreSmelt] furnace recipes now: " + furnaceCount);
            sender.sendMessage("[CoreSmelt] wiped: " + removedFurnaceRecipes
                    + ", NI registered: " + registeredNiRecipes
                    + ", hooks pushed: " + pushedHooks
                    + ", hooksAvailable=" + hooksAvailable);
            if (hooksAvailable) {
                sender.sendMessage("[CoreSmelt] FurnaceNmsHooks.active=" + FurnaceNmsHooks.isActive()
                        + " ruleCount=" + FurnaceNmsHooks.ruleCount());
            }
            for (SmeltEntry e : chain) {
                boolean ok = ItemManager.INSTANCE.hasItem(e.inputNiId)
                        && ItemManager.INSTANCE.hasItem(e.resultNiId);
                sender.sendMessage("[CoreSmelt] " + e.inputNiId + " -> " + e.resultNiId
                        + (ok ? " OK" : " MISSING") + " cook=" + e.cookTimeTicks
                        + " amount=" + e.resultAmount + " exp=" + e.exp);
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            wipeAndRegister();
            sender.sendMessage("[CoreSmelt] reloaded YAML + re-pushed FurnaceNmsHooks (" + pushedHooks + " rules)");
            return true;
        }
        sender.sendMessage("/coresmelt check|reload");
        return true;
    }
}
