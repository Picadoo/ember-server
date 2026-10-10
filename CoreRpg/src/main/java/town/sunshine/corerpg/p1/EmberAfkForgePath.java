package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;

/**
 * D688: when AFK daily kill cap fills — suggest forge sink from vault+inv remain.
 * Zero AFK table / price / yield change. Chat+ActionBar only — no button twin spam.
 */
public final class EmberAfkForgePath {

    /** Rising-edge latch per day: 1 = already told today. */
    public static final String C_TOLD = "p1_afk_forge_path_told";

    private EmberAfkForgePath() {}

    public static String suggestLine(int shard, int bone, int blank, int coin, int forgeGoal) {
        if (forgeGoal == EmberForgeGoal.ENHANCE) {
            if (shard >= 12) return "§a挂机日满 · 仓碎片够强化 · /corerpg p1 forge";
            return "§e挂机日满 · 还差碎片强化 · 仓碎 §f" + shard + " §7· /corerpg p1 forge";
        }
        if (forgeGoal == EmberForgeGoal.REFINE) {
            if (blank >= 1 && bone >= 2) return "§a挂机日满 · 胚/骨够精工 · /corerpg p1 refine";
            return "§e挂机日满 · 精工还差料 · 胚§f" + blank + " §7骨§f" + bone + " §7· /corerpg p1 forge";
        }
        if (forgeGoal == EmberForgeGoal.UPGRADE) {
            EmberUpgradeRules.Cost uc = EmberUpgradeRules.upgradeCost(1);
            int needS = uc == null ? 60 : uc.shards;
            int needC = uc == null ? 12 : uc.cores;
            int needB = uc == null ? 6 : uc.blanks;
            // bone/coin unused; suggest uses shard as primary signal + blank
            if (shard >= needS && blank >= needB)
                return "§a挂机日满 · 料够升阶 · /corerpg p1 upgrade";
            return "§e挂机日满 · 升阶还差料 · 碎§f" + shard + " §7胚§f" + blank
                    + " §7· /corerpg p1 forge";
        }
        if (forgeGoal == EmberForgeGoal.LOCK) {
            // D2050: rough T1 lock cost display (shard+lock_shard)
            EmberGrowthService gs = EmberGrowthService.get();
            EmberAffix.Rules rr = gs == null ? null : gs.reroll();
            int need = rr == null ? 20 : (rr.shardFor(1) + rr.lockShardFor(1));
            if (shard >= need) return "§a挂机日满 · 碎够锁定洗 · /corerpg p1 reroll from forge";
            return "§e挂机日满 · 锁定洗还差碎 §f" + Math.max(0, need - shard)
                    + " §7· /corerpg p1 reroll";
        }
        if (forgeGoal == EmberForgeGoal.GEM) {
            EmberUpgradeRules.Cost gc = EmberGemAcquire.craftCost();
            if (shard >= gc.shards && bone >= gc.bone && coin >= gc.coins)
                return "§a挂机日满 · 料够合成宝石 · /corerpg p1 gem craft sharp";
            return "§e挂机日满 · 宝石还差料 · 碎§f" + shard + " §7骨§f" + bone
                    + " §7币§f" + coin + " §7· /ember_p1_gem";
        }
        if (forgeGoal == EmberForgeGoal.QUALITY) {
            // D1932: mirror qualityCost(0) thresholds loosely for suggest (display only)
            EmberUpgradeRules.Cost qc = EmberUpgradeRules.qualityCost(0);
            int needB = qc == null ? 8 : qc.blanks;
            int needO = qc == null ? 8 : qc.bone;
            int needC = qc == null ? 800 : qc.coins;
            if (blank >= needB && bone >= needO && coin >= needC)
                return "§a挂机日满 · 料够成色 · /corerpg p1 quality";
            return "§e挂机日满 · 成色还差料 · 胚§f" + blank + " §7骨§f" + bone
                    + " §7币§f" + coin + " §7· /corerpg p1 quality";
        }
        if (forgeGoal == EmberForgeGoal.BRAND) {
            if (shard >= EmberBrandRules.craftShards())
                return "§a挂机日满 · 碎够合成烙纹 · /corerpg p1 brand craft";
            return "§e挂机日满 · 烙纹还差碎 §f" + Math.max(0, EmberBrandRules.craftShards() - shard)
                    + " §7· /corerpg p1 brand menu";
        }
        if (shard >= 12) return "§a挂机日满 · 碎片够强化 · /corerpg p1 enhance";
        if (blank >= 1 && bone >= 2) return "§a挂机日满 · 胚/骨够精工 · /corerpg p1 refine";
        if (shard >= EmberGemAcquire.CRAFT_SHARD && bone >= EmberGemAcquire.CRAFT_BONE && coin >= EmberGemAcquire.CRAFT_COIN)
            return "§a挂机日满 · 够合成宝石 · /ember_p1_gem";
        if (shard > 0 || blank > 0 || bone > 0)
            return "§e挂机日满 · 料在仓 · 打开工坊花 · /corerpg p1 forge";
        return "§e挂机日满 · 去冒险或短征 · /ember_p1_adventure";
    }

    /** Rising-edge once per day when remain hits 0. */
    public static boolean maybeTell(Player p, PlayerData d, NiBridge ni, EmberVault vault) {
        if (p == null || d == null) return false;
        String day = town.sunshine.corerpg.DailyService.today();
        if (d.periodCount(C_TOLD, day) > 0) return false;
        d.addPeriodCount(C_TOLD, day, 1);
        int shard = mat(p, ni, vault, EmberUpgradeRules.MAT_SHARD);
        int bone = mat(p, ni, vault, EmberUpgradeRules.MAT_BONE);
        int blank = mat(p, ni, vault, EmberUpgradeRules.MAT_BLANK);
        int coin = d.getCoin();
        String line = suggestLine(shard, bone, blank, coin, EmberForgeGoal.get(d));
        p.sendMessage(EmberRunService.P + line);
        try { p.sendActionBar(line); } catch (Throwable ignored) { } // D1741 same remain line
        return true;
    }

    /** Vault+inv via NiBridge ExtraSource (no double-count). */
    static int mat(Player p, NiBridge ni, EmberVault vault, String id) {
        if (ni != null) return ni.countInInventory(p, id);
        if (vault != null && p != null) return (int) Math.min(Integer.MAX_VALUE, vault.amount(p.getUniqueId(), id));
        return 0;
    }

    public static String followHint() { return "挂机日满→工坊"; }
}
