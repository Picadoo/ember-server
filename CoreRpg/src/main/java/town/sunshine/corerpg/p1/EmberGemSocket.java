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
        if ("swap".equals(sub) || "对调".equals(sub) || "换孔".equals(sub)) return holeSwap(p, loadouts, plugin);
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
        p.sendMessage(P + "用法：/corerpg p1 gem list|craft <石>|pity|insert|remove|swap");
        return true;
    }

    static boolean list(Player p, EmberLoadoutService loadouts, CoreRpgPlugin plugin) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        EmberItems.Read r = loadouts.items().read(hand);
        if (r == null || !r.ok()) {
            p.sendMessage(P + "请手持已签名的余烬刃/护符（或 /corerpg p1 gem pity 看保底）");
            if (plugin != null && plugin.getDataStore() != null) {
                PlayerData d = plugin.getDataStore().get(p.getUniqueId());
                p.sendMessage(P + "§e宝石通关保底：§f" + EmberGemAcquire.remainLine(d)
                        + unlockSoftListTail(d)); // TIP-BC · ready peek · never unlocked()
            }
            return true;
        }
        EmberItemData d = r.data;
        String line = d.socketLoreLine();
        p.sendMessage(P + d.shortLabel() + " · " + (line == null ? "无孔" : line));
        p.sendMessage(P + "§7镶嵌：/corerpg p1 gem insert <孔> <sharp|steady|drain|gale> · 拆：remove <孔>");
        PlayerData pdList = plugin != null && plugin.getDataStore() != null
                ? plugin.getDataStore().get(p.getUniqueId()) : null;
        try { // D683 hole glance · TIP-BC optional soft-ok short mark (ready peek only)
            String h0 = d.sockUnlocked(0) ? (d.sockAt(0) == 0 ? "空" : EmberItemData.gemName(d.sockAt(0))) : "锁";
            String h1 = d.sockUnlocked(1) ? (d.sockAt(1) == 0 ? "空" : EmberItemData.gemName(d.sockAt(1))) : "锁";
            String bar = "§5孔0 §f" + h0 + " §8· §5孔1 §f" + h1;
            if (EmberGemHoleUnlockSoft.ready(pdList)
                    && (( !d.sockUnlocked(0) && EmberGemHoleUnlockSoft.softEligible(d, 0))
                    || ( !d.sockUnlocked(1) && EmberGemHoleUnlockSoft.softEligible(d, 1)))) {
                bar = bar + " §a· 可软开";
            }
            p.sendActionBar(bar);
        } catch (Throwable ignored) { }
        EmberGemInsertChase.listHint(p, d, plugin); // D727
        p.sendMessage(P + "§8锋利战斗：刃 T2+ 才计入 · 稳/汲/疾全程 · 不走旧 /corerpg socket");
        if (pdList != null) {
            p.sendMessage(P + "§e宝石通关保底：§f" + EmberGemAcquire.remainLine(pdList)
                    + unlockSoftListTail(pdList)); // TIP-BC · ready peek · never unlocked()
            p.sendMessage(P + "§7合成：/corerpg p1 gem craft <sharp|steady|drain|gale> · 碎"
                    + EmberGemAcquire.CRAFT_SHARD + "+骨" + EmberGemAcquire.CRAFT_BONE + "+币" + EmberGemAcquire.CRAFT_COIN);
        }
        return true;
    }

    static boolean insert(Player p, String[] args, EmberLoadoutService loadouts, CoreRpgPlugin plugin) {
        if (args.length < 4) {
            p.sendMessage(P + "用法：/corerpg p1 gem insert <孔0|1> [sharp|steady|drain|gale]");
            return true;
        }
        int hole;
        try { hole = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
            p.sendMessage(P + "孔号须为 0 或 1"); return true;
        }
        PlayerData pathPd = plugin != null && plugin.getDataStore() != null ? plugin.getDataStore().get(p.getUniqueId()) : null;
        int code = 0;
        if (args.length >= 5) {
            code = EmberGemPath.parse(args[4]);
            if (code <= 0) { p.sendMessage(P + "未知宝石：" + args[4]); return true; }
        } else {
            code = EmberGemInsertPathDefault.resolve(pathPd); // D12177
            if (code <= 0) {
                p.sendMessage(P + "用法：/corerpg p1 gem insert <孔0|1> <sharp|steady|drain|gale>（或先设宝石路径）");
                return true;
            }
            EmberGemInsertPathDefault.consume(pathPd);
            p.sendMessage(P + "§7路径默认镶嵌 · §f" + EmberGemPath.label(code) + " §8（今日一次）");
        }
        ItemStack hand = p.getInventory().getItemInMainHand();
        EmberItems.Read r = loadouts.items().read(hand);
        if (r == null || !r.ok()) { p.sendMessage(P + "请手持已签名的余烬刃/护符"); return true; }
        EmberItemData d = r.data;
        PlayerData holePd = plugin != null && plugin.getDataStore() != null ? plugin.getDataStore().get(p.getUniqueId()) : null;
        if (!d.sockUnlocked(hole) && !EmberGemHoleUnlockSoft.unlocked(d, hole, holePd)) {
            p.sendMessage(P + unlockRejectLine(holePd)); // TIP-BC · ready→soft / used→hard
            try { p.sendActionBar("§c孔未解锁 · 先强化开孔"); } catch (Throwable ignored) { } // D1311
            ConfirmTokens.sendButtons(p, P + "§7去解锁：", // D871
                    new String[]{"[强化预览]", "/corerpg p1 enhance", "手持冲强化开孔", "GREEN"},
                    new String[]{"[工坊]", "/corerpg p1 forge", "养成页", "GOLD"});
            return true;
        }
        int prevCode = d.sockAt(hole);
        PlayerData swapPd = holePd;
        boolean swap = false;
        if (prevCode != 0) {
            if (swapPd != null && EmberGemSwapSoft.ready(swapPd)) {
                swap = true;
            } else {
                p.sendMessage(P + "该孔已有石，先 remove"
                        + (EmberGemSwapSoft.usedToday(swapPd) ? " §8（今日换石已用）" : ""));
                return true;
            }
        }
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
        if (swap) {
            try {
                EmberGemSwapSoft.consume(swapPd);
                String oldId = EmberGemPath.gemId(prevCode);
                if (oldId != null) ni.giveNiItem(p, oldId, 1);
                p.sendMessage(P + "§a换石 · 卸下 §f" + EmberItemData.gemName(prevCode)
                        + " §a· 镶入 §f" + EmberItemData.gemName(code) + " §7→ 孔" + hole + " §a（今日一次）");
            } catch (Throwable ignored) {
                p.sendMessage(P + "已镶入 " + EmberItemData.gemName(code) + " → 孔" + hole);
            }
        } else {
            p.sendMessage(P + "已镶入 " + EmberItemData.gemName(code) + " → 孔" + hole);
        }
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

    /** D11969: sock0↔sock1 once/day. */
    static boolean holeSwap(Player p, EmberLoadoutService loadouts, CoreRpgPlugin plugin) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        EmberItems.Read r = loadouts.items().read(hand);
        if (r == null || !r.ok()) { p.sendMessage(P + "请手持已签名的余烬刃/护符"); return true; }
        EmberItemData d = r.data;
        PlayerData pd = plugin != null && plugin.getDataStore() != null ? plugin.getDataStore().get(p.getUniqueId()) : null;
        if (!EmberGemHoleSwap.canSwap(d)) {
            p.sendMessage(P + "§c双孔未齐或皆空，无法对调");
            return true;
        }
        if (!EmberGemHoleSwap.ready(pd)) {
            p.sendMessage(P + "§c今日双孔对调已用");
            return true;
        }
        int a = d.sockAt(0), b = d.sockAt(1);
        if (a == b) {
            p.sendMessage(P + "§7两孔相同，无需对调");
            return true;
        }
        if (!write(p, loadouts, d, b, a)) {
            p.sendMessage(P + "§c对调写入失败");
            return true;
        }
        EmberGemHoleSwap.consume(pd);
        p.sendMessage(P + "§a双孔对调 · §f孔0←" + EmberItemData.gemName(b)
                + " §7· §f孔1←" + EmberItemData.gemName(a) + " §a（今日一次）");
        try { p.sendActionBar("§a双孔对调"); } catch (Throwable ignored) { }
        try {
            if (b > 0) EmberGemSeatLive.maybeAfterSeat(p, pd, b, loadouts);
            if (a > 0) EmberGemSeatLive.maybeAfterSeat(p, pd, a, loadouts);
        } catch (Throwable ignored) { }
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
        try {
            PlayerData pdRm = plugin.getDataStore().get(p.getUniqueId());
            EmberGemRemovePathSync.maybeSync(p, pdRm, code); // D12033
        } catch (Throwable ignored) { }
        // D862 re-insert chase
        ConfirmTokens.sendButtons(p, P + "§7可再嵌：",
                new String[]{"[嵌回孔" + hole + "]", "/corerpg p1 gem insert " + hole + " " + EmberGemPath.key(code),
                        "嵌回刚拆的石", "GREEN"},
                EmberForgeCommitNext.enhance(),
                new String[]{"[宝石列表]", "/corerpg p1 gem list", "换石", "AQUA"});
        return true;
    }


    /** TIP-BC: Ready cue for Open/list; peek only — never calls unlocked(). */
    static String unlockSoftListTail(PlayerData pd) {
        if (EmberGemHoleUnlockSoft.ready(pd)) {
            return " · " + EmberGemHoleUnlockSoft.CUE;
        }
        return "";
    }

    /** TIP-BC: Insert reject copy — ready → soft thresholds; used → hard table. */
    static String unlockRejectLine(PlayerData pd) {
        if (EmberGemHoleUnlockSoft.ready(pd)) {
            return "该孔未解锁（今日门槛−1：刃二孔≥4 · 护符≥2；表额≥5/≥3）";
        }
        return "该孔未解锁（刃第二孔需强化≥5 · 护符孔需强化≥3）";
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
