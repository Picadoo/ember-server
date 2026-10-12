package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.PlayerData;

/**
 * D680/D681: economy sink visibility — forge glance for held piece.
 * TIP-AM: armor early-return ≡ ARMOR_REFUSE (± dismantle half); never fake quality/refine/upgrade costs.
 * TIP-AS D12798: non-armor feeds soft-aware GearNextHint Lines + soft lack peek.
 * TIP-AT D12814: BRAND/CONVERT/ROLL lack soft peek（LOCK/甲不动）.
 * Not a PAPI hope-meter twin. Zero price / AFK / yield change.
 */
public final class EmberSinkGlance {

    private EmberSinkGlance() {}

    public static void tellHeld(Player p, EmberForgeService forge, EmberLoadoutService loadouts) {
        tellHeld(p, forge, loadouts, null);
    }

    public static void tellHeld(Player p, EmberForgeService forge, EmberLoadoutService loadouts, PlayerData pd) {
        if (p == null) return;
        String P = EmberRunService.P;
        ItemStack hand = p.getInventory().getItemInMainHand();
        EmberItemData d = null;
        if (loadouts != null && hand != null) {
            EmberItems.Read r = loadouts.items().read(hand);
            if (r != null && r.ok()) d = r.data;
        }
        if (d == null) {
            p.sendMessage(P + "§8消耗一瞥：手持余烬件可看强化/成色/精工本次费用与缺料");
            return;
        }
        // TIP-AM D12700/D12701: armor ≡ ARMOR_REFUSE; never qualityLine/craftLine/upgradeLine / quality·refine lackHalf
        if (shouldSkipForgeCosts(d)) {
            p.sendMessage(P + "§e" + armorHonestChat(d));
            try {
                p.sendActionBar("§e" + armorHonestActionBar(d));
            } catch (Throwable ignored) { }
            return;
        }
        List<String> bits = costBits(d, pd);
        int goal = EmberForgeGoal.get(pd);
        String lack = "";
        String focus = "强化";
        if (forge != null) {
            EmberUpgradeRules.Cost cost = null;
            if (goal == EmberForgeGoal.REFINE) {
                cost = EmberGearNextHint.softCost("refine", pd, EmberUpgradeRules.refineCost(d.craft));
                focus = "精工";
            } else if (goal == EmberForgeGoal.ROLL) {
                focus = "随机锻";
                // D1200: ordinary roll matCost（非 floor · 禁 K3）；TIP-AT soft peek
                cost = EmberGearNextHint.softCost("roll", pd, EmberForgeRollRules.matCost());
            } else if (goal == EmberForgeGoal.CONVERT) {
                focus = "转化";
                // D1216: weekly convert cost by held tier；TIP-AT soft peek
                if (d.tier >= 1 && d.tier <= 3)
                    cost = EmberGearNextHint.softCost("convert", pd, EmberConvertRules.cost(d.tier));
            } else if (goal == EmberForgeGoal.BRAND) {
                focus = "烙纹";
                // D1192: craftShards mirror · 非 K3；TIP-AT soft peek
                cost = EmberGearNextHint.softCost("brand", pd,
                        new EmberUpgradeRules.Cost(EmberBrandRules.craftShards(), 0, 0, 0, 0));
            } else {
                EmberUpgradeRules.Plan plan = EmberUpgradeRules.enhanceCheck(d);
                if (plan != null && plan.ok()) cost = EmberGearNextHint.softCost("enhance", pd, plan.cost);
                focus = goal == EmberForgeGoal.ENHANCE ? "强化(目标)" : "强化";
            }
            if (cost != null) lack = EmberGearNextHint.lackHalf(forge.lackingFor(p, cost));
            // D1176: always surface 成色/精工缺料 honesty（稳线 · 非 K3）；TIP-AS soft peek
            EmberUpgradeRules.Cost qc = EmberGearNextHint.softCost("quality", pd, EmberUpgradeRules.qualityCost(d.quality));
            if (qc != null) {
                String ql = EmberGearNextHint.lackHalf(forge.lackingFor(p, qc));
                if (!ql.isEmpty()) bits.add("成色升" + ql);
            }
            EmberUpgradeRules.Cost rc = EmberGearNextHint.softCost("refine", pd, EmberUpgradeRules.refineCost(d.craft));
            if (rc != null && goal != EmberForgeGoal.REFINE) {
                String rl = EmberGearNextHint.lackHalf(forge.lackingFor(p, rc));
                if (!rl.isEmpty()) bits.add("精工" + rl);
            }
        }
        String goalTag = EmberForgeGoal.valid(goal) ? " §8（目标:" + EmberForgeGoal.label(goal) + "）" : "";
        p.sendMessage(P + "§e消耗一瞥 · §f" + d.shortLabel() + " §7｜ "
                + String.join(" §8·§7 ", bits)
                + (lack.isEmpty() ? "" : " §c" + lack) + goalTag);
        try {
            p.sendActionBar("§e消耗 · §f" + focus
                    + (lack.isEmpty() ? " §a料够" : " §c" + lack));
        } catch (Throwable ignored) { }
    }

    /**
     * TIP-AM: true when held piece is armor — skip all forge cost projection.
     * Package-visible for unit tests.
     */
    static boolean shouldSkipForgeCosts(EmberItemData d) {
        return d != null && d.isArmor();
    }

    /**
     * TIP-AM DESIGN §2.3: armor honest chat body (no § / no P prefix).
     * Contains ARMOR_REFUSE; optional dismantle half-line; never quality/refine/upgrade cost shapes.
     */
    static String armorHonestChat(EmberItemData d) {
        if (d == null || !d.isArmor()) return null;
        String half = "";
        if (EmberUpgradeRules.dismantleCheck(d) == null
                && EmberUpgradeRules.armorDismantleTenths(d) > 0) {
            half = " · 分解零头 · /corerpg p1 dismantle";
        }
        return "消耗一瞥 · " + d.shortLabel() + " ｜ " + EmberUpgradeRules.ARMOR_REFUSE + half;
    }

    /**
     * TIP-AM: armor ActionBar body (no § prefix). Honest — no focus/lack cost projection.
     */
    static String armorHonestActionBar(EmberItemData d) {
        if (d == null || !d.isArmor()) return null;
        if (EmberUpgradeRules.dismantleCheck(d) == null
                && EmberUpgradeRules.armorDismantleTenths(d) > 0) {
            return "消耗 · 护甲随护符 · 分解零头";
        }
        return "消耗 · 护甲随护符";
    }

    /**
     * TIP-AM: blade/charm cost-line bits (no lackHalf / no forge). Used by tellHeld + blade regression.
     * Never call for armor (caller gates with {@link #shouldSkipForgeCosts}).
     */
    static List<String> costBits(EmberItemData d) {
        return costBits(d, null);
    }

    /** TIP-AS D12798: soft-aware Line bits when pd present. */
    static List<String> costBits(EmberItemData d, PlayerData pd) {
        List<String> bits = new ArrayList<String>(4);
        if (d == null) return bits;
        String enh = EmberGearNextHint.enhanceLine(d, pd);
        if (enh != null && !enh.isEmpty()) bits.add("强化 " + enh);
        String q = EmberGearNextHint.qualityLine(d, pd);
        if (q != null && !q.isEmpty()) bits.add(q);
        String c = EmberGearNextHint.craftLine(d, pd);
        if (c != null && !c.isEmpty()) bits.add(c);
        // D1224: 升阶费用近档（gateOk=true 只示费用；闸文案仍走升阶预览）
        String up = EmberGearNextHint.upgradeLine(d, true, pd);
        if (up != null && !up.isEmpty() && !up.startsWith("已是") && !up.startsWith("T0")) bits.add("升阶 " + up);
        return bits;
    }

    public static String followHint() {
        return "消耗一瞥";
    }
}
