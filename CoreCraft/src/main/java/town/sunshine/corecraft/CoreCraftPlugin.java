package town.sunshine.corecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import pers.coresystem.paper.CraftNmsHooks;
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
 * Wipes most vanilla crafting recipes (keeps infrastructure whitelist + all furnace recipes),
 * registers NI→NI recipes from config.yml, and gates crafts via Prepare/Craft events.
 */
public final class CoreCraftPlugin extends JavaPlugin implements Listener {

    private static final class CraftEntry {
        final String id;
        final String type;
        final List<String> shape;
        final Map<Character, String> shapedIngredients;
        final List<String> shapelessIngredients;
        final String resultNiId;
        final int resultAmount;

        CraftEntry(String id, String type, List<String> shape, Map<Character, String> shapedIngredients,
                   List<String> shapelessIngredients, String resultNiId, int resultAmount) {
            this.id = id;
            this.type = type;
            this.shape = shape;
            this.shapedIngredients = shapedIngredients;
            this.shapelessIngredients = shapelessIngredients;
            this.resultNiId = resultNiId;
            this.resultAmount = resultAmount;
        }
    }

    private final List<CraftEntry> entries = new ArrayList<CraftEntry>();
    private final Set<Material> whitelistResults = new HashSet<Material>();
    /** result Material -> allowed NI result ids for custom recipes */
    private final Map<Material, Set<String>> customResultsByMaterial = new HashMap<Material, Set<String>>();
    private final Map<String, CraftEntry> byResultId = new HashMap<String, CraftEntry>();

    private int removedCrafting;
    private int keptWhitelist;
    private int keptFurnace;
    private int registeredNi;
    private boolean wipeVanilla = true;
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
        getLogger().info("CoreCraft scheduled (CraftNmsHooks available=" + hooksAvailable + ").");
    }

    private boolean probeHooks() {
        try {
            Class.forName("pers.coresystem.paper.CraftNmsHooks");
            return true;
        } catch (ClassNotFoundException ex) {
            getLogger().warning("CraftNmsHooks not found — stats only via plugin; run paper-custom.jar.");
            return false;
        }
    }

    private void loadFromConfig() {
        entries.clear();
        byResultId.clear();
        whitelistResults.clear();
        wipeVanilla = getConfig().getBoolean("wipe_vanilla", true);
        List<?> wl = getConfig().getList("infrastructure_whitelist_results");
        if (wl != null) {
            for (Object o : wl) {
                if (o == null) {
                    continue;
                }
                try {
                    whitelistResults.add(Material.valueOf(String.valueOf(o).toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    getLogger().warning("Unknown whitelist material: " + o);
                }
            }
        }
        List<Map<?, ?>> list = getConfig().getMapList("recipes");
        if (list == null) {
            return;
        }
        for (Map<?, ?> map : list) {
            Object idObj = map.get("id");
            Object typeObj = map.get("type");
            Object resultObj = map.get("result_ni_id");
            if (idObj == null || typeObj == null || resultObj == null) {
                getLogger().warning("Skipping recipe missing id/type/result_ni_id: " + map);
                continue;
            }
            String id = String.valueOf(idObj);
            String type = String.valueOf(typeObj).toLowerCase();
            String resultNi = String.valueOf(resultObj);
            int amount = 1;
            if (map.get("result_amount") != null) {
                amount = Integer.parseInt(String.valueOf(map.get("result_amount")));
            }
            List<String> shape = new ArrayList<String>();
            Map<Character, String> shapedIng = new HashMap<Character, String>();
            List<String> shapelessIng = new ArrayList<String>();
            if ("shaped".equals(type)) {
                Object shapeObj = map.get("shape");
                if (shapeObj instanceof List) {
                    for (Object row : (List<?>) shapeObj) {
                        shape.add(String.valueOf(row));
                    }
                }
                Object ingObj = map.get("ingredients");
                if (ingObj instanceof Map) {
                    Map<?, ?> ingMap = (Map<?, ?>) ingObj;
                    for (Map.Entry<?, ?> e : ingMap.entrySet()) {
                        String key = String.valueOf(e.getKey());
                        if (key.isEmpty()) {
                            continue;
                        }
                        shapedIng.put(key.charAt(0), String.valueOf(e.getValue()));
                    }
                }
            } else if ("shapeless".equals(type)) {
                Object ingObj = map.get("ingredients");
                if (ingObj instanceof List) {
                    for (Object o : (List<?>) ingObj) {
                        shapelessIng.add(String.valueOf(o));
                    }
                }
            } else {
                getLogger().warning("Unknown recipe type: " + type + " for " + id);
                continue;
            }
            CraftEntry entry = new CraftEntry(id, type, shape, shapedIng, shapelessIng, resultNi, amount);
            entries.add(entry);
            byResultId.put(resultNi, entry);
        }
        getLogger().info("Loaded " + entries.size() + " craft recipe definition(s); whitelist=" + whitelistResults);
    }

    private void wipeAndRegister() {
        reloadConfig();
        loadFromConfig();
        removedCrafting = 0;
        keptWhitelist = 0;
        keptFurnace = 0;
        if (wipeVanilla) {
            wipeCraftingKeepInfraAndFurnace();
        } else {
            getLogger().info("wipe_vanilla=false — skipped recipe wipe.");
        }
        registeredNi = registerNiRecipes();
        pushHooks();
        getLogger().info("Craft wipe removed=" + removedCrafting
                + " keptWhitelist=" + keptWhitelist
                + " keptFurnace=" + keptFurnace
                + " NI registered=" + registeredNi);
    }

    private void wipeCraftingKeepInfraAndFurnace() {
        List<Recipe> keep = new ArrayList<Recipe>();
        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            Recipe recipe = it.next();
            if (recipe instanceof FurnaceRecipe) {
                keep.add(recipe);
                keptFurnace++;
                continue;
            }
            ItemStack result = recipe.getResult();
            if (result != null && whitelistResults.contains(result.getType())) {
                keep.add(recipe);
                keptWhitelist++;
                continue;
            }
            removedCrafting++;
        }
        Bukkit.clearRecipes();
        for (Recipe recipe : keep) {
            try {
                Bukkit.addRecipe(recipe);
            } catch (Exception ex) {
                getLogger().log(Level.WARNING, "Failed to re-add kept recipe: " + recipe, ex);
            }
        }
    }

    private int registerNiRecipes() {
        customResultsByMaterial.clear();
        int count = 0;
        for (CraftEntry entry : entries) {
            if (!ItemManager.INSTANCE.hasItem(entry.resultNiId)) {
                getLogger().severe("Missing NI result: " + entry.resultNiId);
                continue;
            }
            ItemStack output = ItemManager.INSTANCE.getItemStack(entry.resultNiId);
            if (output == null) {
                getLogger().severe("getItemStack null for " + entry.resultNiId);
                continue;
            }
            output = output.clone();
            output.setAmount(Math.max(1, entry.resultAmount));
            NamespacedKey key = new NamespacedKey(this, entry.id.toLowerCase().replace(' ', '_'));
            boolean ok = false;
            if ("shaped".equals(entry.type)) {
                ok = registerShaped(key, output, entry);
            } else {
                ok = registerShapeless(key, output, entry);
            }
            if (ok) {
                count++;
                Material mat = output.getType();
                Set<String> set = customResultsByMaterial.get(mat);
                if (set == null) {
                    set = new HashSet<String>();
                    customResultsByMaterial.put(mat, set);
                }
                set.add(entry.resultNiId);
                getLogger().info("Registered craft " + entry.type + ": " + entry.id + " -> " + entry.resultNiId
                        + " x" + entry.resultAmount);
            }
        }
        return count;
    }

    private boolean registerShaped(NamespacedKey key, ItemStack output, CraftEntry entry) {
        if (entry.shape.isEmpty()) {
            getLogger().warning("Empty shape for " + entry.id);
            return false;
        }
        ShapedRecipe recipe = new ShapedRecipe(key, output);
        recipe.shape(entry.shape.toArray(new String[0]));
        for (Map.Entry<Character, String> e : entry.shapedIngredients.entrySet()) {
            char c = e.getKey();
            if (c == ' ') {
                continue;
            }
            String niId = e.getValue();
            if (!ItemManager.INSTANCE.hasItem(niId)) {
                getLogger().severe("Missing NI ingredient " + niId + " for " + entry.id);
                return false;
            }
            ItemStack sample = ItemManager.INSTANCE.getItemStack(niId);
            if (sample == null) {
                return false;
            }
            recipe.setIngredient(c, sample.getType(), sample.getDurability());
        }
        return Bukkit.addRecipe(recipe);
    }

    private boolean registerShapeless(NamespacedKey key, ItemStack output, CraftEntry entry) {
        if (entry.shapelessIngredients.isEmpty()) {
            getLogger().warning("Empty ingredients for " + entry.id);
            return false;
        }
        ShapelessRecipe recipe = new ShapelessRecipe(key, output);
        for (String niId : entry.shapelessIngredients) {
            if (!ItemManager.INSTANCE.hasItem(niId)) {
                getLogger().severe("Missing NI ingredient " + niId + " for " + entry.id);
                return false;
            }
            ItemStack sample = ItemManager.INSTANCE.getItemStack(niId);
            if (sample == null) {
                return false;
            }
            recipe.addIngredient(sample.getType(), sample.getDurability());
        }
        return Bukkit.addRecipe(recipe);
    }

    private void pushHooks() {
        if (!hooksAvailable) {
            return;
        }
        CraftNmsHooks.setStats(wipeVanilla, removedCrafting, keptWhitelist, registeredNi);
    }

    /** Collect required NI ingredient ids for a craft entry (multiset as list). */
    private List<String> requiredIngredients(CraftEntry entry) {
        List<String> req = new ArrayList<String>();
        if ("shaped".equals(entry.type)) {
            for (String row : entry.shape) {
                for (int i = 0; i < row.length(); i++) {
                    char c = row.charAt(i);
                    if (c == ' ') {
                        continue;
                    }
                    String ni = entry.shapedIngredients.get(c);
                    if (ni != null) {
                        req.add(ni);
                    }
                }
            }
        } else {
            req.addAll(entry.shapelessIngredients);
        }
        return req;
    }

    private boolean matrixMatches(ItemStack[] matrix, CraftEntry entry) {
        List<String> required = requiredIngredients(entry);
        List<String> present = new ArrayList<String>();
        if (matrix != null) {
            for (ItemStack stack : matrix) {
                if (stack == null || stack.getType() == Material.AIR) {
                    continue;
                }
                ItemInfo info = ItemManager.INSTANCE.isNiItem(stack);
                if (info == null) {
                    return false;
                }
                present.add(info.getId());
            }
        }
        if (present.size() != required.size()) {
            return false;
        }
        List<String> need = new ArrayList<String>(required);
        for (String id : present) {
            if (!need.remove(id)) {
                return false;
            }
        }
        return need.isEmpty();
    }

    private CraftEntry findMatchingEntry(ItemStack result, ItemStack[] matrix) {
        if (result == null) {
            return null;
        }
        // Scan all entries so multiple recipes can share the same result_ni_id
        // (e.g. crystal_ember_enchant from mats vs from core). byResultId alone
        // would keep only the last definition.
        for (CraftEntry e : entries) {
            if (matrixMatches(matrix, e)) {
                return e;
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            return;
        }
        if (recipe instanceof FurnaceRecipe) {
            return;
        }
        ItemStack result = recipe.getResult();
        if (result == null) {
            return;
        }
        // Whitelisted vanilla infrastructure — allow as-is
        if (whitelistResults.contains(result.getType())) {
            ItemInfo info = ItemManager.INSTANCE.isNiItem(result);
            if (info == null) {
                return;
            }
            // NI result that happens to use whitelist material still needs gate below
        }
        ItemStack[] matrix = event.getInventory().getMatrix();
        CraftEntry match = findMatchingEntry(result, matrix);
        if (match != null) {
            ItemStack out = ItemManager.INSTANCE.getItemStack(match.resultNiId);
            if (out != null) {
                out = out.clone();
                out.setAmount(Math.max(1, match.resultAmount));
                event.getInventory().setResult(out);
            }
            return;
        }
        // Custom material path but wrong NI / vanilla of same mat → cancel result
        if (customResultsByMaterial.containsKey(result.getType())) {
            event.getInventory().setResult(null);
            return;
        }
        // If wipe is on, non-whitelist non-custom should not appear; belt-and-suspenders
        if (wipeVanilla && !whitelistResults.contains(result.getType())) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            return;
        }
        if (recipe instanceof FurnaceRecipe) {
            return;
        }
        ItemStack result = recipe.getResult();
        if (result != null && whitelistResults.contains(result.getType())) {
            ItemInfo info = ItemManager.INSTANCE.isNiItem(result);
            if (info == null) {
                return;
            }
        }
        ItemStack[] matrix = event.getInventory().getMatrix();
        CraftEntry match = findMatchingEntry(result, matrix);
        if (match != null) {
            ItemStack out = ItemManager.INSTANCE.getItemStack(match.resultNiId);
            if (out != null) {
                out = out.clone();
                out.setAmount(Math.max(1, match.resultAmount));
                event.setCurrentItem(out);
            }
            return;
        }
        if (wipeVanilla && (result == null || !whitelistResults.contains(result.getType()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("corecraft")) {
            return false;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("check")) {
            int crafting = 0;
            int furnace = 0;
            Iterator<Recipe> it = Bukkit.recipeIterator();
            while (it.hasNext()) {
                Recipe r = it.next();
                if (r instanceof FurnaceRecipe) {
                    furnace++;
                } else {
                    crafting++;
                }
            }
            sender.sendMessage("[CoreCraft] wipe_vanilla=" + wipeVanilla
                    + " removed=" + removedCrafting
                    + " keptWhitelist=" + keptWhitelist
                    + " keptFurnace=" + keptFurnace
                    + " NI_registered=" + registeredNi);
            sender.sendMessage("[CoreCraft] recipes now: crafting=" + crafting + " furnace=" + furnace
                    + " whitelist=" + whitelistResults);
            if (hooksAvailable) {
                sender.sendMessage("[CoreCraft] CraftNmsHooks.active=" + CraftNmsHooks.isActive()
                        + " removed=" + CraftNmsHooks.getRemovedCount()
                        + " kept=" + CraftNmsHooks.getKeptWhitelist()
                        + " registered=" + CraftNmsHooks.getRegisteredCustom());
            }
            for (CraftEntry e : entries) {
                boolean ok = ItemManager.INSTANCE.hasItem(e.resultNiId);
                for (String ing : requiredIngredients(e)) {
                    ok = ok && ItemManager.INSTANCE.hasItem(ing);
                }
                sender.sendMessage("[CoreCraft] " + e.id + " (" + e.type + ") -> " + e.resultNiId
                        + (ok ? " OK" : " MISSING"));
            }
            sender.sendMessage("[CoreCraft] give demos: /ni give <player> ingot_ember_iron 16");
            sender.sendMessage("[CoreCraft]   then craft plate_ember_iron / rod_ember_iron / core_ember_compact");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            wipeAndRegister();
            sender.sendMessage("[CoreCraft] reloaded — NI_registered=" + registeredNi
                    + " removed=" + removedCrafting);
            return true;
        }
        sender.sendMessage("/corecraft check|reload");
        return true;
    }
}
