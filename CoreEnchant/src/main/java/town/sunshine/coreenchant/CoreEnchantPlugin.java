package town.sunshine.coreenchant;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pers.coresystem.paper.EnchantNmsHooks;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Loads YAML enchant offer tables and pushes them into EnchantNmsHooks.
 * Registers an NI catalyst validator so Paper never depends on NeigeItems.
 */
public final class CoreEnchantPlugin extends JavaPlugin {

    private static final class SlotOffer {
        final int slot;
        final Enchantment enchantment;
        final int level;
        final int cost;
        final int lapisCost;

        SlotOffer(int slot, Enchantment enchantment, int level, int cost, int lapisCost) {
            this.slot = slot;
            this.enchantment = enchantment;
            this.level = level;
            this.cost = cost;
            this.lapisCost = lapisCost;
        }
    }

    private static final class OfferTable {
        final String id;
        final Set<Material> materials;
        final List<SlotOffer> offers;

        OfferTable(String id, Set<Material> materials, List<SlotOffer> offers) {
            this.id = id;
            this.materials = materials;
            this.offers = offers;
        }
    }

    private final List<OfferTable> tables = new ArrayList<OfferTable>();
    private final Set<String> whitelistNi = new HashSet<String>();
    private boolean overrideVanilla = true;
    private boolean requireNiItem = false;
    private boolean rejectVanillaLapis = true;
    private String catalystNiId = "crystal_ember_enchant";
    private int bookshelfCostBumpCap = 0;
    private boolean hooksAvailable;
    private int pushedTables;

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
        getLogger().info("CoreEnchant scheduled (EnchantNmsHooks available=" + hooksAvailable + ").");
    }

    @Override
    public void onDisable() {
        if (hooksAvailable) {
            EnchantNmsHooks.setProvider(null);
            EnchantNmsHooks.setCatalystValidator(null);
            EnchantNmsHooks.setOverrideVanilla(false);
            EnchantNmsHooks.setRejectVanillaLapis(false);
            EnchantNmsHooks.setCatalystNiId("");
            EnchantNmsHooks.setRegisteredTableCount(0);
        }
    }

    private boolean probeHooks() {
        try {
            Class.forName("pers.coresystem.paper.EnchantNmsHooks");
            return true;
        } catch (ClassNotFoundException ex) {
            getLogger().severe("EnchantNmsHooks not found — use custom Paper jar (paper-custom.jar).");
            return false;
        }
    }

    private void loadFromConfig() {
        tables.clear();
        whitelistNi.clear();
        overrideVanilla = getConfig().getBoolean("override_vanilla", true);
        requireNiItem = getConfig().getBoolean("require_ni_item", false);
        rejectVanillaLapis = getConfig().getBoolean("reject_vanilla_lapis", true);
        catalystNiId = getConfig().getString("catalyst_ni_id", "crystal_ember_enchant");
        if (catalystNiId == null) {
            catalystNiId = "";
        }
        bookshelfCostBumpCap = Math.max(0, getConfig().getInt("bookshelf_cost_bump_cap", 0));
        List<?> wl = getConfig().getList("whitelist_ni_ids");
        if (wl != null) {
            for (Object o : wl) {
                if (o != null) {
                    whitelistNi.add(String.valueOf(o));
                }
            }
        }
        List<Map<?, ?>> list = getConfig().getMapList("offer_tables");
        if (list == null || list.isEmpty()) {
            getLogger().severe("config.yml offer_tables is empty!");
            return;
        }
        for (Map<?, ?> map : list) {
            String id = map.get("id") != null ? String.valueOf(map.get("id")) : "unnamed";
            Set<Material> mats = new HashSet<Material>();
            Object matsObj = map.get("materials");
            if (matsObj instanceof List) {
                for (Object m : (List<?>) matsObj) {
                    if (m == null) {
                        continue;
                    }
                    Material mat = Material.matchMaterial(String.valueOf(m));
                    if (mat != null) {
                        mats.add(mat);
                    } else {
                        getLogger().warning("Unknown material in table " + id + ": " + m);
                    }
                }
            }
            List<SlotOffer> offers = new ArrayList<SlotOffer>();
            Object offersObj = map.get("offers");
            if (offersObj instanceof List) {
                for (Object rowObj : (List<?>) offersObj) {
                    if (!(rowObj instanceof Map)) {
                        continue;
                    }
                    Map<?, ?> row = (Map<?, ?>) rowObj;
                    int slot = row.get("slot") != null ? Integer.parseInt(String.valueOf(row.get("slot"))) : 0;
                    String enchName = row.get("enchantment") != null ? String.valueOf(row.get("enchantment")) : "";
                    Enchantment ench = Enchantment.getByName(enchName);
                    if (ench == null) {
                        getLogger().warning("Unknown enchantment '" + enchName + "' in table " + id);
                        continue;
                    }
                    int level = row.get("level") != null ? Integer.parseInt(String.valueOf(row.get("level"))) : 1;
                    int cost = row.get("cost") != null ? Integer.parseInt(String.valueOf(row.get("cost"))) : (slot + 1);
                    int lapis = row.get("lapis_cost") != null ? Integer.parseInt(String.valueOf(row.get("lapis_cost"))) : (slot + 1);
                    offers.add(new SlotOffer(slot, ench, level, cost, lapis));
                }
            }
            tables.add(new OfferTable(id, mats, offers));
            getLogger().info("Loaded offer table '" + id + "' with " + offers.size() + " slot offer(s).");
        }
    }

    private void reloadAll() {
        reloadConfig();
        loadFromConfig();
        pushedTables = pushHooks();
        getLogger().info("Pushed " + pushedTables + " offer table(s) into EnchantNmsHooks (override="
                + overrideVanilla + ", catalyst=" + catalystNiId
                + ", rejectVanillaLapis=" + rejectVanillaLapis
                + ", bookshelfBumpCap=" + bookshelfCostBumpCap
                + ", active=" + (hooksAvailable && EnchantNmsHooks.isActive()) + ").");
    }

    private int pushHooks() {
        if (!hooksAvailable) {
            return 0;
        }
        EnchantNmsHooks.setOverrideVanilla(overrideVanilla);
        EnchantNmsHooks.setRegisteredTableCount(tables.size());
        EnchantNmsHooks.setRejectVanillaLapis(rejectVanillaLapis);
        EnchantNmsHooks.setCatalystNiId(catalystNiId);

        final List<OfferTable> localTables = new ArrayList<OfferTable>(tables);
        final Set<String> localWhitelist = new HashSet<String>(whitelistNi);
        final boolean localRequireNi = requireNiItem;
        final String localCatalystId = catalystNiId;
        final boolean localRejectVanilla = rejectVanillaLapis;
        final int localBumpCap = bookshelfCostBumpCap;

        EnchantNmsHooks.setProvider(new EnchantNmsHooks.OfferProvider() {
            @Override
            public boolean canEnchantItem(ItemStack item) {
                if (item == null || item.getType() == Material.AIR) {
                    return false;
                }
                ItemInfo info = ItemManager.INSTANCE.isNiItem(item);
                if (!localWhitelist.isEmpty()) {
                    return info != null && localWhitelist.contains(info.getId());
                }
                if (localRequireNi) {
                    return info != null;
                }
                return true;
            }

            @Override
            public int lapisCostForSlot(int slot) {
                if (localTables.isEmpty()) {
                    return slot + 1;
                }
                OfferTable table = localTables.get(0);
                for (SlotOffer o : table.offers) {
                    if (o.slot == slot) {
                        return o.lapisCost;
                    }
                }
                return slot + 1;
            }

            @Override
            public EnchantNmsHooks.Offer[] buildOffers(ItemStack item, int bookshelves, int seed) {
                if (localTables.isEmpty()) {
                    return new EnchantNmsHooks.Offer[3];
                }
                List<OfferTable> matched = new ArrayList<OfferTable>();
                for (OfferTable t : localTables) {
                    if (t.materials.isEmpty() || t.materials.contains(item.getType())) {
                        matched.add(t);
                    }
                }
                if (matched.isEmpty()) {
                    return new EnchantNmsHooks.Offer[3];
                }
                OfferTable table = matched.get(Math.floorMod(seed, matched.size()));
                EnchantNmsHooks.Offer[] out = new EnchantNmsHooks.Offer[3];
                for (SlotOffer o : table.offers) {
                    if (o.slot < 0 || o.slot > 2) {
                        continue;
                    }
                    int bump = 0;
                    if (localBumpCap > 0) {
                        bump = Math.min(localBumpCap, bookshelves / 3);
                    }
                    int scaled = o.cost + bump;
                    out[o.slot] = new EnchantNmsHooks.Offer(o.enchantment.getId(), o.level, scaled, o.lapisCost);
                }
                return out;
            }
        });

        if (localCatalystId != null && !localCatalystId.isEmpty()) {
            EnchantNmsHooks.setCatalystValidator(new EnchantNmsHooks.CatalystValidator() {
                @Override
                public boolean isValid(ItemStack stack) {
                    if (stack == null || stack.getType() == Material.AIR) {
                        return false;
                    }
                    ItemInfo info = ItemManager.INSTANCE.isNiItem(stack);
                    if (info == null) {
                        // Vanilla lapis / any non-NI item rejected when catalyst id is set
                        return false;
                    }
                    return localCatalystId.equals(info.getId());
                }
            });
        } else if (localRejectVanilla) {
            // Reject everything in secondary slot (misconfig) — still register so vanilla lapis cannot be used
            EnchantNmsHooks.setCatalystValidator(new EnchantNmsHooks.CatalystValidator() {
                @Override
                public boolean isValid(ItemStack stack) {
                    return false;
                }
            });
        } else {
            EnchantNmsHooks.setCatalystValidator(null);
        }

        return tables.size();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("coreenchant")) {
            return false;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("check")) {
            sender.sendMessage("[CoreEnchant] hooksAvailable=" + hooksAvailable
                    + " override=" + overrideVanilla
                    + " tables=" + tables.size()
                    + " whitelist=" + whitelistNi.size()
                    + " require_ni=" + requireNiItem);
            sender.sendMessage("[CoreEnchant] catalyst_ni_id=" + catalystNiId
                    + " reject_vanilla_lapis=" + rejectVanillaLapis
                    + " bookshelf_cost_bump_cap=" + bookshelfCostBumpCap);
            if (hooksAvailable) {
                sender.sendMessage("[CoreEnchant] EnchantNmsHooks.active=" + EnchantNmsHooks.isActive()
                        + " overrideVanilla=" + EnchantNmsHooks.isOverrideVanilla()
                        + " registeredTables=" + EnchantNmsHooks.getRegisteredTableCount()
                        + " hasCatalystOverride=" + EnchantNmsHooks.hasCatalystOverride()
                        + " catalystNiId=" + EnchantNmsHooks.getCatalystNiId()
                        + " rejectVanillaLapis=" + EnchantNmsHooks.isRejectVanillaLapis());
            }
            for (OfferTable t : tables) {
                sender.sendMessage("[CoreEnchant] table=" + t.id + " offers=" + t.offers.size()
                        + " mats=" + (t.materials.isEmpty() ? "*" : t.materials.toString()));
                for (SlotOffer o : t.offers) {
                    sender.sendMessage("  slot " + o.slot + ": " + o.enchantment.getName()
                            + " " + o.level + " cost=" + o.cost + " lapis=" + o.lapisCost);
                }
            }
            sender.sendMessage("[CoreEnchant] give catalyst: /ni give <player> " + catalystNiId + " 16");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            reloadAll();
            sender.sendMessage("[CoreEnchant] reloaded YAML + re-pushed EnchantNmsHooks (" + pushedTables
                    + " tables, catalyst=" + catalystNiId + ")");
            return true;
        }
        sender.sendMessage("/coreenchant check|reload");
        return true;
    }
}
