package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * D680: economy sink visibility — one forge glance for held piece costs/lacking.
 * Not a PAPI hope-meter twin. Zero price / AFK / yield change.
 */
public final class EmberSinkGlance {

    private EmberSinkGlance() {}

    /** Open-forge / command glance. */
    public static void tellHeld(Player p, EmberForgeService forge, EmberLoadoutService loadouts) {
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
        String lack = "";
        if (forge != null) {
            EmberUpgradeRules.Plan plan = EmberUpgradeRules.enhanceCheck(d);
            if (plan != null && plan.ok() && plan.cost != null) {
                lack = EmberGearNextHint.lackHalf(forge.lackingFor(p, plan.cost));
            }
        }
        p.sendMessage(P + "§e消耗一瞥 · §f" + d.shortLabel() + " §7｜ "
                + String.join(" §8·§7 ", bits)
                + (lack.isEmpty() ? "" : " §c" + lack));
        try {
            p.sendActionBar("§e消耗 · §f" + d.shortLabel()
                    + (lack.isEmpty() ? " §a料够开强化" : " §c" + lack));
        } catch (Throwable ignored) { }
    }

    public static String followHint() {
        return "消耗一瞥";
    }
}
