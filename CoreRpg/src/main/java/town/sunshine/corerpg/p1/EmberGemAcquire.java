package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;

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
        return new Cost(CRAFT_SHARD, 0, 0, CRAFT_BONE, CRAFT_COIN);
    }

    public static int pity(PlayerData d) {
        return d == null ? 0 : Math.max(0, d.periodCount(C_PITY, "all"));
    }

    public static String hopeLine(PlayerData d) {
        return EmberHopeMeter.gemPity(pity(d), PITY_NEED);
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
        int cur = pity(d);
        if (cur < PITY_NEED) {
            if (p.isOnline()) p.sendMessage(P + "§7宝石希望 " + EmberHopeMeter.gemPity(cur, PITY_NEED));
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
            if (ok) p.sendMessage(P + "§a宝石保底！§f" + EmberItemData.gemName(code) + " §7×1（通关 " + PITY_NEED + " 次）· " + hopeLine(d));
            else p.sendMessage(P + "§c保底石发放失败（NI）· 计数已重置，请联系管理");
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
        NiBridge ni = plugin.getNiBridge();
        if (ni == null) { p.sendMessage(P + "§cNI 未就绪"); return true; }
        // check mats
        if (ni.countInInventory(p, EmberUpgradeRules.MAT_SHARD) < cost.shards
                || ni.countInInventory(p, EmberUpgradeRules.MAT_BONE) < cost.bone) {
            p.sendMessage(P + "§c缺少材料：碎片×" + cost.shards + " + 骨尘×" + cost.bone + " + 币 " + cost.coins);
            return true;
        }
        PlayerData d = plugin.getDataStore().get(p.getUniqueId());
        if (d == null) { p.sendMessage(P + "§c数据未就绪"); return true; }
        if (d.getCoin() < cost.coins) {
            p.sendMessage(P + "§c币不足（需要 " + cost.coins + "）");
            return true;
        }
        if (!ni.consumeExact(p, EmberUpgradeRules.MAT_SHARD, cost.shards)
                || !ni.consumeExact(p, EmberUpgradeRules.MAT_BONE, cost.bone)) {
            p.sendMessage(P + "§c扣除材料失败");
            return true;
        }
        d.addCoin(-cost.coins);
        plugin.getDataStore().flushMutation(p.getUniqueId());
        String gemId = EmberGemPath.gemId(code);
        if (!ni.giveNiItem(p, gemId, 1)) {
            // refund
            ni.giveNiItem(p, EmberUpgradeRules.MAT_SHARD, cost.shards);
            ni.giveNiItem(p, EmberUpgradeRules.MAT_BONE, cost.bone);
            d.addCoin(cost.coins);
            plugin.getDataStore().flushMutation(p.getUniqueId());
            p.sendMessage(P + "§c发放宝石失败，已退回");
            return true;
        }
        p.sendMessage(P + "§a合成成功：§f" + EmberItemData.gemName(code) + " §7（碎" + cost.shards + "+骨" + cost.bone + "+币" + cost.coins + "）");
        // D678: craft → insert path (UI polish · zero craft price / gem flat)
        try { p.sendActionBar("§a合成 · §f" + EmberItemData.gemName(code) + " §7· 手持件镶嵌"); } catch (Throwable ignored) { }
        ConfirmTokens.sendButtons(p, P + "§7镶嵌：",
                new String[]{"[手持 list]", "/corerpg p1 gem list", "看孔位与已嵌", "GOLD"},
                new String[]{"[嵌孔0]", "/corerpg p1 gem insert 0 " + EmberGemPath.key(code), "刃/符第1孔", "GREEN"},
                new String[]{"[宝石页]", "/ember_p1_gem", "继续合成", "AQUA"});
        return true;
    }
}
