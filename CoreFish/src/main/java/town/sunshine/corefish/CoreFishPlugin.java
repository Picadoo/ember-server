package town.sunshine.corefish;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pers.coresystem.paper.FishNmsHooks;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

/**
 * Pushes NI fishing loot tables and optional wait ticks into FishNmsHooks.
 */
public final class CoreFishPlugin extends JavaPlugin {

    private static final class Weighted {
        final String niId;
        final int weight;

        Weighted(String niId, int weight) {
            this.niId = niId;
            this.weight = weight;
        }
    }

    private final Map<String, List<Weighted>> tables = new HashMap<String, List<Weighted>>();
    private final Map<String, Integer> categoryWeights = new HashMap<String, Integer>();
    private final Random random = new Random();

    private boolean overrideLoot = true;
    private int waitMin = -1;
    private int waitMax = -1;
    private int entryCount;
    private boolean hooksAvailable;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        hooksAvailable = probeHooks();
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                pushHooks();
            }
        });
        getLogger().info("CoreFish scheduled (FishNmsHooks available=" + hooksAvailable + ").");
    }

    private boolean probeHooks() {
        try {
            Class.forName("pers.coresystem.paper.FishNmsHooks");
            return true;
        } catch (ClassNotFoundException ex) {
            getLogger().severe("FishNmsHooks not found — run custom Paper jar (paper-custom.jar).");
            return false;
        }
    }

    private void loadFromConfig() {
        tables.clear();
        categoryWeights.clear();
        entryCount = 0;
        overrideLoot = getConfig().getBoolean("override_vanilla_loot", true);
        waitMin = getConfig().getInt("wait_ticks_min", -1);
        waitMax = getConfig().getInt("wait_ticks_max", -1);
        if (getConfig().isConfigurationSection("category_weights")) {
            for (String key : getConfig().getConfigurationSection("category_weights").getKeys(false)) {
                categoryWeights.put(key, getConfig().getInt("category_weights." + key, 0));
            }
        }
        if (categoryWeights.isEmpty()) {
            categoryWeights.put("fish", 80);
            categoryWeights.put("treasure", 10);
            categoryWeights.put("junk", 10);
        }
        if (getConfig().isConfigurationSection("tables")) {
            for (String cat : getConfig().getConfigurationSection("tables").getKeys(false)) {
                List<Weighted> list = new ArrayList<Weighted>();
                List<Map<?, ?>> rows = getConfig().getMapList("tables." + cat);
                if (rows != null) {
                    for (Map<?, ?> row : rows) {
                        Object idObj = row.get("ni_id");
                        if (idObj == null) {
                            continue;
                        }
                        int w = 1;
                        if (row.get("weight") != null) {
                            w = Integer.parseInt(String.valueOf(row.get("weight")));
                        }
                        if (w <= 0) {
                            continue;
                        }
                        list.add(new Weighted(String.valueOf(idObj), w));
                        entryCount++;
                    }
                }
                tables.put(cat, list);
            }
        }
        getLogger().info("Loaded fishing tables entries=" + entryCount + " categories=" + tables.keySet());
    }

    private void pushHooks() {
        reloadConfig();
        loadFromConfig();
        if (!hooksAvailable) {
            return;
        }
        FishNmsHooks.setOverrideLoot(overrideLoot);
        FishNmsHooks.setWaitTicks(waitMin, waitMax);
        FishNmsHooks.setTableEntryCount(entryCount);
        final Map<String, List<Weighted>> localTables = new HashMap<String, List<Weighted>>();
        for (Map.Entry<String, List<Weighted>> e : tables.entrySet()) {
            localTables.put(e.getKey(), new ArrayList<Weighted>(e.getValue()));
        }
        final Map<String, Integer> localCats = new HashMap<String, Integer>(categoryWeights);
        FishNmsHooks.setLootProvider(new FishNmsHooks.LootProvider() {
            @Override
            public ItemStack rollLoot(Player player, float luck) {
                String cat = pickCategory(localCats);
                List<Weighted> list = localTables.get(cat);
                if (list == null || list.isEmpty()) {
                    // fallback any non-empty table
                    for (List<Weighted> cand : localTables.values()) {
                        if (cand != null && !cand.isEmpty()) {
                            list = cand;
                            break;
                        }
                    }
                }
                if (list == null || list.isEmpty()) {
                    return null;
                }
                String niId = pickWeighted(list);
                if (niId == null || !ItemManager.INSTANCE.hasItem(niId)) {
                    getLogger().log(Level.WARNING, "Fishing roll missing NI item: " + niId);
                    return null;
                }
                ItemStack stack = ItemManager.INSTANCE.getItemStack(niId);
                return stack == null ? null : stack.clone();
            }
        });
        getLogger().info("Pushed FishNmsHooks overrideLoot=" + overrideLoot
                + " wait=" + waitMin + "-" + waitMax
                + " entries=" + entryCount
                + " active=" + FishNmsHooks.isActive());
    }

    private String pickCategory(Map<String, Integer> cats) {
        int total = 0;
        for (Integer w : cats.values()) {
            if (w != null && w > 0) {
                total += w;
            }
        }
        if (total <= 0) {
            return "fish";
        }
        int roll = random.nextInt(total);
        int acc = 0;
        for (Map.Entry<String, Integer> e : cats.entrySet()) {
            int w = e.getValue() == null ? 0 : e.getValue();
            if (w <= 0) {
                continue;
            }
            acc += w;
            if (roll < acc) {
                return e.getKey();
            }
        }
        return "fish";
    }

    private String pickWeighted(List<Weighted> list) {
        int total = 0;
        for (Weighted w : list) {
            total += w.weight;
        }
        if (total <= 0) {
            return null;
        }
        int roll = random.nextInt(total);
        int acc = 0;
        for (Weighted w : list) {
            acc += w.weight;
            if (roll < acc) {
                return w.niId;
            }
        }
        return list.get(list.size() - 1).niId;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("corefish")) {
            return false;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("check")) {
            sender.sendMessage("[CoreFish] override_loot=" + overrideLoot
                    + " wait=" + waitMin + "-" + waitMax
                    + " entries=" + entryCount
                    + " hooksAvailable=" + hooksAvailable);
            if (hooksAvailable) {
                sender.sendMessage("[CoreFish] FishNmsHooks.active=" + FishNmsHooks.isActive()
                        + " overrideLoot=" + FishNmsHooks.isOverrideLoot()
                        + " hasWaitOverride=" + FishNmsHooks.hasWaitOverride()
                        + " tableEntryCount=" + FishNmsHooks.getTableEntryCount());
            }
            for (Map.Entry<String, List<Weighted>> e : tables.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[CoreFish] ").append(e.getKey()).append(" w=")
                        .append(categoryWeights.get(e.getKey())).append(" : ");
                for (Weighted w : e.getValue()) {
                    boolean ok = ItemManager.INSTANCE.hasItem(w.niId);
                    sb.append(w.niId).append("(").append(w.weight).append(ok ? " OK" : " MISSING").append(") ");
                }
                sender.sendMessage(sb.toString());
            }
            sender.sendMessage("[CoreFish] give: /ni give <player> fish_ember_cod 1");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            pushHooks();
            sender.sendMessage("[CoreFish] reloaded — entries=" + entryCount);
            return true;
        }
        sender.sendMessage("/corefish check|reload");
        return true;
    }
}
