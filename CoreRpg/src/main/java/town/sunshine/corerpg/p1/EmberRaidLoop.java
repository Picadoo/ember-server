package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D657: raid weekly-clear acquisition feel after raid_mark lands.
 * Zero weeklyCap / stamina / loot math change. Not a PAPI meter twin.
 */
public final class EmberRaidLoop {

    private EmberRaidLoop() {}

    /**
     * @param used weekly clears after this clear
     * @param cap  weeklyCap
     * @param firstClear true when this was the raid key's first-ever clear
     */
    public static void afterWeeklyClear(Player p, EmberRunMaps.MapDef m, int used, int cap, boolean firstClear) {
        if (p == null || m == null || cap <= 0 || used <= 0) return;
        String P = EmberRunService.P;
        String name = m.name == null ? m.key : m.name;
        if (used >= cap) {
            p.sendMessage(P + "§d本周团本额度满 · §f" + used + "/" + cap + " §7· " + name
                    + " §8（周一 0 点重置）");
            try { p.sendActionBar("§d团本周额度满 · §f" + used + "/" + cap); } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[冒险页]", "/ember_p1_adventure", "主线/挑战/深渊", "GREEN"},
                    new String[]{"[工坊]", "/corerpg p1 forge", "花掉落材料", "GOLD"},
                    new String[]{"[稍后再说]", null, "下周再打团本", "GRAY"});
        } else {
            String line = "§d团本入账 · §f" + name + " §7本周 §e" + used + "/" + cap
                    + (firstClear ? " §a· 首通" : "");
            try { p.sendActionBar(line); } catch (Throwable ignored) { }
            p.sendMessage(P + line + " §8（通关才扣周次数）");
        }
        PlayerData d = null;
        try {
            org.bukkit.plugin.Plugin pl = org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
            if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin)
                d = ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().get(p.getUniqueId());
        } catch (Throwable ignored) { }
        EmberRaidPath.afterClearHit(p, d, m.key); // D752
    }

    public static String chatLine(int used, int cap) {
        if (cap <= 0) return "";
        return used >= cap
                ? "§d本周团本额度满 · §f" + used + "/" + cap
                : "§d团本入账 · 本周 §e" + used + "/" + cap;
    }
}
