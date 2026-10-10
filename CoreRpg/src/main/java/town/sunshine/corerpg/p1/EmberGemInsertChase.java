package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.NiBridge;

/**
 * D727: gem insert chase from acquire — first empty unlocked hole on held gear.
 * Zero gem flat / craft price / legacy socket. Chat+ActionBar + at most one insert button.
 */
public final class EmberGemInsertChase {

    private EmberGemInsertChase() {}

    /** First empty unlocked hole, or -1. */
    public static int firstEmptyHole(EmberItemData d) {
        if (d == null) return -1;
        for (int i = 0; i < d.sockMax(); i++) {
            if (d.sockUnlocked(i) && d.sockAt(i) == 0) return i;
        }
        return -1;
    }

    public static void afterAcquire(Player p, int gemCode, EmberLoadoutService loadouts, CoreRpgPlugin plugin) {
        if (p == null || gemCode <= 0 || loadouts == null) return;
        ItemStack hand = p.getInventory().getItemInMainHand();
        EmberItems.Read r = loadouts.items().read(hand);
        EmberItemData d = r != null && r.ok() ? r.data : null;
        int hole = firstEmptyHole(d);
        String gname = EmberItemData.gemName(gemCode);
        String P = EmberRunService.P;
        if (hole < 0) {
            p.sendMessage(P + "§e镶嵌追：§f" + gname + " §7到手 · 手持有空孔的刃/符再嵌");
            try { p.sendActionBar("§e镶嵌追 · §f" + gname + " §7· 需空孔"); } catch (Throwable ignored) { }
            ConfirmTokens.sendButton(p, P + "§7下一步：", "[宝石 list]", "/corerpg p1 gem list", "看孔位");
            return;
        }
        String key = EmberGemPath.key(gemCode);
        p.sendMessage(P + "§e镶嵌追：§f" + gname + " §7→ 手持 §f孔" + hole + " §7空");
        try { p.sendActionBar("§e镶嵌追 · 孔" + hole + " · §f" + gname); } catch (Throwable ignored) { }
        ConfirmTokens.sendButton(p, P + "§7一键：",
                "[嵌孔" + hole + "]", "/corerpg p1 gem insert " + hole + " " + key,
                "手持件嵌 " + gname);
    }

    /** list: if held has empty hole and inv has matching path gem, one chase line. */
    public static void listHint(Player p, EmberItemData d, CoreRpgPlugin plugin) {
        if (p == null || d == null || plugin == null) return;
        int hole = firstEmptyHole(d);
        if (hole < 0) return;
        int path = EmberGemPath.get(plugin.getDataStore() == null ? null
                : plugin.getDataStore().get(p.getUniqueId()));
        if (!EmberGemPath.valid(path)) path = EmberGemPath.SHARP;
        String gemId = EmberGemPath.gemId(path);
        NiBridge ni = plugin.getNiBridge();
        int have = ni == null ? 0 : ni.countInInventory(p, gemId);
        if (have <= 0) return;
        p.sendMessage(EmberRunService.P + "§e可嵌：§f孔" + hole + " 空 · 背包 "
                + EmberItemData.gemName(path) + " ×" + have
                + " §7· /corerpg p1 gem insert " + hole + " " + EmberGemPath.key(path));
    }
}
