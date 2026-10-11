package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;

/**
 * Gem Stage2/3: P1 insert/remove/craft/pity on held blade/charm. Never legacy {@code /corerpg socket}.
 */
public final class EmberGemSocket {

    public static final String P = EmberRunService.P;

    private EmberGemSocket() {}

    public static boolean cmd(Player p, String[] args, EmberLoadoutService loadouts, CoreRpgPlugin plugin) {
        if (p == null || loadouts == null) return true;
        String sub = args.length >= 3 ? args[2].toLowerCase(java.util.Locale.ROOT) : "list";
        if ("list".equals(sub) || "ls".equals(sub) || "info".equals(sub)) return list(p, loadouts, plugin);
        if ("insert".equals(sub) || "in".equals(sub) || "镶".equals(sub)) return insert(p, args, loadouts, plugin);
        if ("remove".equals(sub) || "rm".equals(sub) || "拆".equals(sub)) return remove(p, args, loadouts, plugin);
        if ("craft".equals(sub) || "合成".equals(sub)) {
            String g = args.length >= 4 ? args[3] : "";
            return EmberGemAcquire.craft(p, g, loadouts, plugin);
        }
        if ("pity".equals(sub) || "希望".equals(sub)) {
            PlayerData d = plugin == null || plugin.getDataStore() == null ? null : plugin.getDataStore().get(p.getUniqueId());
            p.sendMessage(P + "宝石通关保底：§f" + EmberGemAcquire.remainLine(d)
                    + " §8· 合成：碎片×" + EmberGemAcquire.CRAFT_SHARD + "+骨尘×" + EmberGemAcquire.CRAFT_BONE
                    + "+币" + EmberGemAcquire.CRAFT_COIN);
            return true;
        }
        p.sendMessage(P + "用法：/corerpg p1 gem list|craft <石>|pity|insert|remove");
        return true;
    }

    static boolean list(Player p, EmberLoadoutService loadouts, CoreRpgPlugin plugin) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        EmberItems.Read r = loadouts.items().read(hand);
        if (r == null || !r.ok()) {
            p.sendMessage(P + "请手持已签名的余烬刃/护符（或 /corerpg p1 gem pity 看保底）");
            if (plugin != null && plugin.getDataStore() != null) {
                PlayerData d = plugin.getDataStore().get(p.getUniqueId());
                p.sendMessage(P + "§e宝石通关保底：§f" + EmberGemAcquire.remainLine(d));
            }
            return true;
        }
        EmberItemData d = r.data;
        String line = d.socketLoreLine();
        p.sendMessage(P + d.shortLabel() + " · " + (line == null ? "无孔" : line));
        p.sendMessage(P + "§7镶嵌：/corerpg p1 gem insert <孔> <sharp|steady|drain|gale> · 拆：remove <孔>");
        try { // D683 hole glance
            String h0 = d.sockUnlocked(0) ? (d.sockAt(0) == 0 ? "空" : EmberItemData.gemName(d.sockAt(0))) : "锁";
            String h1 = d.sockUnlocked(1) ? (d.sockAt(1) == 0 ? "空" : EmberItemData.gemName(d.sockAt(1))) : "锁";
            p.sendActionBar("§5孔0 §f" + h0 + " §8· §5孔1 §f" + h1);
        } catch (Throwable ignored) { }
        EmberGemInsertChase.listHint(p, d, plugin); // D727
        p.sendMessage(P + "§8锋利战斗：刃 T2+ 才计入 · 稳/汲/疾全程 · 不走旧 /corerpg socket");
        if (plugin != null && plugin.getDataStore() != null) {
            PlayerData pd = plugin.getDataStore().get(p.getUniqueId());
            p.sendMessage(P + "§e宝石通关保底：§f" + EmberGemAcquire.remainLine(pd));
            p.sendMessage(P + "§7合成：/corerpg p1 gem craft <sharp|steady|drain|gale> · 碎"
                    + EmberGemAcquire.CRAFT_SHARD + "+骨" + EmberGemAcquire.CRAFT_BONE + "+币" + EmberGemAcquire.CRAFT_COIN);
        }
        return true;
    }

    static boolean insert(Player p, String[] args, EmberLoadoutService loadouts, CoreRpgPlugin plugin) {
        if (args.length < 5) {
            p.sendMessage(P + "用法：/corerpg p1 gem insert <孔0|1> <sharp|steady|drain|gale>");
            return true;
        }
        int hole;
        try { hole = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
            p.sendMessage(P + "孔号须为 0 或 1"); return true;
        }
        int code = EmberGemPath.parse(args[4]);
        if (code <= 0) { p.sendMessage(P + "未知宝石：" + args[4]); return true; }
        ItemStack hand = p.getInventory().getItemInMainHand();
        EmberItems.Read r = loadouts.items().read(hand);
        if (r == null || !r.ok()) { p.sendMessage(P + "请手持已签名的余烬刃/护符"); return true; }
        EmberItemData d = r.data;
        if (!d.sockUnlocked(hole)) {
            p.sendMessage(P + "该孔未解锁（刃第二孔需强化≥5 · 护符孔需强化≥3）");
            try { p.sendActionBar("§c孔未解锁 · 先强化开孔"); } catch (Throwable ignored) { } // D1311
            ConfirmTokens.sendButtons(p, P + "§7去解锁：", // D871
                    new String[]{"[强化预览]", "/corerpg p1 enhance", "手持冲强化开孔", "GREEN"},
                    new String[]{"[工坊]", "/corerpg p1 forge", "养成页", "GOLD"});
            return true;
        }
        if (d.sockAt(hole) != 0) { p.sendMessage(P + "该孔已有石，先 remove"); return true; }
        String gemId = EmberGemPath.gemId(code);
        NiBridge ni = plugin.getNiBridge();
        if (ni == null || !ni.consumeExact(p, gemId, 1)) {
            int have = ni == null ? 0 : ni.countInInventory(p, gemId);
            p.sendMessage(P + "缺少 " + EmberItemData.gemName(code) + " §7（背包+仓 §f" + have + "§7）· 先合成或通关保底"); // D686
            try { p.sendActionBar("§c缺石 · §f" + EmberItemData.gemName(code) + " §7有" + have); } catch (Throwable ignored) { } // D1311
            ConfirmTokens.sendButton(p, P + "§7获取：", // D739
                    "[合成" + EmberItemData.gemName(code) + "]", "/corerpg p1 gem craft " + EmberGemPath.key(code),
                    "碎+骨+币合成 · 不走旧 socket");
            return true;
        }
        int s0 = hole == 0 ? code : d.sock0;
        int s1 = hole == 1 ? code : d.sock1;
        if (!write(p, loadouts, d, s0, s1)) {
            ni.giveNiItem(p, gemId, 1);
            p.sendMessage(P + "§c写入失败，已退回宝石");
            return true;
        }
        p.sendMessage(P + "已镶入 " + EmberItemData.gemName(code) + " → 孔" + hole);
        try { p.sendActionBar("§a镶嵌 · §f" + EmberItemData.gemName(code) + " → 孔" + hole); } catch (Throwable ignored) { }
        try {
            PlayerData pd = plugin.getDataStore().get(p.getUniqueId());
            EmberGemSeatLive.maybeAfterSeat(p, pd, code, loadouts); // D4289
            EmberGemInsertShard.maybeGift(p, pd, ni); // D8705
        } catch (Throwable ignored) { }
        // D2090 piece-aware commit next (empty hole / enhance / craft)
        EmberItems.Read afterR = loadouts.items().read(p.getInventory().getItemInMainHand());
        EmberItemData after = afterR != null && afterR.ok() ? afterR.data : null;
        EmberForgeCommitNext.afterGemInsert(p, after, code);
        return true;
    }

    static boolean remove(Player p, String[] args, EmberLoadoutService loadouts, CoreRpgPlugin plugin) {
        if (args.length < 4) {
            p.sendMessage(P + "用法：/corerpg p1 gem remove <孔0|1>");
            return true;
        }
        int hole;
        try { hole = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
            p.sendMessage(P + "孔号须为 0 或 1"); return true;
        }
        ItemStack hand = p.getInventory().getItemInMainHand();
        EmberItems.Read r = loadouts.items().read(hand);
        if (r == null || !r.ok()) { p.sendMessage(P + "请手持已签名的余烬刃/护符"); return true; }
        EmberItemData d = r.data;
        if (hole < 0 || hole >= d.sockMax()) { p.sendMessage(P + "无效孔"); return true; }
        int code = d.sockAt(hole);
        if (code == 0) { p.sendMessage(P + "该孔为空"); return true; }
        int s0 = hole == 0 ? 0 : d.sock0;
        int s1 = hole == 1 ? 0 : d.sock1;
        if (!write(p, loadouts, d, s0, s1)) {
            p.sendMessage(P + "§c写入失败");
            return true;
        }
        String gemId = EmberGemPath.gemId(code);
        NiBridge ni = plugin.getNiBridge();
        if (ni != null) ni.giveNiItem(p, gemId, 1);
        p.sendMessage(P + "已拆下 " + EmberItemData.gemName(code) + "（已返还）");
        try { p.sendActionBar("§e拆石 · §f孔" + hole + " 空"); } catch (Throwable ignored) { }
        // D862 re-insert chase
        ConfirmTokens.sendButtons(p, P + "§7可再嵌：",
                new String[]{"[嵌回孔" + hole + "]", "/corerpg p1 gem insert " + hole + " " + EmberGemPath.key(code),
                        "嵌回刚拆的石", "GREEN"},
                EmberForgeCommitNext.enhance(),
                new String[]{"[宝石列表]", "/corerpg p1 gem list", "换石", "AQUA"});
        return true;
    }

    static boolean write(Player p, EmberLoadoutService loadouts, EmberItemData before, int s0, int s1) {
        EmberItemData after = before.withSocketsNextRev(s0, s1);
        ItemStack fresh = loadouts.items().create(after);
        if (fresh == null) return false;
        loadouts.remember(after, p.getUniqueId());
        p.getInventory().setItemInMainHand(fresh);
        p.updateInventory();
        loadouts.refresh(p);
        return true;
    }
}
