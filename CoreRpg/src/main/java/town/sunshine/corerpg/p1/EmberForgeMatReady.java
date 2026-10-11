package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

import java.util.List;

/**
 * D478: rising-edge forge enhance mat-ready cue (acquisition loop).
 * Zero enhance rates / costs / AFK change.
 */
public final class EmberForgeMatReady {

    /** Latched next enhance target (1..MAX) that we already announced; 0 = clear. */
    public static final String C_READY = "p1_forge_mat_ready";
    /** D3977: quiet held tip latch (no ConfirmTokens); same target semantics as C_READY. */
    public static final String C_QUIET = "p1_forge_mat_quiet";

    private EmberForgeMatReady() {}

    public static String actionBar(int from, int to) {
        return "§a工坊材料就绪 · §f强化 +" + from + "→+" + to;
    }

    public static String chatLine(String label, int from, int to) {
        return "§a强化材料够了：§f" + label + " §7· +" + from + "→+" + to + " §a· 可去工坊";
    }

    /** Pure: whether latch should fire for this target given prior latch. */
    public static boolean risingEdge(int latched, int target, boolean canAfford) {
        if (!canAfford) return false;
        if (target < 1) return false;
        return latched != target;
    }

    public static void clearLatch(PlayerData d) {
        if (d == null) return;
        int cur = d.periodCount(C_READY, "all");
        if (cur != 0) d.addPeriodCount(C_READY, "all", -cur);
        clearQuietLatch(d);
    }

    public static void clearQuietLatch(PlayerData d) {
        if (d == null) return;
        int cur = d.periodCount(C_QUIET, "all");
        if (cur != 0) d.addPeriodCount(C_QUIET, "all", -cur);
    }

    public static void setQuietLatch(PlayerData d, int target) {
        if (d == null) return;
        int cur = d.periodCount(C_QUIET, "all");
        d.addPeriodCount(C_QUIET, "all", target - cur);
    }

    public static void setLatch(PlayerData d, int target) {
        if (d == null) return;
        int cur = d.periodCount(C_READY, "all");
        d.addPeriodCount(C_READY, "all", target - cur);
    }

    /**
     * Rising-edge cue when main-hand forgeable P1 piece (T1+) can pay next enhance.
     * @return true if a cue was shown
     */
    public static boolean maybeReadyCue(Player p) {
        if (p == null || !p.isOnline()) return false;
        CoreRpgPlugin pl = plugin();
        if (pl == null) return false;
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null || d.isLoadFailed()) return false;
        EmberForgeService forge = pl.getEmberForge();
        EmberLoadoutService ls = pl.getEmberLoadouts();
        if (forge == null || ls == null) return false;

        ItemStack main = p.getInventory().getItemInMainHand();
        if (main == null || !ls.items().hasData(main)) {
            clearLatch(d);
            flush(p);
            return false;
        }
        EmberItems.Read r = ls.items().read(main);
        if (r == null || !r.ok()) {
            clearLatch(d);
            flush(p);
            return false;
        }
        EmberItemData data = r.data;
        // T0 starter: forge itself warns not to invest — skip cue
        if (data.tier < 1) {
            clearLatch(d);
            flush(p);
            return false;
        }
        EmberUpgradeRules.Plan plan = EmberUpgradeRules.enhanceCheck(data);
        int latched = d.periodCount(C_READY, "all");
        if (!plan.ok() || plan.cost == null) {
            clearLatch(d);
            flush(p);
            return false;
        }
        List<String> lack = forge.lackingFor(p, plan.cost);
        boolean can = lack == null || lack.isEmpty();
        int target = data.enhance + 1;
        if (!can) {
            clearLatch(d);
            flush(p);
            return false;
        }
        if (!risingEdge(latched, target, true)) return false;
        setLatch(d, target);
        flush(p);

        String P = EmberRunService.P;
        String label = data.shortLabel();
        String line = chatLine(label, data.enhance, target);
        p.sendMessage(P + line);
        try { p.sendActionBar(actionBar(data.enhance, target)); } catch (Throwable ignored) { }
        ConfirmTokens.sendButtons(p, P + "§7下一步：", // D857/D877
                new String[]{"[强化预览]", "/corerpg p1 enhance", "看成功率/保底再确认", "GREEN"},
                new String[]{"[成套追]", "/corerpg p1 setchase", "换装再强化", "AQUA"},
                new String[]{"[去工坊]", "/corerpg p1 forge", "打开工坊页", "GOLD"});
        return true;
    }


    /**
     * D3977: same afford gate as {@link #maybeReadyCue} but chat+AB only (no ConfirmTokens).
     * For ItemHeld / hub convenience.
     */
    public static boolean maybeQuietReadyTip(Player p) {
        if (p == null || !p.isOnline()) return false;
        if (EmberMode.isP1World(p.getWorld())) return false; // hub/town only
        CoreRpgPlugin pl = plugin();
        if (pl == null) return false;
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null || d.isLoadFailed()) return false;
        EmberForgeService forge = pl.getEmberForge();
        EmberLoadoutService ls = pl.getEmberLoadouts();
        if (forge == null || ls == null) return false;
        ItemStack main = p.getInventory().getItemInMainHand();
        if (main == null || !ls.items().hasData(main)) {
            clearQuietLatch(d);
            flush(p);
            return false;
        }
        EmberItems.Read r = ls.items().read(main);
        if (r == null || !r.ok() || r.data == null || r.data.tier < 1) {
            clearQuietLatch(d);
            flush(p);
            return false;
        }
        EmberItemData data = r.data;
        EmberUpgradeRules.Plan plan = EmberUpgradeRules.enhanceCheck(data);
        if (!plan.ok() || plan.cost == null) {
            clearQuietLatch(d);
            flush(p);
            return false;
        }
        List<String> lack = forge.lackingFor(p, plan.cost);
        boolean can = lack == null || lack.isEmpty();
        int target = data.enhance + 1;
        int latched = d.periodCount(C_QUIET, "all");
        if (!can) {
            clearQuietLatch(d);
            flush(p);
            return false;
        }
        if (!risingEdge(latched, target, true)) return false;
        setQuietLatch(d, target);
        flush(p);
        String P = EmberRunService.P;
        p.sendMessage(P + chatLine(data.shortLabel(), data.enhance, target)
                + " §8· /corerpg p1 enhance");
        try { p.sendActionBar(actionBar(data.enhance, target)); } catch (Throwable ignored) { }
        return true;
    }

    /** After enhance commit: drop latch so a later +N can re-fire when mats refill. */
    public static void afterEnhance(Player p) {
        if (p == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null) return;
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null) return;
        clearLatch(d);
        flush(p);
    }

    /** Admin smoke: force ActionBar/chat without afford gate (optional held label). */
    public static void adminSample(Player p, int from, int to) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + chatLine("样例件", from, to));
        try { p.sendActionBar(actionBar(from, to)); } catch (Throwable ignored) { }
        ConfirmTokens.sendButton(p, P + "§7下一步：",
                "[去工坊强化]", "/corerpg p1 forge", "打开工坊");
    }

    private static void flush(Player p) {
        CoreRpgPlugin pl = plugin();
        if (pl != null && p != null) pl.getDataStore().flushMutation(p.getUniqueId());
    }

    private static CoreRpgPlugin plugin() {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }
}
