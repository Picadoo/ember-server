package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D4097: enhance unlocks a new socket hole → auto-seat preferred gem from bag.
 * Real sock write; zero gem flat / craft price / STAMP_RATE / skill_mult / ConfirmTokens.
 */
public final class EmberGemHoleSeat {

    private EmberGemHoleSeat() {}

    /** Bukkit-free: first hole newly unlocked on after vs before, empty. -1 if none. */
    public static int newEmptyHole(EmberItemData before, EmberItemData after) {
        if (before == null || after == null) return -1;
        if (after.enhance <= before.enhance) return -1;
        int max = after.sockMax();
        for (int i = 0; i < max; i++) {
            if (after.sockUnlocked(i) && !before.sockUnlocked(i) && after.sockAt(i) == 0)
                return i;
        }
        return -1;
    }

    /**
     * After enhance win: if new hole unlocked and bag has path gem, seat it on main-hand piece.
     * @return true when a gem was seated
     */
    public static boolean maybeAutoSeat(Player p, PlayerData d, List<TxnItem> list) {
        if (p == null || list == null) return false;
        CoreRpgPlugin pl = plugin();
        if (pl == null) return false;
        EmberLoadoutService lo = pl.getEmberLoadouts();
        if (lo == null) return false;
        NiBridge ni = pl.getNiBridge();
        if (ni == null) return false;

        for (TxnItem t : list) {
            if (t == null || t.before == null || t.after == null) continue;
            int hole = newEmptyHole(t.before, t.after);
            if (hole < 0) continue;

            // must still hold the enhanced piece
            ItemStack hand = p.getInventory().getItemInMainHand();
            EmberItems.Read r = lo.items().read(hand);
            if (r == null || !r.ok() || r.data == null) return false;
            if (!t.after.uid.equals(r.data.uid)) return false;
            EmberItemData cur = r.data;
            if (!cur.sockUnlocked(hole) || cur.sockAt(hole) != 0) return false;

            int path = EmberGemPath.get(d);
            if (!EmberGemPath.valid(path)) return false; // need intentional gempath; else D731 chase
            String gemId = EmberGemPath.gemId(path);
            if (gemId == null || ni.countInInventory(p, gemId) <= 0) {
                // leave D731 chase to EnhanceLoop; no ConfirmTokens here
                return false;
            }
            if (!ni.consumeExact(p, gemId, 1)) return false;
            int s0 = hole == 0 ? path : cur.sock0;
            int s1 = hole == 1 ? path : cur.sock1;
            if (!EmberGemSocket.write(p, lo, cur, s0, s1)) {
                ni.giveNiItem(p, gemId, 1);
                return false;
            }
            String P = EmberRunService.P;
            p.sendMessage(P + "§a新孔自动镶嵌 · §f孔" + hole + " ← "
                    + EmberItemData.gemName(path)
                    + " §7· 强化养成闭环（不改宝石数值表）");
            try {
                p.sendActionBar("§a开孔入座 · §f" + EmberItemData.gemName(path) + " → 孔" + hole);
            } catch (Throwable ignored) { }
            EmberGemSeatLive.maybeAfterSeat(p, d, path, lo); // D4289
            return true;
        }
        return false;
    }


    /**
     * After gem craft/pity: if main-hand has empty unlocked hole and gemcode matches path (or any if path unset skip),
     * seat that just-granted gem. Caller already put gem in bag — we consume one.
     */
    public static boolean maybeAutoSeatOnAcquire(Player p, int gemCode, EmberLoadoutService loadouts, CoreRpgPlugin plugin) {
        if (p == null || plugin == null || loadouts == null || gemCode <= 0) return false;
        PlayerData d = plugin.getDataStore().get(p.getUniqueId());
        int path = EmberGemPath.get(d);
        if (!EmberGemPath.valid(path) || path != gemCode) return false;
        ItemStack hand = p.getInventory().getItemInMainHand();
        EmberItems.Read r = loadouts.items().read(hand);
        if (r == null || !r.ok() || r.data == null) return false;
        EmberItemData cur = r.data;
        int hole = EmberGemInsertChase.firstEmptyHole(cur);
        if (hole < 0) return false;
        NiBridge ni = plugin.getNiBridge();
        String gemId = EmberGemPath.gemId(gemCode);
        if (ni == null || gemId == null || ni.countInInventory(p, gemId) <= 0) return false;
        if (!ni.consumeExact(p, gemId, 1)) return false;
        int s0 = hole == 0 ? gemCode : cur.sock0;
        int s1 = hole == 1 ? gemCode : cur.sock1;
        if (!EmberGemSocket.write(p, loadouts, cur, s0, s1)) {
            ni.giveNiItem(p, gemId, 1);
            return false;
        }
        String P = EmberRunService.P;
        p.sendMessage(P + "§a宝石自动入座 · §f孔" + hole + " ← "
                + EmberItemData.gemName(gemCode)
                + " §7· 合成/保底养成闭环");
        try {
            p.sendActionBar("§a入座 · §f" + EmberItemData.gemName(gemCode) + " → 孔" + hole);
        } catch (Throwable ignored) { }
        EmberGemSeatLive.maybeAfterSeat(p, d, gemCode, loadouts); // D4289
        return true;
    }

    private static CoreRpgPlugin plugin() {
        org.bukkit.plugin.Plugin pl = org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }
}
