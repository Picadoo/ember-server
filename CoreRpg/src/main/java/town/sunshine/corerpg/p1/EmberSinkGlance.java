package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.PlayerData;

/**
 * D680/D681: economy sink visibility — forge glance for held piece.
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
        List<String> bits = new ArrayList<String>(4);
        String enh = EmberGearNextHint.enhanceLine(d);
        if (enh != null && !enh.isEmpty()) bits.add("强化 " + enh);
        String q = EmberGearNextHint.qualityLine(d);
        if (q != null && !q.isEmpty()) bits.add(q);
        String c = EmberGearNextHint.craftLine(d);
        if (c != null && !c.isEmpty()) bits.add(c);

        int goal = EmberForgeGoal.get(pd);
        String lack = "";
        String focus = "强化";
        if (forge != null) {
            EmberUpgradeRules.Cost cost = null;
            if (goal == EmberForgeGoal.REFINE) {
                cost = EmberUpgradeRules.refineCost(d.craft);
                focus = "精工";
            } else {
                EmberUpgradeRules.Plan plan = EmberUpgradeRules.enhanceCheck(d);
                if (plan != null && plan.ok()) cost = plan.cost;
                focus = goal == EmberForgeGoal.ENHANCE ? "强化(目标)" : "强化";
            }
            if (cost != null) lack = EmberGearNextHint.lackHalf(forge.lackingFor(p, cost));
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

    public static String followHint() {
        return "消耗一瞥";
    }
}
