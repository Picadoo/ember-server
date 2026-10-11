package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.ConfirmTokens;

import java.util.Locale;

import org.bukkit.entity.Player;


import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * D608 Gem Stage3: clear-pity + deterministic craft. No AFK/sx/MM drop table raises; no combat flat change.
 */
public final class EmberGemAcquire {

    public static final String C_PITY = "p1_gem_clear_pity";
    public static final int PITY_NEED = 5;

    public static final int CRAFT_SHARD = 8;
    public static final int CRAFT_BONE = 4;
    public static final int CRAFT_COIN = 40;

    public static final String P = EmberRunService.P;

    private EmberGemAcquire() {}

    public static Cost craftCost() {
        // D948: craft amounts from economy yml SoT (CRAFT_* stay golden mirrors for tests)
        return new Cost(EmberEconomy.amount("C24", "shard"), 0, 0,
                EmberEconomy.amount("C24", "bone"), EmberEconomy.amount("C24", "coin"));
    }

    public static int pity(PlayerData d) {
        return d == null ? 0 : Math.max(0, d.periodCount(C_PITY, "all"));
    }

    public static String hopeLine(PlayerData d) {
        return EmberHopeMeter.gemPity(pity(d), PITY_NEED);
    }

    /** D1258: clears remaining until gem (incl. current progress already counted). */
    public static int clearsLeft(int cur) {
        int c = Math.max(0, cur);
        if (c >= PITY_NEED) return 0;
        return PITY_NEED - c;
    }

    /** D1258: non-hope remain — {@code 通关保底 2/5 · 再通关3次得石}. */
    public static String remainLine(int cur) {
        int c = Math.max(0, cur);
        if (c >= PITY_NEED) return "通关保底已满 · 下次通关得石";
        int left = clearsLeft(c);
        return "通关保底 " + c + "/" + PITY_NEED + " · 再通关" + left + "次得石";
    }

    public static String remainLine(PlayerData d) {
        return remainLine(pity(d));
    }

    /** True for Q01–Q07 mainline maps (normal or challenge); excludes raid / abyss / event / short. */
    public static boolean countsForPity(EmberRunMaps.MapDef m, EmberRunSession s) {
        if (m == null || s == null) return false;
        if (m.raid || m.event || m.rush || m.shortExpedition) return false;
        if (s.abyss > 0) return false;
        String k = m.key == null ? "" : m.key.toLowerCase(Locale.ROOT);
        return k.startsWith("q0") && k.length() == 3;
    }

    /**
     * After a successful settle: +1 pity; at threshold grant one gem and reset.
     * @return true if a gem was granted this call
     */
    public static boolean onClear(Player p, PlayerData d, EmberRunMaps.MapDef m, EmberRunSession s, CoreRpgPlugin plugin) {
        if (p == null || d == null || plugin == null) return false;
        if (!countsForPity(m, s)) return false;
        d.addPeriodCount(C_PITY, "all", 1);
        try {
            int extra = EmberGemPityNudge.consumeExtra(d); // D10673
            if (extra > 0) {
                d.addPeriodCount(C_PITY, "all", extra);
                if (p.isOnline()) {
                    p.sendMessage(P + "§a宝石保底加码 · +" + extra + "（今日一次）");
                    try { p.sendActionBar("§a宝石保底加码"); } catch (Throwable ignored) { }
                }
            }
        } catch (Throwable ignored) { }
        int cur = pity(d);
        if (cur < PITY_NEED) {
            // D1259: remain honesty（非 hope celebration）
            if (p.isOnline()) {
                String rem = remainLine(cur);
                p.sendMessage(P + "§7宝石 · §f" + rem);
                try { p.sendActionBar("§e宝石 · §f" + rem); } catch (Throwable ignored) { }
            }
            return false;
        }
        // grant
        int code = EmberGemPath.get(d);
        if (!EmberGemPath.valid(code)) code = EmberGemPath.SHARP;
        String gemId = EmberGemPath.gemId(code);
        NiBridge ni = plugin.getNiBridge();
        boolean ok = ni != null && ni.giveNiItem(p, gemId, 1);
        // reset pity
        int now = pity(d);
        if (now > 0) d.addPeriodCount(C_PITY, "all", -now);
        if (p.isOnline()) {
            if (ok) {
                p.sendMessage(P + "§a宝石保底！§f" + EmberItemData.gemName(code) + " §7×1（通关 " + PITY_NEED + " 次）");
                EmberLoadoutService lo = plugin.getEmberRuns() == null ? null : plugin.getEmberRuns().loadouts();
                if (!EmberGemHoleSeat.maybeAutoSeatOnAcquire(p, code, lo, plugin)) // D4161
                    EmberGemInsertChase.afterAcquire(p, code, lo, plugin); // D727
            } else p.sendMessage(P + "§c保底石发放失败（NI）· 计数已重置，请联系管理");
        }
        return ok;
    }

    public static boolean craft(Player p, String rawGem, EmberLoadoutService loadouts, CoreRpgPlugin plugin) {
        if (p == null || plugin == null) return true;
        int code = EmberGemPath.parse(rawGem);
        if (code <= 0) {
            p.sendMessage(P + "用法：/corerpg p1 gem craft <sharp|steady|drain|gale>");
            return true;
        }
        Cost cost = craftCost();
        PlayerData softPd = plugin.getDataStore() == null ? null : plugin.getDataStore().get(p.getUniqueId());
        boolean craftSoft = false;
        try {
            Cost soft = EmberGemCraftCostSoft.peek(softPd, cost); // D10657
            if (soft != cost) { cost = soft; craftSoft = true; }
        } catch (Throwable ignored) { }
        NiBridge ni = plugin.getNiBridge();
        if (ni == null) { p.sendMessage(P + "§cNI 未就绪"); return true; }
        // check mats
        int haveS = ni.countInInventory(p, EmberUpgradeRules.MAT_SHARD);
        int haveB = ni.countInInventory(p, EmberUpgradeRules.MAT_BONE);
        if (haveS < cost.shards || haveB < cost.bone) {
            // D684 / D1299: sink visibility + AB
            p.sendMessage(P + "§c合成缺料：碎 §f" + haveS + "/" + cost.shards
                    + " §c骨 §f" + haveB + "/" + cost.bone + " §c币 §f" + cost.coins
                    + " §8（仓+背包合计）");
            try {
                int gS = Math.max(0, cost.shards - haveS);
                int gB = Math.max(0, cost.bone - haveB);
                p.sendActionBar("§c宝石合成缺料 · 碎差" + gS + " 骨差" + gB);
            } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7补料：", // D875
                    new String[]{"[冒险刷料]", "/ember_p1_adventure", "图装/材料本", "GREEN"},
                    new String[]{"[一键存入]", "/corerpg p1 stash", "仓里可能有", "AQUA"},
                    new String[]{"[工坊]", "/corerpg p1 forge", "看消耗", "GOLD"});
            return true;
        }
        PlayerData d = plugin.getDataStore().get(p.getUniqueId());
        if (d == null) { p.sendMessage(P + "§c数据未就绪"); return true; }
        if (d.getCoin() < cost.coins) {
            p.sendMessage(P + "§c币不足：§f" + d.getCoin() + "/" + cost.coins + " §8（合成消耗）");
            try { p.sendActionBar("§c宝石合成 · 还差币 §f" + (cost.coins - d.getCoin())); } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7补币：", // D875 coin
                    new String[]{"[冒险]", "/ember_p1_adventure", "刷币/卖装", "GREEN"},
                    new String[]{"[工坊分解]", "/corerpg p1 forge", "拆件换钱", "GOLD"});
            return true;
        }
        if (!ni.consumeExact(p, EmberUpgradeRules.MAT_SHARD, cost.shards)
                || !ni.consumeExact(p, EmberUpgradeRules.MAT_BONE, cost.bone)) {
            p.sendMessage(P + "§c扣除材料失败");
            return true;
        }
        // D935 / ARCH S2: C24 gem craft coin sink
        if (!EmberEconomy.spendCoin(d, "C24", cost.coins)) {
            ni.giveNiItem(p, EmberUpgradeRules.MAT_SHARD, cost.shards);
            ni.giveNiItem(p, EmberUpgradeRules.MAT_BONE, cost.bone);
            p.sendMessage(P + "§c扣币失败，材料已退");
            return true;
        }
        plugin.getDataStore().flushMutation(p.getUniqueId());
        String gemId = EmberGemPath.gemId(code);
        if (!ni.giveNiItem(p, gemId, 1)) {
            // refund mats + coin (refund allowlisted file — D935)
            ni.giveNiItem(p, EmberUpgradeRules.MAT_SHARD, cost.shards);
            ni.giveNiItem(p, EmberUpgradeRules.MAT_BONE, cost.bone);
            d.addCoin(cost.coins);
            plugin.getDataStore().flushMutation(p.getUniqueId());
            p.sendMessage(P + "§c发放宝石失败，已退回");
            return true;
        }
        if (craftSoft) {
            try {
                if (EmberGemCraftCostSoft.consume(softPd)) {
                    p.sendMessage(P + "§a合成减负 · 消耗减半（今日一次）");
                    try { p.sendActionBar("§a宝石合成减负"); } catch (Throwable ignored) { }
                }
            } catch (Throwable ignored) { }
        }
        p.sendMessage(P + "§a合成成功：§f" + EmberItemData.gemName(code) + " §7（碎" + cost.shards + "+骨" + cost.bone + "+币" + cost.coins + "）");
        try { p.sendActionBar("§a宝石合成 · §f" + EmberItemData.gemName(code)); } catch (Throwable ignored) { }
        try { EmberGemCraftShardRefund.maybeRefund(p, d, ni); } catch (Throwable ignored) { } // D8385
        // D1984 FG-G3: weekly forgegoal GEM complete
        if (EmberForgeGoal.get(d) == EmberForgeGoal.GEM) {
            p.sendMessage(P + "§a本周工坊目标完成：§f宝石合成");
            try { p.sendActionBar("§a工坊目标 · 宝石完成"); } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[换下周目标]", "/corerpg p1 forgegoal", "选强化/成色…", "GREEN"},
                    new String[]{"[再合成]", "/corerpg p1 gem craft " + EmberGemPath.key(code), "同款再做", "LIGHT_PURPLE"},
                    new String[]{"[去镶嵌]", "/ember_p1_gem", "空孔镶石", "AQUA"});
        }
        if (!EmberGemHoleSeat.maybeAutoSeatOnAcquire(p, code, loadouts, plugin)) // D4161 craft→seat
            EmberGemInsertChase.afterAcquire(p, code, loadouts, plugin); // D727 chase empty hole
        return true;
    }
}
